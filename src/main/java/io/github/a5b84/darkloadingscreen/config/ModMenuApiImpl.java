package io.github.a5b84.darkloadingscreen.config;

import io.github.a5b84.darkloadingscreen.Mod;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.ColorEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class ModMenuApiImpl implements ModMenuApi {

    private static final ConfigScreenFactory<?> FACTORY = FabricLoader.getInstance().isModLoaded("cloth-config2")
            ? new ConfigScreenFactoryImpl() : parent -> null;

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return FACTORY;
    }

    /** Classe interne pcq ça a l'air de permettre de pas charger les classes
     * de Cloth Config (et donc de pas faire crasher le jeu quand il y est pas) */
    private static class ConfigScreenFactoryImpl implements ConfigScreenFactory<Screen> {

        @Override
        public Screen create(Screen parent) {
            final ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(new TranslatableText("darkLoadingScreen.config.title"));

            // On garde l'ancienne config pour pouvoir enlever la nouvelle
            // après l'avoir testé (au cas où on sauvegarde pas à la fin)
            final Config oldConfig = Mod.config;

            // Champs
            final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            final ColorEntry bgField = createColorField(entryBuilder, "background", Mod.config.bg, Config.DEFAULT.bg);
            final ColorEntry barField = createColorField(entryBuilder, "bar", Mod.config.bar, Config.DEFAULT.bar);
            final ColorEntry barBgField = createColorField(entryBuilder, "barBackground", Mod.config.barBg, Config.DEFAULT.barBg);
            final ColorEntry borderField = createColorField(entryBuilder, "border", Mod.config.border, Config.DEFAULT.border);
            final ColorEntry logoField = createColorField(entryBuilder, "logo", Mod.config.logo, Config.DEFAULT.logo);

            builder.getOrCreateCategory(new LiteralText(""))
                    .addEntry(bgField)
                    .addEntry(barField)
                    .addEntry(barBgField)
                    .addEntry(borderField)
                    .addEntry(logoField)
                    .addEntry(new ButtonEntry(new TranslatableText("darkLoadingScreen.config.try"), button -> {
                        // Essai
                        Mod.config = new Config(
                                bgField.getValue(), barField.getValue(),
                                barBgField.getValue(), borderField.getValue(),
                                logoField.getValue()
                        );
                        final MinecraftClient client = MinecraftClient.getInstance();
                        client.setOverlay(new PreviewSplashScreen(
                                500, () -> Mod.config = oldConfig
                        ));
                    }));

            builder.setSavingRunnable(() -> {
                // Sauvegarde
                Mod.config = new Config(
                        bgField.getValue(), barField.getValue(),
                        barBgField.getValue(), borderField.getValue(),
                        logoField.getValue()
                );
                Mod.config.write();
            });

            // Fini
            return builder.build();
        }

        private static ColorEntry createColorField(ConfigEntryBuilder entryBuilder, String id, int value, int defaultValue) {
            return entryBuilder.startColorField(new TranslatableText("darkLoadingScreen.config." + id), value)
                    .setDefaultValue(defaultValue)
                    .build();
        }
    }
}
