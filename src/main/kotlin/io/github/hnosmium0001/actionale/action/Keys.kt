package io.github.hnosmium0001.actionale.action

import net.minecraft.client.resource.language.I18n

/**
 * Alias meant to mean GLFW's key code
 */
typealias Key = Int

enum class KeyAction(val trKey: String) {
    PRESS("key.action.press"),
    RELEASE("key.action.release"),
    BOTH("key.action.both");

    fun localize(): String = I18n.translate(trKey)
}

interface IKeyChord {
    val keys: Array<Key>
}

class KeyChord(
        override val keys: Array<Key>,
        val triggerAction: KeyAction = KeyAction.PRESS,
        val orderSensitive: Boolean = true
) : IKeyChord {
    var heldTime = -1L
    val isPressed get() = heldTime > 0L

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyChord

        if (!keys.contentEquals(other.keys)) return false

        return true
    }

    override fun hashCode(): Int {
        return keys.contentHashCode()
    }
}