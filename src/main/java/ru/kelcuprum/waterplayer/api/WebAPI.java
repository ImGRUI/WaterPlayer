package ru.kelcuprum.waterplayer.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import express.Express;
import express.middleware.CorsOptions;
import express.middleware.Middleware;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.Level;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.AlinLogger;
import ru.kelcuprum.waterplayer.WaterPlayer;
import ru.kelcuprum.waterplayer.backend.WaterPlayerAPI;
import ru.kelcuprum.waterplayer.backend.playlist.Playlist;
import ru.kelcuprum.waterplayer.frontend.gui.screens.config.PlaylistsScreen;
import ru.kelcuprum.waterplayer.frontend.localization.MusicHelper;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class WebAPI {
    public static boolean state = false;
    public static boolean corsSetting = false;
    public static AlinLogger logger = new AlinLogger("WaterPlayer/WebAPI");
    public static Express app = new Express(WaterPlayer.apiConfig.getString("hostname", "127.0.0.1"));
    public static String authKey = WaterPlayer.apiConfig.getString("AUTH_KEY", genAuthKey());
    public static void run(){
        app = new Express(WaterPlayer.apiConfig.getString("hostname", "127.0.0.1"));
        if(!corsSetting) {
            corsSetting = true;
            CorsOptions corsOptions = new CorsOptions();
            corsOptions.setOrigin("*");
            corsOptions.setAllowCredentials(true);
            corsOptions.setHeaders(new String[]{"GET", "POST"});
        }
        app.use(Middleware.cors());
        app.use((req, res) -> {
            boolean disabled = !WaterPlayer.apiConfig.getBoolean("enable", false);
            if(!req.getHeader("Authorization").isEmpty() && disabled) disabled = !(req.getHeader("Authorization").get(0).equals(authKey));
            if(disabled){
                JsonObject resp = new JsonObject();
                JsonObject error = new JsonObject();
                error.addProperty("code", 403);
                error.addProperty("codename", "Forbidden");
                error.addProperty("message", "Disabled by configs");
                resp.add("error", error);
                res.setStatus(403);
                res.json(resp);
            } else if(WaterPlayer.apiConfig.getBoolean("ENABLE.AUTH", false) && (req.getHeader("Authorization").isEmpty() || !(req.getHeader("Authorization").get(0).equals(authKey)))){
                res.json(Objects.UNAUTHORIZED);
            }
        });
        app.all("/", (req, res) -> {
            JsonObject resp = new JsonObject();
            resp.addProperty("message", "Hello, world!");
            resp.addProperty("version", FabricLoader.getInstance().getModContainer("waterplayer").get().getMetadata().getVersion().getFriendlyString());
            res.json(resp);
        });
        // ---> Player
        app.post("/state", (req, res) -> {
            if(req.getHeader("Authorization").isEmpty() || !(req.getHeader("Authorization").get(0).equals(authKey))) {
                res.setStatus(401);
                res.json(Objects.UNAUTHORIZED);
                return;
            }
            if(req.getQuery("pause") != null) WaterPlayer.player.changePaused(Boolean.parseBoolean(req.getQuery("pause")));
            if(req.getQuery("repeat") != null) WaterPlayer.player.getTrackScheduler().setRepeatStatus(parseInt(req.getQuery("repeat")));
            if(req.getQuery("volume") != null) WaterPlayer.player.getAudioPlayer().setVolume(parseInt(req.getQuery("volume")));
            if(req.getQuery("position") != null) WaterPlayer.player.setPosition(parseLong(req.getQuery("position")));
            res.json("{\"state\": \"ok\"}");
        });
        app.post("/queue", (req, res) -> {
            if(req.getHeader("Authorization").isEmpty() || !(req.getHeader("Authorization").get(0).equals(authKey))){
                res.setStatus(401);
                res.json(Objects.UNAUTHORIZED);
                return;
            }
            if(req.getQuery("reset") != null && Boolean.getBoolean(req.getQuery("reset"))) WaterPlayer.player.getTrackScheduler().reset();
            if(req.getQuery("shuffle") != null && Boolean.getBoolean(req.getQuery("shuffle"))) WaterPlayer.player.getTrackScheduler().shuffle();
            res.json("{\"state\": \"ok\"}");
        });
        // ---> Queue
        app.get("/current", (req, res) -> {
            if(WaterPlayer.apiConfig.getBoolean("ENABLE.AUTH", false) && (req.getHeader("Authorization").isEmpty() || !(req.getHeader("Authorization").get(0).equals(authKey)))){
                res.json(Objects.UNAUTHORIZED);
                return;
            }
            JsonObject resp = new JsonObject();
            AudioTrack track = WaterPlayer.player.getAudioPlayer().getPlayingTrack();
            resp.addProperty("state", track == null ? "nothing" : WaterPlayer.player.isPaused() ? "paused" : "listening");
            resp.addProperty("volume", WaterPlayer.player.getVolume());
            resp.addProperty("repeat", WaterPlayer.player.getTrackScheduler().getRepeatStatus());
            if(track == null) resp.add("track", null);
            else {
                JsonObject trackInfo = new JsonObject();
                trackInfo.addProperty("live", track.getInfo().isStream);
                trackInfo.addProperty("service", MusicHelper.getService(track));
                trackInfo.addProperty("url", track.getInfo().uri);
                trackInfo.addProperty("title", MusicHelper.getTitle(track));
                if(!MusicHelper.isAuthorNull(track)) trackInfo.addProperty("author", MusicHelper.getAuthor(track));
                trackInfo.addProperty("artwork", MusicHelper.isFile(track) || track.getInfo().artworkUrl == null ? WaterPlayerAPI.getArtwork(track) : track.getInfo().artworkUrl);

                double z = track.getInfo().isStream ? 1.0 : (double) WaterPlayer.player.getAudioPlayer().getPlayingTrack().getPosition() / WaterPlayer.player.getAudioPlayer().getPlayingTrack().getDuration();
                trackInfo.addProperty("progress", z);
                if(!track.getInfo().isStream){
                    trackInfo.addProperty("position", track.getPosition());
                    trackInfo.addProperty("duration", track.getDuration());
                }
                resp.add("track", trackInfo);
            }
            res.json(resp);
        });
        app.get("/queue", (req, res) -> {
            JsonObject resp = new JsonObject();
            JsonArray array = new JsonArray();
            boolean empty = WaterPlayer.player.getTrackScheduler().queue.getQueue().isEmpty();
            resp.addProperty("empty", empty);
            if(!empty) for(AudioTrack track : WaterPlayer.player.getTrackScheduler().queue.getQueue()){
                    JsonObject trackInfo = new JsonObject();
                    trackInfo.addProperty("live", track.getInfo().isStream);
                trackInfo.addProperty("service", MusicHelper.getService(track));
                trackInfo.addProperty("url", track.getInfo().uri);
                    trackInfo.addProperty("title", MusicHelper.getTitle(track));
                    if(!MusicHelper.isAuthorNull(track)) trackInfo.addProperty("author", MusicHelper.getAuthor(track));
                    trackInfo.addProperty("artwork", MusicHelper.isFile(track) || track.getInfo().artworkUrl == null ? WaterPlayerAPI.getArtwork(track) : track.getInfo().artworkUrl);
                    array.add(trackInfo);
                }
            resp.add("queue", array);
            res.json(resp);
        });
        // ---> Queue function
        app.get("/next", (req, res) -> {
            JsonObject resp = new JsonObject();
            if(req.getHeader("Authorization").isEmpty() || !(req.getHeader("Authorization").get(0).equals(authKey))){
                res.setStatus(401);
                res.json(Objects.UNAUTHORIZED);
            }
            else {
                boolean empty = WaterPlayer.player.getTrackScheduler().queue.getQueue().isEmpty();
                resp.addProperty("empty", empty);
                if(!empty) WaterPlayer.player.getTrackScheduler().nextTrack();
                AudioTrack track = WaterPlayer.player.getAudioPlayer().getPlayingTrack();
                if(track == null) resp.add("track", null);
                else {
                    JsonObject trackInfo = new JsonObject();
                    trackInfo.addProperty("live", track.getInfo().isStream);
                    trackInfo.addProperty("service", MusicHelper.getService(track));
                    trackInfo.addProperty("url", track.getInfo().uri);
                    trackInfo.addProperty("title", MusicHelper.getTitle(track));
                    if(!MusicHelper.isAuthorNull(track)) trackInfo.addProperty("author", MusicHelper.getAuthor(track));
                    trackInfo.addProperty("artwork", MusicHelper.isFile(track) || track.getInfo().artworkUrl == null ? WaterPlayerAPI.getArtwork(track) : track.getInfo().artworkUrl);
                    resp.add("track", trackInfo);
                }
                res.json(resp);
            }
        });
        app.get("/back", (req, res) -> {
            JsonObject resp = new JsonObject();
            if(req.getHeader("Authorization").isEmpty() || !(req.getHeader("Authorization").get(0).equals(authKey))){
                res.setStatus(401);
                res.json(Objects.UNAUTHORIZED);
            } else {
                boolean empty = WaterPlayer.player.getTrackScheduler().queue == null || WaterPlayer.player.getTrackScheduler().queue.getQueue().isEmpty();
                resp.addProperty("empty", empty);
                if(!empty) WaterPlayer.player.getTrackScheduler().backTrack();
                AudioTrack track = WaterPlayer.player.getAudioPlayer().getPlayingTrack();
                if(track == null) resp.add("track", null);
                else {
                    JsonObject trackInfo = new JsonObject();
                    trackInfo.addProperty("live", track.getInfo().isStream);
                    trackInfo.addProperty("service", MusicHelper.getService(track));
                    trackInfo.addProperty("url", track.getInfo().uri);
                    trackInfo.addProperty("title", MusicHelper.getTitle(track));
                    if(!MusicHelper.isAuthorNull(track)) trackInfo.addProperty("author", MusicHelper.getAuthor(track));
                    trackInfo.addProperty("artwork", MusicHelper.isFile(track) || track.getInfo().artworkUrl == null ? WaterPlayerAPI.getArtwork(track) : track.getInfo().artworkUrl);
                    resp.add("track", trackInfo);
                }
                res.json(resp);
            }
        });
        // ---> Playlist
        app.get("/playlists", (req, res) -> {
            JsonObject resp = new JsonObject();
            JsonArray playlistsArray = new JsonArray();
            File playlists = AlinLib.MINECRAFT.gameDirectory.toPath().resolve(WaterPlayer.getPath()+"/playlists").toFile();
            if(playlists.exists() && playlists.isDirectory()){
                for(File playlist : java.util.Objects.requireNonNull(playlists.listFiles())){
                    if(playlist.isFile() && playlist.getName().endsWith(".json")){
                        try {
                            Playlist playlistData = new Playlist(playlist.toPath());
                            JsonObject playlistJson = new JsonObject();
                            playlistJson.addProperty("id", playlistData.fileName);
                            playlistJson.add("data", playlistData.toJSON());
                            playlistsArray.add(playlistJson);
                        } catch (Exception e){
                            WaterPlayer.log(e.getLocalizedMessage(), Level.ERROR);
                        }
                    }
                }
            }
            resp.add("playlists", playlistsArray);
            res.json(resp);
        });
        app.get("/playlist/:id", (req, res) -> {
            JsonObject resp = new JsonObject();
            JsonArray playlistsArray = new JsonArray();
            File playlists = AlinLib.MINECRAFT.gameDirectory.toPath().resolve(WaterPlayer.getPath()+"/playlists/"+req.getParam("id")+".json").toFile();
            if(playlists.exists()){
                try {
                    resp = GsonHelper.parse(Files.readString(playlists.toPath()));
                } catch (Exception ex){
                    res.setStatus(500);
                    res.json(Objects.INTERNAL_SERVER_ERROR);
                    ex.printStackTrace();
                }
                res.json(resp);
            } else {
                res.setStatus(404);
                JsonObject object = Objects.NOT_FOUND;
                object.getAsJsonObject("error").addProperty("message", "Playlist not found");
                res.json(object);
            }
        });
        // ---> Not found
        app.all((req, res) -> {
            res.setStatus(404);
            res.json(Objects.NOT_FOUND);
        });
        app.listen(parseInt(WaterPlayer.apiConfig.getString("port", "2264")));
        logger.log("API Started");
        logger.log("Open: http://localhost:%s", WaterPlayer.apiConfig.getNumber("port", 2264).intValue());
        state = true;
    }
    public static void stop(){
        if(state) {
            app.stop();
            logger.log("API Stopped");
        } else logger.warn("API not running");
    }

    public static String genAuthKey(){
        return String.format("%s-%s-%s-%s", makeIDPlaylist(8), makeIDPlaylist(8), makeIDPlaylist(8), makeIDPlaylist(8));
    }
    public static String makeIDPlaylist(int length){
        StringBuilder result = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int charactersLength = characters.length();
        int counter = 0;
        while (counter < length) {
            result.append(characters.charAt((int) Math.floor(Math.random() * charactersLength)));
            counter += 1;
        }
        return result.toString();
    }

    public interface Objects {
        JsonObject NOT_FOUND = GsonHelper.parse("{\"error\":{\"code\":404,\"codename\":\"Not found\",\"message\":\"Method not found\"}}");
        JsonObject INTERNAL_SERVER_ERROR = GsonHelper.parse("{\"error\":{\"code\":500,\"codename\":\"Internal Server Error\",\"message\":\"\"}}");
        JsonObject UNAUTHORIZED = GsonHelper.parse("{\"error\": {\"code\": 401,\"codename\": \"Unauthorized\",\"message\": \"You not authorized\"}}");
        JsonObject BAD_REQUEST = GsonHelper.parse("{\"error\": {\"code\": 400,\"codename\": \"Bad Request\",\"message\": \"The required arguments are missing!\"}}");
    }
}
