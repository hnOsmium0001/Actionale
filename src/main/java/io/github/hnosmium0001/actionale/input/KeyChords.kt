package io.github.hnosmium0001.actionale.input

import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

/**
 * A key chord is a set of keys that needs to be pressed at the same time for the chord to be considered "pressed".
 * Different from key combinations, which allows arbitrary delay between the individual pressed.
 */
class KeyChord(val keys: Array<Key>) {
    /**
     * Listeners to be called whenever this key chord gets registered as pressed or unpressed. All listeners are called with
     * parameter `this` and the current key state.
     */
    val listeners: MutableSet<(KeyChord, InputAction) -> Unit> = HashSet()
    var state = GLFW_RELEASE
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
                .map { it.toIndicatorChar() }
                .joinToString("-", "<", ">") { it.toString() }
    }

    fun translate(): String {
        return keys.asSequence()
                .map { it.toIndicatorTranslation() }
                .joinToString("-", "<", ">") { it }
    }
}

object KeyChordManager {
    private val keyChords: MutableMap<Array<Key>, KeyChord> = HashMap()

    fun obtainKeyChord(keys: Array<Key>) = keyChords.getOrPut(keys) {
        return KeyChord(keys).also {
            TriggerTree.addNodesFor(it)
        }
    }
}
