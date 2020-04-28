package io.github.hnosmium0001.actionale

import net.fabricmc.api.ClientModInitializer
import net.minecraft.util.Identifier

object Actionale : ClientModInitializer {
    const val MODID = "actionale"
    fun identifier(path: String) = Identifier(MODID, path)

    override fun onInitializeClient() {
    }
}
