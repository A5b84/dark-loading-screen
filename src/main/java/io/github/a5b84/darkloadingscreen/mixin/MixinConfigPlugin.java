package io.github.a5b84.darkloadingscreen.mixin;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.minecraft.MinecraftVersion;

/**
 * Plugin qui désactive les mixins qui sont incompatibles.
 * Les noms de mixins à version sont au format `...Mixin_a[x]b[y]`
 * avec `a[x]` (after) et `b[y]` (before) optionnels, les mixins sont
 * injectées si `x <= dataVersion < y`.
 * Liste des dataVersions :
 * https://minecraft.gamepedia.com/Data_version#List_of_data_versions
 */
public class MixinConfigPlugin implements IMixinConfigPlugin {

    /** Expression régulière qui matche les versions limites d'un mixin */
    private static final Pattern CONSTRAINT_PATTERN = Pattern.compile(".*?(?:a([0-9]+))?(?:b([0-9]+))?");

    private static final int GAME_VERSION = MinecraftVersion.create().getWorldVersion();

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        final Matcher matcher = CONSTRAINT_PATTERN.matcher(mixinClassName);
        matcher.matches(); // marche toujours
        final String afterStr = matcher.group(1);
        final String beforeStr = matcher.group(2);
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
