package ru.kelcuprum.waterplayer.frontend.gui.screens.config;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import ru.kelcuprum.alinlib.AlinLib;
import ru.kelcuprum.alinlib.config.Localization;
import ru.kelcuprum.alinlib.gui.InterfaceUtils;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonBooleanBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.button.ButtonWithIconBuilder;
import ru.kelcuprum.alinlib.gui.components.builder.editbox.EditBoxBuilder;
import ru.kelcuprum.alinlib.gui.components.text.CategoryBox;
import ru.kelcuprum.alinlib.gui.components.text.MessageBox;
import ru.kelcuprum.alinlib.gui.components.text.TextBox;
import ru.kelcuprum.alinlib.gui.screens.ConfigScreenBuilder;
import ru.kelcuprum.waterplayer.WaterPlayer;
import ru.kelcuprum.waterplayer.backend.WaterPlayerAPI;
import ru.kelcuprum.waterplayer.frontend.gui.screens.control.ControlScreen;

import static ru.kelcuprum.alinlib.gui.InterfaceUtils.Icons.*;
import static ru.kelcuprum.alinlib.gui.InterfaceUtils.Icons.LIST;

public class SecretConfigsScreen {
    private static final Component MainConfigCategory = Localization.getText("waterplayer.config");
    private static final Component LocalizationConfigCategory = Localization.getText("waterplayer.config.localization");
    private static final Component SecretConfigCategory = Localization.getText("waterplayer.secret");
    private static final Component PlaylistsCategory = Localization.getText("waterplayer.playlists");
    private static final Component PlayCategory = Localization.getText("waterplayer.play");

    private final Component tokensText = Localization.getText("waterplayer.secret.title.tokens");
    private final Component yandexMusicText = Localization.getText("waterplayer.config.yandex_music_token");
    private final Component deezerText = Localization.getText("waterplayer.config.deezer_decryption_key");
    private final Component floweryTTSText = Localization.getText("waterplayer.config.flowery_tts_voice");

    private final Component spotifyText = Localization.getText("waterplayer.secret.title.spotify");
    private final Component spotifyClientIDText = Localization.getText("waterplayer.config.spotify_client_id");
    private final Component spotifyClientSecretText = Localization.getText("waterplayer.config.spotify_client_secret");
    private final Component spotifySPDC = Localization.getText("waterplayer.config.spotify_sp_dc");
    private final Component spotifyCountryCodeText = Localization.getText("waterplayer.config.spotify_country_code");

    private final Component appleMusicText = Localization.getText("waterplayer.secret.title.apple_music");
    private final Component appleMusicMediaAPITokenText = Localization.getText("waterplayer.config.apple_music_media_api_token");
    private final Component appleMusicCountryCodeText = Localization.getText("waterplayer.config.apple_music_country_code");
    //
    public Screen build(Screen parent) {
        return new ConfigScreenBuilder(parent, Component.translatable("waterplayer.name"))
                .addPanelWidget(new ButtonWithIconBuilder(MainConfigCategory, OPTIONS, (e) -> AlinLib.MINECRAFT.setScreen(new MainConfigsScreen().build(parent))).setCentered(false).build())
                .addPanelWidget(new ButtonWithIconBuilder(LocalizationConfigCategory, LIST, (e) -> AlinLib.MINECRAFT.setScreen(new LocalizationConfigsScreen().build(parent))).setCentered(false).build())
                .addPanelWidget(new ButtonWithIconBuilder(SecretConfigCategory, WARNING, (e) -> AlinLib.MINECRAFT.setScreen(new SecretConfigsScreen().build(parent))).setCentered(false).build())
                .addPanelWidget(new ButtonWithIconBuilder(PlaylistsCategory, LIST, (e) -> AlinLib.MINECRAFT.setScreen(new PlaylistsScreen().build(parent))).setCentered(false).build())
                .addPanelWidget(new ButtonWithIconBuilder(PlayCategory, InterfaceUtils.getResourceLocation("waterplayer", "textures/player/play.png"), (e) -> AlinLib.MINECRAFT.setScreen(new ControlScreen(this.build(parent)))).setCentered(false).build())

                .addWidget(new TextBox(SecretConfigCategory, true))
                .addWidget(new ButtonWithIconBuilder(Component.translatable("waterplayer.secret.how_to_get_tokens"), InterfaceUtils.getResourceLocation("waterplayer", "textures/think.png"), (e)-> WaterPlayer.confirmLinkNow(new SecretConfigsScreen().build(parent), "https://github.com/topi314/LavaSrc?tab=readme-ov-file#usage")).build())
                .addWidget(new MessageBox(Component.translatable("waterplayer.secret.description")))
                .addWidget(new CategoryBox(tokensText)
                        .addValue(new EditBoxBuilder(yandexMusicText).setValue("").setConfig(WaterPlayer.config, "YANDEX_MUSIC_TOKEN").setSecret(true).build())
                        .addValue(new EditBoxBuilder(deezerText).setValue("").setConfig(WaterPlayer.config, "DEEZER_DECRYPTION_KEY").setSecret(true).build())
                        .addValue(new EditBoxBuilder(floweryTTSText).setValue("").setConfig(WaterPlayer.config, "FLOWERY_TTS_VOICE").build())
                )
                .addWidget(new CategoryBox(spotifyText)
                        .addValue(new EditBoxBuilder(spotifyClientIDText).setValue("").setConfig(WaterPlayer.config, "SPOTIFY_CLIENT_ID").setSecret(true).build())
                        .addValue(new EditBoxBuilder(spotifyClientSecretText).setValue("").setConfig(WaterPlayer.config, "SPOTIFY_CLIENT_SECRET").setSecret(true).build())
                        .addValue(new EditBoxBuilder(spotifySPDC).setValue("").setConfig(WaterPlayer.config, "SPOTIFY_SP_DC").setSecret(true).build())
                        .addValue(new EditBoxBuilder(spotifyCountryCodeText).setValue("US").setConfig(WaterPlayer.config, "SPOTIFY_COUNTRY_CODE").build())
                )
                .addWidget(new CategoryBox(appleMusicText)
                        .addValue(new EditBoxBuilder(appleMusicMediaAPITokenText).setValue("").setConfig(WaterPlayer.config, "APPLE_MUSIC_MEDIA_API_TOKEN").setSecret(true).build())
                        .addValue(new EditBoxBuilder(appleMusicCountryCodeText).setValue("US").setConfig(WaterPlayer.config, "APPLE_MUSIC_COUNTRY_CODE").build())
                )
                .addWidget(new CategoryBox(Component.translatable("waterplayer.api"))
                        .addValue(new ButtonWithIconBuilder(Component.translatable("waterplayer.web.what_data_is_sent"), InterfaceUtils.getResourceLocation("waterplayer", "textures/think.png"), (e) -> WaterPlayer.confirmLinkNow(new SecretConfigsScreen().build(parent), AlinLib.MINECRAFT.options.languageCode.equals("ru_ru") ? "https://waterplayer.ru/data" : "https://waterplayer.ru/data_en")).build())
                        .addValue(new ButtonBooleanBuilder(Component.translatable("waterplayer.api.enable"), true).setConfig(WaterPlayer.config, "API.ENABLE").build())
                        .addValue(new EditBoxBuilder(Component.translatable("waterplayer.api.url")).setValue("https://api.waterplayer.ru").setConfig(WaterPlayer.config, "API.URL").build())
                        .addValue(new ButtonWithIconBuilder(Component.translatable("waterplayer.api.update_configs"), RESET, (e) -> {
                            WaterPlayerAPI.loadConfig();
                            WaterPlayer.getToast().setMessage(Component.translatable("waterplayer.api.config_updated")).show(AlinLib.MINECRAFT.getToasts());
                        }).build())
                )
                .build();
    }
}
