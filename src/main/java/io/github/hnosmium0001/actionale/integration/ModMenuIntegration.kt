package io.github.hnosmium0001.actionale.integration

import io.github.hnosmium0001.actionale.Actionale
import io.github.hnosmium0001.actionale.ActionaleConfigData
import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class ActionaleModMenuIntegration : ModMenuApi {
    override fun getModId() = Actionale.MODID

    override fun getModConfigScreenFactory() =
        ConfigScreenFactory { parent -> AutoConfig.getConfigScreen(ActionaleConfigData::class.java, parent).get() }
}