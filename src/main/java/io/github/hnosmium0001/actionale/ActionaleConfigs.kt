package io.github.hnosmium0001.actionale

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.ConfigData
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config

@Config(name = Actionale.MODID)
class ActionaleConfigData : ConfigData {
    var exportNamedKeys = true
}

val modConfig: ActionaleConfigData by lazy { AutoConfig.getConfigHolder(ActionaleConfigData::class.java).config }