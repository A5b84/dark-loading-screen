package io.github.a5b84.darkloadingscreen.mixin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import io.github.a5b84.darkloadingscreen.mixin.MixinConfigPlugin.V.OptifineVersions;
import io.github.a5b84.darkloadingscreen.mixin.MixinConfigPlugin.V.Versions;
import net.fabricmc.loader.api.FabricLoader;

/** Plugin qui désactive des mixins selon la version du jeu */
public class MixinConfigPlugin implements IMixinConfigPlugin {

    public static long tt = 0;
    public static int i = 0;

    public static final Logger LOGGER = LogManager.getLogger();
    private static final ClassLoader CLASS_LOADER = MixinConfigPlugin.class.getClassLoader();

    private static final int GAME_VERSION = getGameVersion();
    private static final boolean HAS_OPTIFINE = FabricLoader.getInstance().isModLoaded("optifabric");



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
        // TODO enlever (setup pour tester)
        i++;
        tt -= System.nanoTime();
        boolean bl = test(targetClassName, mixinClassName);
        tt += System.nanoTime();
        LOGGER.info("yyyy\t" + tt + "\t" + i + "\t" + tt / i);
        return bl;
    }

    public boolean test(String targetClassName, String mixinClassName) {
        if (HAS_OPTIFINE) {
            final OptifineVersions vers = getAnnotation(mixinClassName, OptifineVersions.class);
            if (vers != null) {
                return vers.min() <= GAME_VERSION && GAME_VERSION < vers.max();
            }
        }

        final Versions vers = getAnnotation(mixinClassName, Versions.class);
        if (vers != null) {
            return vers.min() <= GAME_VERSION && GAME_VERSION < vers.max();
        }
        return true;
    }

    private <T extends Annotation> @Nullable T getAnnotation(String className, Class<T> annotation) {
        try {
            final Class<?> clazz = Class.forName(className, false, CLASS_LOADER);
            return clazz.getAnnotation(annotation);
        } catch (ClassNotFoundException e) {
            // Devrait pas arriver, mais même si ça arrive la mixin aurait pas
            // pu être injectée et ça mérite bien un crash
            throw new RuntimeException(e);
        }
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}



    /** Trucs de versions */
    public static final class V {

        private V() {}
    
        /** Versions avec des changements, voir https://minecraft.gamepedia.com/Data_version#List_of_data_versions */
        public static final int
            v19w41a = 2210,
            v20w10a = 2512,
            v20w17a = 2529,
            v20w22a = 2555;

        public static final int NO_MIN = Integer.MIN_VALUE;
        public static final int NO_MAX = Integer.MAX_VALUE;



        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        public static @interface Versions {
            public int min() default NO_MIN;
            public int max() default NO_MAX;
        }

        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        public static @interface OptifineVersions {
            public int min() default NO_MIN;
            public int max() default NO_MAX;
        }

    }

}
