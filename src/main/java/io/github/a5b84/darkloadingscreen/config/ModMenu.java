package io.github.a5b84.darkloadingscreen.config;

import io.github.a5b84.darkloadingscreen.Mod;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenu implements ModMenuApi {

    @Override
    public String getModId() {
        return Mod.ID;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigScreen(parent);
    }

}
