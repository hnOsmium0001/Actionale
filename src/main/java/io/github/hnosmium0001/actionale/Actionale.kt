package io.github.hnosmium0001.actionale

import io.github.hnosmium0001.actionale.input.KeyChordManager
import net.fabricmc.api.ModInitializer
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW.GLFW_KEY_J
import org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL

object Actionale : ModInitializer {
    const val MODID = "actionale"
    fun identifier(path: String) = Identifier(MODID, path)

    override fun onInitialize() {
        KeyChordManager.obtain(arrayOf(
            InputUtil.getKeyCode(GLFW_KEY_LEFT_CONTROL, -1),
            InputUtil.getKeyCode(GLFW_KEY_J, -1)
        )).run {
            listeners.add { chord, action ->
                println("Keychord Ctrl+J triggered as $action")
            }
        }
    }
}
