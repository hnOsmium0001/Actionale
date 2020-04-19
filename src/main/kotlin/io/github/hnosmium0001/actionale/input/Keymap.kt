package io.github.hnosmium0001.actionale.input

import com.google.common.base.Preconditions
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW.*

class Keymap(val combination: Array<KeyChord>) {
    var state = GLFW_RELEASE
        private set
    val pressed get() = state == GLFW_PRESS
    val released get() = state == GLFW_RELEASE

    init {
        Preconditions.checkArgument(combination.isNotEmpty())
        for (chord in combination) {
            chord.listeners += this::onChordChanged
        }
    }

    private var pressedIndex = -1
    private fun onChordChanged(chord: KeyChord, state: InputAction) {
        if (state == GLFW_RELEASE && combination.last() == chord) {
            // If the last key chord is released, this keymap must be released
            // because the last key chord must be the last to trigger and last to release
            this.state = GLFW_RELEASE
            return
        }

        if (combination[pressedIndex + 1] == chord) {
            // The next required key chord is pressed, advance index to look for the next one
            pressedIndex++
            if (pressedIndex >= combination.size) {
                // At end of the array, matched all of the key chords successfully
                this.state = state
            }
        } else {
            // Pressing streak failed, reset to beginning
            pressedIndex = -1
        }
    }
}

object KeymapManager {
    val keymaps: MutableSet<Keymap> = HashSet()

    fun onKeyInput(key: InputUtil.KeyCode, status: InputAction) {
        if (status != GLFW_PRESS) {
            TriggerTree.clearPresses()
        } else {
            TriggerTree.onKeyPress(key)
        }
    }
}

/**
 * Trigger tree for key chords, which is unique in the whole game. Key maps will be triggered from callbacks from the
 * key chords.
 */
private object TriggerTree {
    val root = RootTriggerNode()

    private var currentMatch: TriggerNode = root
    private val pressedChords = ArrayList<KeyChord>()

    fun onKeyPress(key: InputUtil.KeyCode) {
        currentMatch = currentMatch.children.getOrDefault(key, root)
        currentMatch.chord?.state = GLFW_PRESS
        currentMatch.chord?.run { pressedChords.add(this) }
    }

    fun clearPresses() {
        for (chord in pressedChords) {
            chord.state = GLFW_RELEASE
        }
        pressedChords.clear()
    }
}

interface TriggerNode {
    val key: Key
    var parent: TriggerNode?
    var children: MutableMap<Key, TriggerNode>

    var chord: KeyChord?
}

private class BranchTriggerNode(
        override val key: Key,
        override var parent: TriggerNode?
) : TriggerNode {
    override var children: MutableMap<Key, TriggerNode> = HashMap()
    override var chord: KeyChord? = null
}

private class RootTriggerNode : TriggerNode {
    override val key: Key = InputUtil.Type.KEYSYM.createFromCode(GLFW_KEY_UNKNOWN)
    override var parent: TriggerNode? = null
    override var children: MutableMap<Key, TriggerNode> = HashMap()
    override var chord: KeyChord? = null
}