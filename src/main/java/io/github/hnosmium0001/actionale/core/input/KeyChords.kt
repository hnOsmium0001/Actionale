package io.github.hnosmium0001.actionale.core.input

import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

/**
 * A key chord is a set of keys that needs to be pressed at the same time for the chord to be considered "pressed".
 * Different from key combinations, which allows arbitrary delay between the individual pressed.
 */
class KeyChord internal constructor(val keys: Array<out Key>) {
    /**
     * Listeners to be called whenever this key chord gets registered as pressed or unpressed. All listeners are called
     * with parameter `this` and the current key state.
     */
    val listeners: MutableSet<(KeyChord, InputAction) -> Unit> = HashSet()
    var state: InputAction = GLFW_RELEASE
        set(value) {
            if (field != value) {
                field = value
                for (callback in listeners) {
                    callback.invoke(this, value)
                }
            }
        }
    val pressed get() = state == GLFW_PRESS
    val released get() = state == GLFW_RELEASE

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

    override fun toString(): String {
        return keys.asSequence()
            .map { it.indicator }
            .joinToString("-", "<", ">") { it }
    }

    fun translate(): String {
        return keys.asSequence()
            .map { it.translation }
            .joinToString("+") { it }
    }
}

private class Keys(val array: Array<out Key>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Keys

        if (!array.contentEquals(other.array)) return false

        return true
    }

    override fun hashCode(): Int {
        return array.contentHashCode()
    }
}

object KeyChordManager {
    private val keyChords: Map<Keys, KeyChord> = HashMap()

    fun obtain(vararg keys: Key): KeyChord {
        keyChords as MutableMap
        val wrapped = Keys(keys)
        if (keyChords.containsKey(wrapped)) {
            return keyChords[wrapped] ?: error("")
        }
        val chord = KeyChord(keys)
        keyChords[wrapped] = chord
        TriggerTree.addNodesFor(chord)
        return chord
    }
}
