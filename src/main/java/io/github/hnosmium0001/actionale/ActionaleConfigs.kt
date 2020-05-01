package io.github.hnosmium0001.actionale

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.ConfigData
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.BoundedDiscrete
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment

@Config(name = Actionale.MODID)
class ActionaleConfigData : ConfigData {
    @Comment("Whether Actionale should write named keys (when true) or GLFW keycodes (when false)")
    var exportNamedKeys = true

    @BoundedDiscrete(min = 3L, max = 8L)
    var radialMenuMinSides = 6
}

val modConfig: ActionaleConfigData by lazy { AutoConfig.getConfigHolder(ActionaleConfigData::class.java).config }