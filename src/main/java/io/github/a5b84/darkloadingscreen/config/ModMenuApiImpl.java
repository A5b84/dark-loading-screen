package io.github.a5b84.darkloadingscreen.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.ColorEntry;
import me.shedaniel.clothconfig2.gui.entries.FloatListEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import static io.github.a5b84.darkloadingscreen.Mod.config;
import static io.github.a5b84.darkloadingscreen.config.Config.DEFAULT;

public class ModMenuApiImpl implements ModMenuApi {

    private static final ConfigScreenFactory<?> FACTORY = FabricLoader.getInstance().isModLoaded("cloth-config2")
            ? new ConfigScreenFactoryImpl()
            : parent -> null;

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return FACTORY;
    }

    // Nested class in order not to load Cloth Config classes and crash
    // the game when it's not installed
    private static class ConfigScreenFactoryImpl implements ConfigScreenFactory<Screen> {

        @Override
        public Screen create(Screen parent) {
            final ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(new TranslatableText("darkLoadingScreen.config.title"));

            // Keep the old config in case the user wants to close without saving
            final Config oldConfig = config;

            // Fields
            final ConfigCategory category = builder.getOrCreateCategory(new LiteralText(""));
            final ConfigEntries entries = new ConfigEntries(builder.entryBuilder(), category);
            category.addEntry(new ButtonEntry(fieldName("preview"), button -> {
                // 'Try' button
                config = entries.createConfig();
                MinecraftClient.getInstance().setOverlay(
                        new PreviewSplashScreen(500, () -> config = oldConfig)
                );
            }));

            // Saving
            builder.setSavingRunnable(() -> {
                config = entries.createConfig();
                config.write();
            });

            // Done
            return builder.build();
        }



        /** @return a {@link Text} that indentifies a field */
        private static Text fieldName(String id) {
            return new TranslatableText("darkLoadingScreen.config.entry." + id);
        }



        /** Class that holds/handles all the fields */
        private static class ConfigEntries {

            private final ConfigEntryBuilder builder;
            private final ConfigCategory category;
            private final ColorEntry bgField, barField, barBgField, borderField, logoField;
            private final FloatListEntry fadeInField, fadeOutField;

            /** Creates all the fields and adds them to {@code category} */
            public ConfigEntries(ConfigEntryBuilder builder, ConfigCategory category) {
                this.builder = builder;
                this.category = category;

                bgField =     createColorField("background",    config.bg,     DEFAULT.bg);
                barField =    createColorField("bar",           config.bar,    DEFAULT.bar);
                barBgField =  createColorField("barBackground", config.barBg,  DEFAULT.barBg);
                borderField = createColorField("border",        config.border, DEFAULT.border);
                logoField =   createColorField("logo",          config.logo,   DEFAULT.logo);
                fadeInField =  createFadeTimeField("fadeIn",  config.fadeIn,  DEFAULT.fadeIn);
                fadeOutField = createFadeTimeField("fadeOut", config.fadeOut, DEFAULT.fadeOut);
            }



            public Config createConfig() {
                return new Config(
                        bgField.getValue(), barField.getValue(), barBgField.getValue(),
                        borderField.getValue(), logoField.getValue(),
                        fadeInField.getValue(), fadeOutField.getValue()
                );
            }



            // Methods that create entries

            private ColorEntry createColorField(String id, int value, int defaultValue) {
                final ColorEntry entry = builder.startColorField(fieldName(id), value)
                        .setDefaultValue(defaultValue)
                        .build();
                category.addEntry(entry);
                return entry;
            }

            private FloatListEntry createFadeTimeField(String id, float value, float defaultValue) {
                final FloatListEntry entry = builder.startFloatField(fieldName(id), value)
                        .setDefaultValue(defaultValue)
                        .setMin(0).setMax(Config.MAX_FADE_DURATION)
                        .build();
                category.addEntry(entry);
                return entry;
            }

        }

    }

}
