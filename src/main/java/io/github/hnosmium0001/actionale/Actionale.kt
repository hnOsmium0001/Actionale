package io.github.hnosmium0001.actionale

import io.github.hnosmium0001.actionale.config.readModData
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier

object Actionale : ClientModInitializer {
    const val MODID = "actionale"
    fun identifier(path: String) = Identifier(MODID, path)

    override fun onInitializeClient() {
        readModData()
    }
}
