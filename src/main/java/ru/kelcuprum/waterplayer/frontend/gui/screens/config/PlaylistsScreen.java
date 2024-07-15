package ru.kelcuprum.waterplayer.frontend.gui.screens.config;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.Level;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.GuiUtils;
import ru.kelcuprum.alinlib.gui.Icons;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.kelcuprum.waterplayer.WaterPlayer;
import ru.kelcuprum.waterplayer.backend.playlist.Playlist;
import ru.kelcuprum.waterplayer.frontend.gui.screens.playlist.CreatePlaylistScreen;
import ru.kelcuprum.waterplayer.frontend.gui.screens.control.ControlScreen;
import ru.kelcuprum.waterplayer.frontend.gui.screens.playlist.PlaylistScreen;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;

import static ru.kelcuprum.alinlib.gui.Icons.OPTIONS;
import static ru.kelcuprum.alinlib.gui.Icons.WARNING;
import static ru.kelcuprum.alinlib.gui.Icons.*;

public class PlaylistsScreen {
    static int assetsSize = 0;
    static boolean isLoaded = false;
    public static Screen build(Screen parent) {
        assetsSize = 0;
        File playlists = AlinLib.MINECRAFT.gameDirectory.toPath().resolve("config/WaterPlayer/playlists").toFile();

        ConfigScreenBuilder builder = new ConfigScreenBuilder(parent, Component.translatable("waterplayer.name"))
                .setOnTick((s) -> {
                    int size = 0;
                    if(playlists.exists() && playlists.isDirectory()){
                        for(File playlist : Objects.requireNonNull(playlists.listFiles())){
                            if(playlist.isFile() && playlist.getName().endsWith(".json")){
                                try {
                                    JsonObject jsonPlaylist = GsonHelper.parse(Files.readString(playlist.toPath()));
                                    new Playlist(jsonPlaylist);
                                    size++;
                                } catch (Exception e){
                                    WaterPlayer.log(e.getLocalizedMessage(), Level.ERROR);
                                }
                            }
                        }
                        if(isLoaded && (assetsSize != size)) AlinLib.MINECRAFT.setScreen(PlaylistsScreen.build(parent));
                    }
                })
                .addPanelWidget(new ButtonBuilder(Component.translatable("waterplayer.config"), (e) -> AlinLib.MINECRAFT.setScreen(MainConfigsScreen.build(parent))).setIcon(OPTIONS).setCentered(false).build())
                .addPanelWidget(new ButtonBuilder(Component.translatable("waterplayer.config.localization"), (e) -> AlinLib.MINECRAFT.setScreen(LocalizationConfigsScreen.build(parent))).setIcon(Icons.LIST).setCentered(false).build())
                .addPanelWidget(new ButtonBuilder(Component.translatable("waterplayer.secret"), (e) -> AlinLib.MINECRAFT.setScreen(SecretConfigsScreen.build(parent))).setIcon(WARNING).setCentered(false).build())
                .addPanelWidget(new ButtonBuilder(Component.translatable("waterplayer.playlists"), (e) -> AlinLib.MINECRAFT.setScreen(PlaylistsScreen.build(parent))).setIcon(Icons.LIST).setCentered(false).build())
                .addPanelWidget(new ButtonBuilder(Component.translatable("waterplayer.play"), (e) -> AlinLib.MINECRAFT.setScreen(WaterPlayer.getControlScreen(PlaylistsScreen.build(parent)))).setIcon(GuiUtils.getResourceLocation("waterplayer", "textures/player/play.png")).setCentered(false).build())
                //
                .addWidget(new TextBox(140, 5, Component.translatable("waterplayer.playlists"), true));
        if(playlists.exists() && playlists.isDirectory()){
            for(File playlist : Objects.requireNonNull(playlists.listFiles())){
                if(playlist.isFile() && playlist.getName().endsWith(".json")){
                    try {
                        Playlist playlistObject = new Playlist(playlist.toPath());
                        assetsSize++;
                        builder.addWidget(new ButtonBuilder(Component.translatable("waterplayer.playlists.value", playlistObject.title, playlistObject.author), Component.literal(playlistObject.fileName), (s) -> AlinLib.MINECRAFT.setScreen(new PlaylistScreen(PlaylistsScreen.build(parent), playlistObject.fileName))).build());
                    } catch (Exception e){
                        WaterPlayer.log(e.getLocalizedMessage(), Level.ERROR);
                    }
                }
            }
        }
        isLoaded = true;
        builder.addWidget(new ButtonBuilder(Component.translatable("waterplayer.playlist.create"), (s) -> AlinLib.MINECRAFT.setScreen(new CreatePlaylistScreen(PlaylistsScreen.build(parent)))).setIcon(ADD).build());
        return builder.build();
    }
}
