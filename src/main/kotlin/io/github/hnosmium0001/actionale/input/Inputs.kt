package io.github.hnosmium0001.actionale.input

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

object InputManager {
    private val statuses: MutableMap<Int, KeyAction> = Int2IntOpenHashMap()

    fun setKeyStatus(keyCode: InputUtil.KeyCode, action: KeyAction) {
        statuses[keyCode.keyCode] = action
    }

    fun getKeyStatus(keyCode: InputUtil.KeyCode): KeyAction {
        return statuses[keyCode.keyCode] ?: GLFW_RELEASE
    }
}
