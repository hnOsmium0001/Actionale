package io.github.hnosmium0001.actionale

import io.github.hnosmium0001.actionale.input.InputManager
import net.minecraft.util.Identifier

object Actionale {
    const val MODID = "actionale"
    fun identifier(path: String) = Identifier(MODID, path)
}
