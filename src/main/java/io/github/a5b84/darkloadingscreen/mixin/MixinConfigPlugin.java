package io.github.a5b84.darkloadingscreen.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/** Active/désactive {@link SplashScreenMixin.OptifineOnly} et {@link SplashScreenMixin.NoOptifine}
 * selon qu'OptiFabric soit installé ou non */
public class MixinConfigPlugin implements IMixinConfigPlugin {

    private static final boolean HAS_OPTIFABRIC = FabricLoader.getInstance().isModLoaded("optifabric");

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("OptifineOnly")) return HAS_OPTIFABRIC;
        if (mixinClassName.endsWith("NoOptifine")) return !HAS_OPTIFABRIC;
        return true;
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

}
