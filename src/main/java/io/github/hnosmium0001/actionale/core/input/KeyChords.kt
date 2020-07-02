package io.github.hnosmium0001.actionale.core.input

import io.github.hnosmium0001.actionale.core.IdentityHashListenerMap
import io.github.hnosmium0001.actionale.core.ListenerMap
import io.github.hnosmium0001.actionale.core.mapInPlace
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
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
    val listeners: ListenerMap<(KeyChord, InputAction) -> Unit> = IdentityHashListenerMap()
    var state: InputAction = GLFW_RELEASE
        set(value) {
            if (field != value) {
                field = value
                for ((_, callback) in listeners) {
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

/**
 * Array wrapper for content based [equals] and [hashCode]. Used as key in a [HashMap]
 *
 * @see KeyChordManager
 */
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
        return keyChords.getOrPut(wrapped) {
            val chord = KeyChord(keys)
            KeyChordTriggerer.addNodesFor(chord)
            chord
        }
    }
}

/**
 * Trigger tree for key chords, which is unique in the whole game. Key maps will be triggered from callbacks from the
 * key chords.
 */
internal object KeyChordTriggerer {
    private val root = RootTriggerNode()

    // We have multiple matching states because the need to support alternative inputs
    // For example, the player might press W, and then press space. Both of the inputs are expected to be processed
    // and the first input (key W) is expected to stay on after the jump as well.
    private val matchingStates: MutableList<TriggerNode> = ArrayList()

    fun onKeyPress(key: Key) {
        // Advance pointer on key press
        matchingStates
            // If this matching state doesn't have any corresponding children key, we leave it as is
            .mapInPlace { it.children.getOrDefault(key, it) }
            // Note that if this matching state is already "finished", setting `state` to `GLFW_PRESS` wouldn't do
            // anything because it's already pressed
            .forEach { it.chord?.state = GLFW_PRESS }

        // Try to start a new key matching state
        root.children[key]?.run {
            matchingStates += this
            chord?.state = GLFW_PRESS
        }
    }

    fun onKeyRelease(key: Key) {
        matchingStates.removeAll { state ->
            when {
                state.parent == null -> true
                state.pathContainsKey(key) -> {
                    state.releaseAllNodes()
                    true
                }
                else -> false
            }
        }
    }

    fun addNodesFor(chord: KeyChord) {
        var node: TriggerNode = root
        for (key in chord.keys) {
            val candidate = node.children.getOrPut(key) { BranchTriggerNode(key, node) }
            node = candidate
        }

        if (node.chord != null) throw IllegalArgumentException("Trying to add an already existing key chord $chord")
        node.chord = chord
    }
}

private interface TriggerNode {
    val key: Key
    val parent: TriggerNode?
    val children: MutableMap<Key, TriggerNode>
    var chord: KeyChord?

    fun pathContainsKey(key: Key): Boolean
    fun releaseAllNodes()
}

private class BranchTriggerNode(
    override val key: Key,
    override val parent: TriggerNode?
) : TriggerNode {
    override val children: MutableMap<Key, TriggerNode> = HashMap()
    override var chord: KeyChord? = null

    override fun pathContainsKey(key: Key): Boolean {
        var current: TriggerNode? = this
        while (current != null) {
            if (current.key == key) {
                return true
            }
            current = current.parent
        }
        return false
    }

    override fun releaseAllNodes() {
        var current: TriggerNode? = this
        while (current != null) {
            current.chord?.state = GLFW_RELEASE
            current = current.parent
        }
    }
}

private class RootTriggerNode : TriggerNode {
    override val key: Key = InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_UNKNOWN)
    override val parent: TriggerNode? = null
    override val children: MutableMap<Key, TriggerNode> = HashMap()
    override var chord: KeyChord? = null

    override fun pathContainsKey(key: Key) = false

    override fun releaseAllNodes() {
    }
}