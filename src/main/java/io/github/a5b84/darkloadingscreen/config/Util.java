package io.github.a5b84.darkloadingscreen.config;

public final class Util {

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

}
