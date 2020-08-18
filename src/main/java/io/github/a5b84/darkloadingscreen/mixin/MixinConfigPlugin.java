package io.github.a5b84.darkloadingscreen.mixin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Plugin qui désactive des mixins selon la version du jeu.
 * 
 * Le filtrage est fait avec les noms des mixins qui doivent se finir par
 * `{}o{}`, où chaque `{}` est une paire de version au format `a123b456`.
 * La paire `a123b456` couvre les versions 123 (inclus, *a*fter 123)
 * à 456 (exclus, *b*efore 456). Enlever un nombre et la lettre d'avant
 * permet d'enlever la contrainte (ex : `a123` pour toutes les versions
 * après 123, `` pour toutes les versions)
 * 
 * La première paire est celle qui est utilisée par défaut, si il y a le `o`
 * la deuxième paire est utilisée à la place.
 * 
 * Les numéros de versions sont ceux utilisés pour les mondes
 * (https://minecraft.gamepedia.com/Data_version#List_of_data_versions).
 * 
 * Les versions pas précisées sont ignorées.
 * 
 * Exemples :
 *  - `a123b456` -> versions 123 à 455
 *  - `a123` -> versions >= 123
 *  - `b456` -> versions < 456
 *  - `` -> n'importe quelle version
 *  - `a123b456oa234b567` -> versions 123 à 455 sans OptiFine, 234 à 567 avec
 *  - `oa234b567` -> n'importe quelle version sans OptiFine, versions 234 à 567 avec
 *  - `b0oa234b567` -> aucune version sans OptiFine (version min = 100), versions 234 à 567 avec
 */
public class MixinConfigPlugin implements IMixinConfigPlugin {

    /** Pattern pour une seule paire de versions */
    private static final String PAIR_PATTERN =  "(?:a([0-9]+))?(?:b([0-9]+))?";

    /** Pattern pour récupérer des versions depuis un nom de mixin */
    private static final Pattern CONSTRAINT_PATTERN = Pattern.compile(
        ".*?" // Pcq ça rajoute un /^/ devant automatiquement
        + PAIR_PATTERN
        + "(?:"
            + "o()" // Parenthèses pour savoir si c'est différent pour OptiFine
            + PAIR_PATTERN
        + ")?"
    );

    private static final int GAME_VERSION = getGameVersion();
    private static final boolean HAS_OPTIFINE = FabricLoader.getInstance().isModLoaded("optifabric");



    /** Lit la version du jeu depuis le version.json dans le jar */
    private static int getGameVersion() {
        try (
            final InputStream stream = MixinConfigPlugin.class.getResourceAsStream("/version.json");
            final Reader reader = new InputStreamReader(stream);
        ) {
            final JsonObject versions = new JsonParser().parse(reader).getAsJsonObject();
            return versions.get("world_version").getAsInt();
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("[Dark Loading Screen] Couldn't get the game version", e);
        }
    }



    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        final Matcher matcher = CONSTRAINT_PATTERN.matcher(mixinClassName);
        matcher.matches(); // Marche toujours parce que tout est optionnel

        if (HAS_OPTIFINE && matcher.group(3) != null) { // 3 -> groupe après le o
            return checkPair(matcher, 4);
        }

        return checkPair(matcher, 1);
    }

    /** Vérifie la version du jeu correspond à une paire donnée
     * @param pairStartIndex Indice du groupe du 1er nombre de la paire */
    private boolean checkPair(Matcher matcher, int pairStartIndex) {
        final String afterStr = matcher.group(pairStartIndex);
        final String beforeStr = matcher.group(pairStartIndex + 1);

        final int afterVer = afterStr == null ? GAME_VERSION - 1 : Integer.parseInt(afterStr);
        final int beforeVer = beforeStr == null ? GAME_VERSION + 1 : Integer.parseInt(beforeStr);

        return afterVer <= GAME_VERSION && GAME_VERSION < beforeVer;
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

}
