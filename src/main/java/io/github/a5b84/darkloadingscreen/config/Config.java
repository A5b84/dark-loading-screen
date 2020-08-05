package io.github.a5b84.darkloadingscreen.config;

/** Config avec tous les trucs utiles qui ont besoin d'être calculés */
public class Config extends BareConfig {

    public final int bgColor;
    public final int barColor;
    public final int borderColor;
    public final int logoColor;

    public final float bgR;
    public final float bgG;
    public final float bgB;
    public final float logoR;
    public final float logoG;
    public final float logoB;



    public Config(String bg, String bar, String border, String logo) {
        super(bg, bar, border, logo);

        if (DEFAULT != null) {
            bgColor = Util.parseColor(bgStr, DEFAULT.bgColor);
            barColor = Util.parseColor(barStr, DEFAULT.barColor);
            borderColor = Util.parseColor(borderStr, DEFAULT.borderColor);
            logoColor = Util.parseColor(logoStr, DEFAULT.logoColor);
        } else {
            // Cas à part parce que DEFAULT peut pas se référencer dans son
            // constructeur
            bgColor = Util.parseColor(bgStr);
            barColor = Util.parseColor(barStr);
            borderColor = Util.parseColor(borderStr);
            logoColor = Util.parseColor(logoStr);
        }

        bgR = ((bgColor >> 16) & 0xff) / 255f;
        bgG = ((bgColor >> 8) & 0xff) / 255f;
        bgB = (bgColor & 0xff) / 255f;
        logoR = ((logoColor >> 16) & 0xff) / 255f;
        logoG = ((logoColor >> 8) & 0xff) / 255f;
        logoB = (logoColor & 0xff) / 255f;
    }
}
