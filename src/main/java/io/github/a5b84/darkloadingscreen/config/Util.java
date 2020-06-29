package io.github.a5b84.darkloadingscreen.config;

public final class Util {

    private Util() { throw new UnsupportedOperationException(); }



    /** @throws NumberFormatException si `s` est pas une couleur au format
     * #rgb, #rrggbb ou #aarrggbb (en hexadécimal) */
    public static int parseColor(String s) throws NumberFormatException {
        // Format #rgb
        if (s.length() == 3) {
            int color = Integer.parseInt(s, 16);
            // Transformation #rgb -> #rrggbb
            return (((color & 0xf00) << 8) + ((color & 0x0f0) << 4) + (color & 0x00f)) * 0x11;
        }

        // Format #rrggbb ou #aarrggbb
        if (s.length() == 6 || s.length() == 8) {
            return Integer.parseInt(s, 16) & 0x00ffffff;
        }

        // Autres formats -> erreur
        throw new NumberFormatException("Couldn't parse color '" + s + "'");
    }

    public static int parseColor(String s, int fallback) {
        try {
            return parseColor(s);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

}
