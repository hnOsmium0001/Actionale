package io.github.hnosmium0001.actionale

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer
import net.fabricmc.api.ClientModInitializer
import net.minecraft.util.Identifier

object Actionale : ClientModInitializer {
    const val MODID = "actionale"
    fun identifier(path: String) = Identifier(MODID, path)

    override fun onInitializeClient() {
        AutoConfig.register(ActionaleConfigData::class.java, ::JanksonConfigSerializer)
    }
}