package io.github.a5b84.darkloadingscreen.config.gui.widget;

import io.github.a5b84.darkloadingscreen.config.Util;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class RgbFieldWidget extends TextFieldWidget {

    public static final int HEIGHT = 20;
    public static final int PREVIEW_WIDTH = HEIGHT;

    protected static final int SPACING = 4;
    protected static final int PREVIEW_BORDER = 0xffa0a0a0;
    //      Copié de TextFieldWidget#renderButton

    public RgbFieldWidget(TextRenderer textRenderer, int x, int y, Text text) {
        super(textRenderer, x, y, 64, HEIGHT, text);
        setMaxLength(9); // Pour laisser de la marge
    }



    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);

        if (!isVisible()) return;

        // Carré avec la couleur
        final int previewX = x + width + SPACING;

        // Contour
        drawRectangle(
            matrices,
            previewX - 1, y - 1,
            previewX + PREVIEW_WIDTH, y + height,
            PREVIEW_BORDER
        );

        // Intérieur
        try {
            final int color = getColor();
            fill(matrices, previewX, y, previewX + PREVIEW_WIDTH, y + height, color);
        } catch (NumberFormatException e) {
            // Erreur de lecture -> truc noir et rose
            final int hh = height / 2; // demi-hauteur
            final int hpw = PREVIEW_WIDTH / 2; // demi largeur de la peview
            fill(matrices, previewX, y, previewX + hpw, y + hh, 0xfff800f8);
            fill(matrices, previewX + hpw, y + hh, previewX + PREVIEW_WIDTH, y + height, 0xfff800f8);
            fill(matrices, previewX + hpw, y, previewX + PREVIEW_WIDTH, y + hh, 0xff000000);
            fill(matrices, previewX, y + hh, previewX + hpw, y + height, 0xff000000);
        }
    }

    protected int getColor() throws NumberFormatException {
        return Util.parseColor(getText()) | 0xff000000;
    }

    @Override
    public int getWidth() {
        return width + SPACING + HEIGHT;
    }



    /** Affiche les contours d'un rectangle qui passe par 2 points donnés */
    private void drawRectangle(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        fill(matrices, x1, y1, x2 + 1, y1 + 1, color); // Haut
        fill(matrices, x1, y2, x2 + 1, y2 + 1, color); // Bas
        fill(matrices, x1, y1 + 1, x1 + 1, y2, color); // Gauche
        fill(matrices, x2, y1 + 1, x2 + 1, y2, color); // Droite
    }

}
