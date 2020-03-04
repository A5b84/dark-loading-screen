package io.github.a5b84.__package__;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;
import io.github.a5b84.__package__.config.__name__Config;
import net.fabricmc.api.ClientModInitializer;

public class __name__Mod implements ClientModInitializer {

    public static final String ID = "__id__";

    @Override
    public void onInitializeClient() {
        AutoConfig.register(__name__Config.class, GsonConfigSerializer::new);
    }

}
