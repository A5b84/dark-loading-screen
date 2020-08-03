package io.github.a5b84.darkloadingscreen.config;

import javax.annotation.Nullable;

import io.github.a5b84.darkloadingscreen.Mod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class ConfigScreen extends Screen {

    protected static final int LEFT_MARGIN = 16;
    protected static final int SPACING = 8;

    private final Screen parent;

    protected final Config oldConfig = Mod.config; // Pour si on annule
    protected @Nullable Config preReloadConfig = null; // Pour quand on essaie
    //      l'écran de chargement (il appelle init à la fin donc il efface
    //      tout et y faut tout remettre)

    protected RgbFieldWidget bgField;
    protected RgbFieldWidget barField;
    protected RgbFieldWidget borderField;
    protected RgbFieldWidget logoField;

    protected ButtonWidget resetButton;
    protected ButtonWidget tryButton;
    protected ButtonWidget cancelButton;
    protected ButtonWidget saveButton;

    protected int fieldX;
    protected int fieldLabelYOffset;



    public ConfigScreen(Screen parent) {
        super(new TranslatableText("darkLoadingScreen.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        client.keyboard.enableRepeatEvents(true);

        fieldX = LEFT_MARGIN + Util.maxTextWidth(textRenderer,
            "darkLoadingScreen.config.background",
            "darkLoadingScreen.config.bar",
            "darkLoadingScreen.config.border",
            "darkLoadingScreen.config.logo"
        ) + SPACING;
        fieldLabelYOffset = (RgbFieldWidget.HEIGHT - textRenderer.fontHeight) / 2;

        // Champs
        bgField = new RgbFieldWidget(
            textRenderer,
            fieldX, 40,
            new TranslatableText("darkLoadingScreen.config.background")
        );
        children.add(bgField);
        barField = new RgbFieldWidget(
            textRenderer,
            fieldX, bgField.y + bgField.getHeight() + SPACING,
            new TranslatableText("darkLoadingScreen.config.bar")
        );
        children.add(barField);
        borderField = new RgbFieldWidget(
            textRenderer,
            fieldX, barField.y + barField.getHeight() + SPACING,
            new TranslatableText("darkLoadingScreen.config.border")
        );
        children.add(borderField);
        logoField = new RgbFieldWidget(
            textRenderer,
            fieldX, borderField.y + borderField.getHeight() + SPACING,
            new TranslatableText("darkLoadingScreen.config.logo")
        );
        children.add(logoField);

        if (preReloadConfig != null) {
            loadConfig(preReloadConfig);
            preReloadConfig = null;
        } else {
            loadConfig(oldConfig);
        }

        // Boutons
        resetButton = addButton(new ButtonWidget(
            LEFT_MARGIN, logoField.y + logoField.getHeight() + SPACING,
            80, 20,
            new TranslatableText("controls.reset"),
            button -> loadConfig(Config.DEFAULT)
        ));
        tryButton = addButton(new ButtonWidget(
            resetButton.x + resetButton.getWidth() + SPACING, resetButton.y,
            80, 20,
            new TranslatableText("darkLoadingScreen.config.try"),
            button -> test()
        ));
        cancelButton = addButton(new ButtonWidget(
            LEFT_MARGIN, resetButton.y + resetButton.getHeight() + SPACING,
            80, 20,
            ScreenTexts.CANCEL,
            button -> undoAndClose()
        ));
        saveButton = addButton(new ButtonWidget(
            cancelButton.x + cancelButton.getWidth() + SPACING, cancelButton.y,
            80, 20,
            new TranslatableText("selectWorld.edit.save"),
            button -> saveAndClose()
        ));

        // Boutons pour faire des tests
        // addButton(new ButtonWidget(
        //     width / 2, 60, 80, 20,
        //     new TranslatableText("structure_block.mode.save"),
        //     button -> getCurrentConfig().write()
        // ));

        // addButton(new ButtonWidget(
        //     width / 2, 80, 80, 20,
        //     new TranslatableText("structure_block.mode.load"),
        //     button -> loadConfig(Config.read())
        // ));
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
    public void tick() {
        super.tick();
        bgField.tick();
        barField.tick();
        borderField.tick();
        logoField.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        // Titre
        drawCenteredString(matrices, textRenderer, I18n.translate("darkLoadingScreen.config.title"), width / 2, 20, 0xffffffff);

        // Couleurs + labels
        drawStringWithShadow(
            matrices, textRenderer,
            I18n.translate("darkLoadingScreen.config.background"),
            LEFT_MARGIN, bgField.y + fieldLabelYOffset, 0xffffffff);
        bgField.render(matrices, mouseX, mouseY, delta);

        drawStringWithShadow(
            matrices, textRenderer,
            I18n.translate("darkLoadingScreen.config.bar"),
            LEFT_MARGIN, barField.y + fieldLabelYOffset, 0xffffffff);
        barField.render(matrices, mouseX, mouseY, delta);

        drawStringWithShadow(
            matrices, textRenderer,
            I18n.translate("darkLoadingScreen.config.border"),
            LEFT_MARGIN, borderField.y + fieldLabelYOffset, 0xffffffff);
        borderField.render(matrices, mouseX, mouseY, delta);

        drawStringWithShadow(
            matrices, textRenderer,
            I18n.translate("darkLoadingScreen.config.logo"),
            LEFT_MARGIN, logoField.y + fieldLabelYOffset, 0xffffffff);
        logoField.render(matrices, mouseX, mouseY, delta);
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (bgField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (barField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (borderField.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (logoField.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        final String backgroundText = bgField.getText();
        final String barText = barField.getText();
        final String borderText = borderField.getText();
        final String logoText = logoField.getText();

        super.resize(client, width, height);

        bgField.setText(backgroundText);
        barField.setText(barText);
        borderField.setText(borderText);
        logoField.setText(logoText);
    }



    public Config getCurrentConfig() {
        return new Config(
            bgField.getText(), barField.getText(), borderField.getText(),
            logoField.getText()
        );
    }

    public void loadConfig(Config config) {
        bgField.setText(config.bgStr);
        barField.setText(config.barStr);
        borderField.setText(config.borderStr);
        logoField.setText(config.logoStr);
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

}
