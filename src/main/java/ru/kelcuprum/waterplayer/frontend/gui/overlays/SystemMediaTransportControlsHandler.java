//package ru.kelcuprum.waterplayer.frontend.gui.overlays;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.network.chat.Component;
//import org.apache.logging.log4j.Level;
//import ru.kelcuprum.alinlib.AlinLib;
//import ru.kelcuprum.alinlib.api.events.client.ClientTickEvents;
//import ru.kelcuprum.waterplayer.WaterPlayer;
//import ru.kelcuprum.waterplayer.frontend.localization.MusicHelper;
//
//public class SystemMediaTransportControlsHandler implements ClientTickEvents.StartTick{
//
//    public SystemMediaTransportControlsHandler(){
//
//    }
//    @Override
//    public void onStartTick(Minecraft minecraft) {
//            boolean isLive = false;
//        boolean isPause = true;
//            try {
//                if (WaterPlayer.player.getAudioPlayer().getPlayingTrack() != null && (WaterPlayer.config.getBoolean("ENABLE_OVERLAY", true) || WaterPlayer.config.getBoolean("ENABLE_MENU_OVERLAY", true))) {
//                    isLive = WaterPlayer.player.getAudioPlayer().getPlayingTrack().getInfo().isStream;
//                    isPause = WaterPlayer.player.getAudioPlayer().isPaused();
//                    v = isLive ? 1.0 : (double) WaterPlayer.player.getAudioPlayer().getPlayingTrack().getPosition() / WaterPlayer.player.getAudioPlayer().getPlayingTrack().getDuration();
//                    //-=-=-=-
//                    Component author = Component.literal(MusicHelper.getAuthor());
//                    Component title = Component.literal(MusicHelper.getTitle());
//                    Component state = Component.literal(WaterPlayer.localization.getParsedText("{waterplayer.player.speaker_icon} {waterplayer.player.volume}% {waterplayer.format.time}{waterplayer.player.repeat_icon}"));
//                    int pos = WaterPlayer.config.getNumber("OVERLAY.POSITION", 0).intValue();
//                    int pos1 = WaterPlayer.config.getNumber("OVERLAY.POSITION", 0).intValue();
//                    int maxWidth = Math.max(AlinLib.MINECRAFT.font.width(state), (bottom ? (pos == 0 || pos == 1) : (pos1 == 0 || pos1 == 1)) ? AlinLib.MINECRAFT.getWindow().getGuiScaledWidth() / 2 : ((AlinLib.MINECRAFT.getWindow().getGuiScaledWidth() - 280) / 2) - (WaterPlayer.player.getAudioPlayer().getPlayingTrack().getInfo().artworkUrl != null || MusicHelper.isFile() ? (AlinLib.MINECRAFT.font.lineHeight + 3) * 3 : 0));
//                    //-=-=-=-
//                    texts.addAll(AlinLib.MINECRAFT.font.split(title, maxWidth));
//                    if (!MusicHelper.isAuthorNull()) texts.addAll(AlinLib.MINECRAFT.font.split(author, maxWidth));
//                    texts.addAll(AlinLib.MINECRAFT.font.split(state, maxWidth));
//                }
//            } catch (Exception ex) {
//                WaterPlayer.log(ex.getLocalizedMessage(), Level.ERROR);
//            }
//    }
//}
