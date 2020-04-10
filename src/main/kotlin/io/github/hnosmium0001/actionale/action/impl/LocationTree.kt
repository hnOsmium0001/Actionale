package io.github.hnosmium0001.actionale.action.impl

import io.github.hnosmium0001.actionale.action.Action
import io.github.hnosmium0001.actionale.action.Key
import org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN

interface ILocationNode {
    val key: Key
    var parent: ILocationNode?
    var children: MutableMap<Key, ILocationNode>

    var action: Action?
}

private class LocationNode(
        override val key: Key,
        override var parent: ILocationNode?,
        override var children: MutableMap<Key, ILocationNode> = HashMap()
) : ILocationNode {
    override var action: Action? = null
}

private class RootLocationNode : ILocationNode {
    override val key = GLFW_KEY_UNKNOWN
    override var parent: ILocationNode? = null
    override var children: MutableMap<Key, ILocationNode> = HashMap()
    override var action: Action? = null
}

class ActionLocator {
    private val root = RootLocationNode()

    /**
     * Current input pointer. When `null`, it indicates no inputs are received.
     */
    private var pointer: ILocationNode = root
    private var idleTime = 0L

    fun addAction(action: Action) {
        var node: ILocationNode = root
        for (key in action.keyChord.keys) {
            node = node.children.getOrPut(key) { LocationNode(key, node) }
        }

        node.action = action
    }

    fun keyInput(key: Key) {
        pointer = pointer.children.getOrDefault(key, root)
    }

    fun tick() {
        idleTime++

        if (idleTime > 10) {
            idleTime = 0L
            pointer.action?.trigger()
        }
    }
}