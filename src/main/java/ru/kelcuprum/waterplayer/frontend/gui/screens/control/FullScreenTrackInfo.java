package ru.kelcuprum.waterplayer.frontend.gui.screens.control;

import com.github.topi314.lavalyrics.lyrics.AudioLyrics;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBuilder;
import ru.kelcuprum.alinlib.gui.components.buttons.Button;
import ru.kelcuprum.alinlib.gui.components.buttons.ButtonBoolean;
import ru.kelcuprum.waterplayer.WaterPlayer;
import ru.kelcuprum.waterplayer.frontend.gui.LyricsHelper;
import ru.kelcuprum.waterplayer.frontend.gui.overlays.OverlayHandler;
import ru.kelcuprum.waterplayer.frontend.gui.screens.control.components.LyricsBox;
import ru.kelcuprum.waterplayer.frontend.gui.screens.control.components.TimelineComponent;
import ru.kelcuprum.waterplayer.frontend.gui.style.AirStyle;
import ru.kelcuprum.waterplayer.frontend.localization.MusicHelper;

import static ru.kelcuprum.alinlib.gui.Colors.BLACK_ALPHA;
import static ru.kelcuprum.alinlib.gui.Colors.FORGOT;

public class FullScreenTrackInfo extends Screen {

    public Screen screen;

    public FullScreenTrackInfo(Screen screen) {
        super(Component.empty());
        this.screen = screen;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        AudioTrack track = WaterPlayer.player.getAudioPlayer().getPlayingTrack();
        if(track == null){
            guiGraphics.drawCenteredString(font, Component.translatable("waterplayer.fullscreen.empty"), width/2, height/2-font.lineHeight/2, -1);
        } else {
            AudioLyrics lyrics = LyricsHelper.getLyrics(track);

            int panelSizes = (int) (width*0.475);
            int x = (int) (((width*(lyrics != null ? 0.5 : 1))-panelSizes) / 2);

            ResourceLocation icon =  MusicHelper.getThumbnail(track);

            int iconSize = (int) Math.min(panelSizes*0.75, height*0.45);

            int iconX = x + (panelSizes-iconSize) / 2;
            int iconY = (int) ((width*0.5-panelSizes) / 2) + (panelSizes-iconSize) / 2;
            guiGraphics.blit(
                    //#if MC >= 12102
                    RenderType::guiTextured,
                    //#endif
                    icon, iconX, iconY, 0.0F, 0.0F, iconSize, iconSize, iconSize, iconSize);

            guiGraphics.drawCenteredString(font, Component.literal(MusicHelper.getTitle()), x+(panelSizes/2), iconY+iconSize+8, -1);
            guiGraphics.drawCenteredString(font, Component.translatable(MusicHelper.getAuthor()), x+(panelSizes/2), iconY+iconSize+8+font.lineHeight+4, -1);
        }
    }

    public LyricsBox lyricsBox = null;
    public TimelineComponent timeline = null;

    @Override
    protected void init() {
        initText();
        initControl();
    }
    protected void initText() {
        int panelSizes = (int) (width * 0.475);
        int x = (int) ((width * 0.5) + ((width * 0.5) - panelSizes) / 2);
        int iconHeight = (int) Math.min(panelSizes*0.75, height*0.45);
        if(lyricsBox == null) lyricsBox = addRenderableWidget(new LyricsBox(x, 5, panelSizes, height-10, null));
        else {
            lyricsBox.setPosition(x, 5);
            lyricsBox.setSize(panelSizes, height-10);
            addRenderableWidget(lyricsBox);
        }
    }

    public Button back;
    public Button next;
    public Button pause;

    protected void initControl() {
            int panelSizes = (int) (width*0.475);
            int x = (int) (((width*-panelSizes) / 2));

            int iconSize = (int) Math.min(panelSizes*0.75, height*0.45);

            int iconX = x + (panelSizes-iconSize) / 2;
            int iconY = (int) ((width*0.5-panelSizes) / 2) + (panelSizes-iconSize) / 2;

        back = (Button) addRenderableWidget(new ButtonBuilder(Component.literal("◀"), (s) -> {
            if (WaterPlayer.player.getTrackScheduler().queue.getQueue().isEmpty() && WaterPlayer.player.getAudioPlayer().getPlayingTrack() == null)
                return;
            WaterPlayer.player.getTrackScheduler().backTrack();
        }).setSize(16, iconSize).setPosition(iconX-16, iconY).setStyle(new AirStyle()).build());
        next = (Button) addRenderableWidget(new ButtonBuilder(Component.literal("▶"), (s) -> {
            if (WaterPlayer.player.getTrackScheduler().queue.getQueue().isEmpty() && WaterPlayer.player.getAudioPlayer().getPlayingTrack() == null)
                return;
            WaterPlayer.player.getTrackScheduler().nextTrack();
        }).setSize(16, iconSize).setPosition(iconX+iconSize, iconY).setStyle(new AirStyle()).build());
        pause = (Button) addRenderableWidget(new ButtonBuilder(Component.empty(), (s) -> WaterPlayer.player.changePaused()).setSize(iconSize, iconSize).setPosition(iconX, iconY).setStyle(new AirStyle()).build());

            timeline = addRenderableWidget(new TimelineComponent(x, iconY+iconSize+12+(font.lineHeight+4)*2, (int) (panelSizes*0.5), 3, true));
    }

    @Override
    public void tick() {
        AudioTrack track = WaterPlayer.player.getAudioPlayer().getPlayingTrack();
        if(track != null) {
            AudioLyrics lyrics = LyricsHelper.getLyrics(track);
            if(lyricsBox != null){
                if(lyrics == null) lyricsBox.visible = false;
                else {
                    lyricsBox.visible = true;
                    lyricsBox.setLyrics(lyrics);
                    lyricsBox.setPosition(track.getPosition());
                }
            }
            if(timeline != null){
                timeline.active = timeline.visible = true;
                int panelSizes = (int) (width*0.475);
                int x = (int) (((width*(lyrics != null ? 0.5 : 1))-panelSizes) / 2);
                int iconSize = (int) Math.min(panelSizes*0.75, height*0.45);

                int iconX = x + (panelSizes-iconSize) / 2;
                int iconY = (int) ((width*0.5-panelSizes) / 2) + (panelSizes-iconSize) / 2;
                back.setPosition(iconX-16, iconY);
                next.setPosition(iconX+iconSize, iconY);
                pause.setPosition(iconX, iconY);
                timeline.setX((int) (x+panelSizes*0.25));
            }
        } else if(timeline != null) timeline.active = timeline.visible = false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        AudioTrack track = WaterPlayer.player.getAudioPlayer().getPlayingTrack();
        super.renderBackground(guiGraphics, i, j, f);
        if(track != null) {
            AudioLyrics lyrics = LyricsHelper.getLyrics(track);
            guiGraphics.fill(0, 0, width, height, OverlayHandler.getCommonColor(track, FORGOT));
            if(lyrics != null) guiGraphics.fill(width/2, 0, width, height, 0x25000000);
        }
        guiGraphics.fill(0, 0, width, height, BLACK_ALPHA);
    }

    @Override
    public void onClose() {
        AlinLib.MINECRAFT.setScreen(screen);
    }
}
