package io.github.hnosmium0001.actionale.input

import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW.GLFW_PRESS

typealias Key = InputUtil.KeyCode
/**
 * Valid values: `GLFW_PRESS`, `GLFW_RELEASE`, `GLFW_REPEAT`.
 */
typealias KeyAction = Int

class KeyChord(
        val keys: Array<Key>,
        val triggerAction: KeyAction = GLFW_PRESS,
        val orderSensitive: Boolean = true
) {
    var pressed = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyChord

        if (!keys.contentEquals(other.keys)) return false
        if (triggerAction != other.triggerAction) return false
        if (orderSensitive != other.orderSensitive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = keys.contentHashCode()
        result = 31 * result + triggerAction.hashCode()
        result = 31 * result + orderSensitive.hashCode()
        return result
    }
}