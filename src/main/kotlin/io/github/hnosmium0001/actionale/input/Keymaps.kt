package io.github.hnosmium0001.actionale.input

import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object KeymapManager {
    private val orderInsensitive: MutableSet<KeyChord> = HashSet()
    private val orderSensitive: MutableSet<KeyChord> = HashSet()

    // TODO order sensitive ones
    fun setKeyStatus(key: InputUtil.KeyCode, status: KeyAction) {
        for (keyChord in orderInsensitive) {
            val matched = keyChord.keys
                    .asSequence()
                    .all { InputManager.getKeyStatus(it) == GLFW.GLFW_PRESS }
            keyChord.pressed = matched
        }
    }
}