package io.github.a5b84.darkloadingscreen.config.gui;

import javax.annotation.Nullable;

import io.github.a5b84.darkloadingscreen.Mod;
import io.github.a5b84.darkloadingscreen.config.BareConfig;
import io.github.a5b84.darkloadingscreen.config.Config;
import io.github.a5b84.darkloadingscreen.config.gui.widget.ButtonEntry;
import io.github.a5b84.darkloadingscreen.config.gui.widget.OptionListWidget;
import io.github.a5b84.darkloadingscreen.config.gui.widget.RgbSettingEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ConfigScreen extends Screen {

    protected static final int BUTTON_WIDTH = 100;
    protected static final int BUTTON_HEIGHT = 20;
    protected static final int SPACING = 8;

    private final Screen parent;

    protected final String title = I18n.translate("darkLoadingScreen.config.title");

    protected final Config oldConfig = Mod.config; // Pour si on annule
    protected @Nullable BareConfig preReloadConfig = null; // Pour quand on
    //      essaie l'écran de chargement (il appelle init à la fin donc il
    //      efface tout et y faut tout remettre)

    protected OptionListWidget options;

    protected RgbSettingEntry bgEntry;
    protected RgbSettingEntry barEntry;
    protected RgbSettingEntry borderEntry;
    protected RgbSettingEntry logoEntry;

    protected ButtonWidget cancelButton;
    protected ButtonWidget saveButton;



    public ConfigScreen(Screen parent) {
        super(label("title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        client.keyboard.enableRepeatEvents(true);

        options = new OptionListWidget(client, width, height, 32, height - 32, 25);

        bgEntry = new RgbSettingEntry(options, label("background"), Config.DEFAULT.bgStr, textRenderer);
        barEntry = new RgbSettingEntry(options, label("bar"), Config.DEFAULT.barStr, textRenderer);
        borderEntry = new RgbSettingEntry(options, label("border"), Config.DEFAULT.borderStr, textRenderer);
        logoEntry = new RgbSettingEntry(options, label("logo"), Config.DEFAULT.logoStr, textRenderer);
        options.add(bgEntry);
        options.add(barEntry);
        options.add(borderEntry);
        options.add(logoEntry);
        options.add(new ButtonEntry(
            options, BUTTON_HEIGHT, label("try"), button -> test())
        );

        children.add(options);

        // Rechargement de la config d'avant la preview / le redimensionnement
        if (preReloadConfig != null) {
            loadConfig(preReloadConfig);
            preReloadConfig = null;
        } else {
            loadConfig(oldConfig);
        }

        // Boutons
        final int bottomButtonsY = height - (32 + BUTTON_HEIGHT) / 2;
        cancelButton = addButton(new ButtonWidget(
            (width - SPACING) / 2 - BUTTON_WIDTH, bottomButtonsY,
            BUTTON_WIDTH, BUTTON_HEIGHT,
            ScreenTexts.CANCEL,
            button -> undoAndClose()
        ));
        saveButton = addButton(new ButtonWidget(
            cancelButton.x + cancelButton.getWidth() + SPACING, bottomButtonsY,
            BUTTON_WIDTH, BUTTON_HEIGHT,
            ScreenTexts.DONE,
            button -> saveAndClose()
        ));
    }

    @Override
    public void onClose() {
        client.openScreen(parent);
    }

    @Override
    public void removed() {
        super.removed();
        client.keyboard.enableRepeatEvents(false);
    }



    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        // Titre
        drawCenteredString(matrices, textRenderer, title, width / 2, 20, 0xffffffff);

        // Options
        options.render(matrices, mouseX, mouseY, delta);
    }

    /** Version modifiée de ParentElement#mouseClicked qui désélectionne
     * comme y faut */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Element clickedElement = null;

        for (final Element child : children) {
            if (clickedElement != null) {
                if (child instanceof TextFieldWidget) {
                    ((TextFieldWidget) child).setSelected(false);
                }
            } else if (child.mouseClicked(mouseX, mouseY, button)) {
                clickedElement = child;
            }
        }

        setFocused(clickedElement);
        if (button == 0) setDragging(true);

        return clickedElement != null;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        preReloadConfig = getCurrentBareConfig();
        super.resize(client, width, height); // Efface tous les champs (init)
    }



    public BareConfig getCurrentBareConfig() {
        return new BareConfig(
            bgEntry.input.getText(), barEntry.input.getText(),
            borderEntry.input.getText(), logoEntry.input.getText()
        );
    }

    public Config getCurrentConfig() {
        return new Config(
            bgEntry.input.getText(), barEntry.input.getText(),
            borderEntry.input.getText(), logoEntry.input.getText()
        );
    }

    public void loadConfig(BareConfig config) {
        bgEntry.input.setText(config.bgStr);
        barEntry.input.setText(config.barStr);
        borderEntry.input.setText(config.borderStr);
        logoEntry.input.setText(config.logoStr);
    }

    public Config apply() {
        return Mod.config = getCurrentConfig();
    }

    public void test() {
        preReloadConfig = apply();
        client.setOverlay(
            new SplashScreen(
                client, new FakeResourceReloadMonitor(500),
                (optional) -> {}, false
            )
        );
    }

    public void saveAndClose() {
        apply();
        Mod.config.write();
        onClose();
    }

    public void undoAndClose() {
        Mod.config = oldConfig;
        onClose();
    }



    private static Text label(String subKey) {
        return new TranslatableText("darkLoadingScreen.config." + subKey);
    }

}
