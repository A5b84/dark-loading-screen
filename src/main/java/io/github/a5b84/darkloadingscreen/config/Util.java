package io.github.a5b84.darkloadingscreen.config;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;

public final class Util {

    public static final int BUTTON_HEIGHT = 20;
    public static final int SCROLLBAR_WIDTH = 6;



    private Util() {}



    /**
     * @return La couleur au format 0x00rrggbb
     * @throws NumberFormatException si `s` est pas une couleur au format
     * #rgb ou #rrggbb (en hexadécimal)
     */
    public static int parseColor(String s) throws NumberFormatException {
        // Format #rgb
        if (s.length() == 3) {
            int color = Integer.parseInt(s, 16);
            // Transformation #rgb -> #rrggbb
            return (((color & 0xf00) << 8) + ((color & 0x0f0) << 4) + (color & 0x00f)) * 0x11;
        }

        // Format #rrggbb
        if (s.length() == 6) {
            return Integer.parseInt(s, 16);
        }

        // Autres formats -> erreur
        throw new NumberFormatException("Couldn't parse color '" + s + "'");
    }

    /** @return La couleur au format 0x00rrggbb, ou fallback en cas d'erreur */
    public static int parseColor(String s, int fallback) {
        try {
            return parseColor(s);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }



    /**
     * Fonction qui appelle mouseClicked et qui désélectionne comme y faut
     * @param targetElement this
     * @param superMouseClicked super::mouseClicked (ou le mouseClicked à
     *      appeler)
     * @return Le résultat de superMouseClicked
     */
    public static boolean unselectingMouseClicked(
        ParentElement targetElement, MouseClickedMethod superMouseClicked,
        double mouseX, double mouseY, int button
    ) {
        final Element oldFocused = targetElement.getFocused();
        final boolean result = superMouseClicked.apply(mouseX, mouseY, button);

        if (oldFocused != null && oldFocused != targetElement.getFocused()) {
            // mouseClicked sur l'élément qui vient d'être désélectionné
            // pour qu'il s'affiche bien comme y faut
            oldFocused.mouseClicked(mouseX, mouseY, button);
        }

        return result;
    }

    @FunctionalInterface
    public static interface MouseClickedMethod {
        boolean apply(double mouseX, double mouseY, int button);
    }

}
