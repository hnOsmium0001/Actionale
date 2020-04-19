package io.github.hnosmium0001.actionale.testmod

import io.github.hnosmium0001.actionale.input.KeyChordManager
import io.github.hnosmium0001.actionale.input.KeyInputCallback
import io.github.hnosmium0001.actionale.input.MouseInputCallback
import net.fabricmc.api.ModInitializer
import net.minecraft.client.util.InputUtil
import org.apache.logging.log4j.LogManager
import org.lwjgl.glfw.GLFW

// TODO make fabric load this
object ActionaleTestMod : ModInitializer {
    const val MODID = "actionale_testmod"

    val logger = LogManager.getLogger(MODID)

    override fun onInitialize() {
        KeyInputCallback.BUS.register(KeyInputCallback { keyCode, scancode, action, mods ->
            logger.info("Key pressed: $keyCode, scancode: $scancode, action: $action, mods: $mods")
        })
        MouseInputCallback.BUS.register(MouseInputCallback { button, action, mods ->
            logger.info("Mouse button: $button, action: $action, mods: $mods")
        })

        val ctrlJ = KeyChordManager.obtainKeyChord(arrayOf(
                InputUtil.getKeyCode(GLFW.GLFW_KEY_LEFT_CONTROL, -1),
                InputUtil.getKeyCode(GLFW.GLFW_KEY_J, -1)
        ))
        ctrlJ.listeners.add { _, action ->
            println("Keychord Ctrl+J triggered as $action")
        }
    }
}