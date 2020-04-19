package io.github.hnosmium0001.actionale.input

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import java.lang.IllegalArgumentException

typealias Key = InputUtil.KeyCode
typealias KeyCode = Int
typealias Scancode = Int
typealias MouseCode = Int

/**
 * Valid values: `GLFW_PRESS`, `GLFW_RELEASE`, `GLFW_REPEAT`.
 */
typealias InputAction = Int
typealias ModFlags = Int

fun Key.toIndicatorChar(): Char = 'a' // TODO
fun Key.toIndicatorTranslation(): String = I18n.translate(name)

object InputManager {
    private val mouseStatuses: MutableMap<Int, InputAction> = Int2IntOpenHashMap()
    private val statuses: MutableMap<Int, InputAction> = Int2IntOpenHashMap()

    fun setKeyStatus(keyCode: InputUtil.KeyCode, action: InputAction) {
        chooseBy(keyCode.category)[keyCode.keyCode] = action

        // Handle key chord triggering
        if (action != GLFW.GLFW_PRESS) {
            TriggerTree.clearPresses()
        } else {
            TriggerTree.onKeyPress(keyCode)
        }
    }

    fun getKeyStatus(keyCode: InputUtil.KeyCode): InputAction {
        return chooseBy(keyCode.category)[keyCode.keyCode] ?: GLFW_RELEASE
    }

    private fun chooseBy(category: InputUtil.Type) = when (category) {
        InputUtil.Type.MOUSE -> mouseStatuses
        InputUtil.Type.KEYSYM, InputUtil.Type.SCANCODE -> statuses
    }
}

/**
 * Trigger tree for key chords, which is unique in the whole game. Key maps will be triggered from callbacks from the
 * key chords.
 */
internal object TriggerTree {
    private val root = RootTriggerNode()

    private var currentMatch: TriggerNode = root
    private val pressedChords = ArrayList<KeyChord>()

    fun onKeyPress(key: InputUtil.KeyCode) {
        currentMatch = currentMatch.children.getOrDefault(key, root)
        currentMatch.chord?.state = GLFW.GLFW_PRESS
        currentMatch.chord?.run { pressedChords.add(this) }
    }

    fun clearPresses() {
        // Clear all matching progress because a key chord needs all key presses to be continuous
        currentMatch = root

        for (chord in pressedChords) {
            chord.state = GLFW_RELEASE
        }
        pressedChords.clear()
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
    override val key: Key = InputUtil.Type.KEYSYM.createFromCode(GLFW.GLFW_KEY_UNKNOWN)
    override var parent: TriggerNode? = null
    override var children: MutableMap<Key, TriggerNode> = HashMap()
    override var chord: KeyChord? = null
}
