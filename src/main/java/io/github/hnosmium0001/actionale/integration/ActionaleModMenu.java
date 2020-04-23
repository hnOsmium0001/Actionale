package io.github.hnosmium0001.actionale.integration;

import io.github.hnosmium0001.actionale.Actionale;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ActionaleModMenu implements ModMenuApi {

    @Override
    public String getModId() {
        return Actionale.MODID;
    }

    // TODO
//    @Override
//    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
//        return parent -> AutoConfig.getConfigScreen(Conf.class, parent).get();
//    }
}
