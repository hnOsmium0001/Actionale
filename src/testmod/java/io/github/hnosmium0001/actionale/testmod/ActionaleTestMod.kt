package io.github.hnosmium0001.actionale.testmod

import io.github.hnosmium0001.actionale.input.KeyChordManager
import io.github.hnosmium0001.actionale.input.KeyInputCallback
import io.github.hnosmium0001.actionale.input.Keymap
import io.github.hnosmium0001.actionale.input.MouseInputCallback
import net.fabricmc.api.ModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.text.LiteralText
import org.apache.logging.log4j.LogManager
import org.lwjgl.glfw.GLFW

// TODO make fabric load this
object ActionaleTestMod : ModInitializer {
    const val MODID = "actionale_testmod"

    val logger = LogManager.getLogger(MODID)

    override fun onInitialize() {
        KeyInputCallback.EVENT.register(KeyInputCallback { keyCode, scancode, action, mods ->
            logger.info("Key pressed: $keyCode, scancode: $scancode, action: $action, mods: $mods")
        })
        MouseInputCallback.EVENT.register(MouseInputCallback { button, action, mods ->
            logger.info("Mouse button: $button, action: $action, mods: $mods")
        })

        val ctrlJ = KeyChordManager.obtain(arrayOf(
                InputUtil.getKeyCode(GLFW.GLFW_KEY_LEFT_CONTROL, -1),
                InputUtil.getKeyCode(GLFW.GLFW_KEY_J, -1)
        ))
        ctrlJ.listeners.add { _, action ->
            println("Keychord Ctrl+J triggered as $action")
        }


        val ctrlW_C = Keymap(
                name = "<C-w>c",
                combination = arrayOf(
                        KeyChordManager.obtain(arrayOf(
                                InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_LEFT_CONTROL),
                                InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_W)
                        )),
                        KeyChordManager.obtain(arrayOf(
                                InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_C)
                        ))
                )
        )
        val g = KeyChordManager.obtain(arrayOf(
                InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_G)
        ))
        val gt = Keymap(
                name = "gt",
                combination = arrayOf(
                        g,
                        KeyChordManager.obtain(arrayOf(
                                InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_T)
                        ))
                )
        )
        val gT = Keymap(
                name = "gT",
                combination = arrayOf(
                        g,
                        KeyChordManager.obtain(arrayOf(
                                InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_LEFT_SHIFT),
                                InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_T)
                        ))
                )
        )

        ctrlW_C.listeners.add { _, action ->
            if (action == GLFW.GLFW_PRESS)
                MinecraftClient.getInstance().player?.sendMessage(LiteralText("<C-w>c: close current panel"))
        }
        gt.listeners.add { _, action ->
            if (action == GLFW.GLFW_PRESS)
                MinecraftClient.getInstance().player?.sendMessage(LiteralText("gt: next tab"))
        }
        gT.listeners.add { _, action ->
            if (action == GLFW.GLFW_PRESS)
                MinecraftClient.getInstance().player?.sendMessage(LiteralText("gT: previous tab"))
        }
    }
}