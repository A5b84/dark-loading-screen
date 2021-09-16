package io.github.a5b84.darkloadingscreen.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuApiImpl implements ModMenuApi {

    private static final ConfigScreenFactory<?> FACTORY = FabricLoader.getInstance().isModLoaded("cloth-config2")
            ? new ConfigScreenFactoryImpl()
            : parent -> null;

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return FACTORY;
    }

}
