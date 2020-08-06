package io.github.a5b84.darkloadingscreen.config.gui;

import io.github.a5b84.darkloadingscreen.Mod;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.SharedConstants;

public class ModMenuApiImpl implements ModMenuApi {

    @Override
    public String getModId() {
        return Mod.ID;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SharedConstants.getGameVersion().getWorldVersion() >= 2529 // 20w17a (ajout MatrixStack)
            ? parent -> new ConfigScreen(parent)
            : parent -> null;
    }

}
