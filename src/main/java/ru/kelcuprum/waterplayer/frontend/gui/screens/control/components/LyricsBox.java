package ru.kelcuprum.waterplayer.frontend.gui.screens.control.components;

import com.github.topi314.lavalyrics.lyrics.AudioLyrics;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.lwjgl.openal.AL;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.waterplayer.WaterPlayer;
import ru.kelcuprum.waterplayer.frontend.localization.MusicHelper;

import java.time.Duration;
import java.util.List;

import static com.mojang.blaze3d.pipeline.MainTarget.DEFAULT_HEIGHT;
import static ru.kelcuprum.alinlib.gui.GuiUtils.DEFAULT_WIDTH;
import static ru.kelcuprum.alinlib.gui.GuiUtils.getPositionOnStylesID;
import static ru.kelcuprum.alinlib.gui.screens.DialogScreen.replaceAlpha;

public class LyricsBox extends AbstractWidget {
    protected AudioLyrics lyrics;

    public LyricsBox(int x, int y, AudioLyrics label) {
        this(x, y, DEFAULT_WIDTH(), DEFAULT_HEIGHT, label);
    }

    ///
    public LyricsBox(int x, int y, int width, int height, AudioLyrics label) {
        super(x, y, width, height, Component.empty());
        this.lyrics = label;
        this.active = false;
    }


    @Override
    public void setX(int x) {
        super.setX(x);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
    }
    int textHeight = 0;
    int lastTextHeight = 0;
    int textSize = (AlinLib.MINECRAFT.font.lineHeight + 3);

    String[] animated = {
            "-=-=-= OoO \uD83C\uDFA7 OoO =-=-=-",
            "-=-=-=- oOo \uD83C\uDFA7 oOo -=-=-=-"
    };
    public static int lastPosition = -1;
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        String activeLine = "";
        int positionLine = Math.max(-1, lastPosition);
        int color = isSplinTrevoga(WaterPlayer.player.getAudioPlayer().getPlayingTrack()) ? 0xFF4db03e : 0xFFFFFFFF;
        if(lyrics != null && lyrics.getLines() != null){
            for(AudioLyrics.Line line : lyrics.getLines()){
                if(line != null && !(line.getDuration() == null)) {
                    int type = WaterPlayer.config.getNumber("CONTROL.LYRICS.TYPE", 0).intValue();
                    Duration pos = Duration.ofMillis(position);
                        if (pos.toMillis() >= line.getTimestamp().toMillis() && pos.toMillis() <= line.getTimestamp().toMillis() + line.getDuration().toMillis()) {
                            positionLine = lyrics.getLines().indexOf(line);
                            lastPosition = positionLine;
                            activeLine = line.getLine();
                            break;
                        }
                }
            }
        List<FormattedCharSequence> activeArgs = AlinLib.MINECRAFT.font.split(Component.empty().append(activeLine.isBlank() ? (System.currentTimeMillis() % 1000 > 500 ? animated[0] : animated[1]) : activeLine).withStyle(Style.EMPTY.withBold(true)), width);
        int y = getY()+getHeight()/2-(AlinLib.MINECRAFT.font.lineHeight*activeArgs.size()/2)-(activeArgs.isEmpty() ? 0 : 3*(activeArgs.size()-1))-AlinLib.MINECRAFT.font.lineHeight-5;
        for(int i1 = positionLine-(activeLine.isBlank() ? 0 :  1); i1>=Math.max(positionLine-(activeLine.isBlank() ? 4 :  5), 0); i1--) {
            List<FormattedCharSequence> args = AlinLib.MINECRAFT.font.split(FormattedText.of(lyrics.getLines().get(i1).getLine()), width);
            for(int i2 = args.size()-1; i2>=0; i2--){
                guiGraphics.drawCenteredString(AlinLib.MINECRAFT.font, args.get(i2), getX()+(getWidth()/2), y, replaceAlpha(color-0x70000000, 255*(i1-positionLine)/6));
                y-= (AlinLib.MINECRAFT.font.lineHeight+5);
            }
        }
            y = getY()+getHeight()/2-(AlinLib.MINECRAFT.font.lineHeight*activeArgs.size()/2)-(activeArgs.isEmpty() ? 0 : 3*(activeArgs.size()-1));
            for(FormattedCharSequence arg : activeArgs) {
                guiGraphics.drawCenteredString(AlinLib.MINECRAFT.font, arg, getX() + (getWidth() / 2), y, color);
                y+=AlinLib.MINECRAFT.font.lineHeight+3;
            }
        y+=3;
        for(int i1 = positionLine+1; i1<Math.min(positionLine+6, lyrics.getLines().size()); i1++) {
            List<FormattedCharSequence> args = AlinLib.MINECRAFT.font.split(FormattedText.of(lyrics.getLines().get(i1).getLine()), width);
            for (FormattedCharSequence arg : args) {
                guiGraphics.drawCenteredString(AlinLib.MINECRAFT.font, arg, getX() + (getWidth() / 2), y, replaceAlpha(color-0x70000000, 255*(positionLine-i1)/6));
                y += (AlinLib.MINECRAFT.font.lineHeight + 5);
            }
        }
        }

    }

    public boolean isSplinTrevoga(AudioTrack track){
        return track != null && MusicHelper.getAuthor(track).equalsIgnoreCase("сплин") && MusicHelper.getTitle(track).equalsIgnoreCase("тревога");
    }

    public long position = 0;
    public long lastPositionTrack = 0;
    public void setPosition(long position){
        this.position = position;
        if(lastPositionTrack > position){
            lastPosition = -1;
        }
        lastPositionTrack = position;
    }
    public AudioLyrics lastLyrics = null;
    public void setLyrics(AudioLyrics lyrics){
        if(lastLyrics != lyrics){
            this.lastLyrics = lyrics;
            this.lyrics = lyrics;
            this.lastPosition = -1;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
