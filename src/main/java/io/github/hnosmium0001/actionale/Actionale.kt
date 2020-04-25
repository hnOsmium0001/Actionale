package io.github.hnosmium0001.actionale

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier

object Actionale : ModInitializer {
    const val MODID = "actionale"
    fun identifier(path: String) = Identifier(MODID, path)

    override fun onInitialize() {
    }
}
