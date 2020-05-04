@file:Suppress("NAME_SHADOWING")

package io.github.hnosmium0001.actionale.core.action

import com.google.common.base.Preconditions
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.github.hnosmium0001.actionale.core.*
import io.github.hnosmium0001.actionale.core.input.InputAction
import io.github.hnosmium0001.actionale.core.input.Keymap
import io.github.hnosmium0001.actionale.core.input.KeymapManager
import io.github.hnosmium0001.actionale.core.ui.RadialMenu
import io.github.hnosmium0001.actionale.modConfig
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW.GLFW_PRESS

// TODO add support for KeyBinding -> Action

/**
 * An action is a callback wrapper that is triggered on player input.
 * Every action is callable through the command `/call <action-id>`. Every action is also remappable to multiple key
 * inputs by registering [Keymap]s bound to an [Action] by either the developer or players.
 * Actions can additionally be arranged into [RadialMenuAction]s (in-game radial menu) by players, which is also
 *
 * ```
 * +----------------+----------------------+-------------------------+
 * | Action ID      | Execute command      | Keymaps (custom)        |
 * +----------------+----------------------+-------------------------+
 * | example:foo    | /call example:foo    | Ctrl+A (<C-a>)          |
 * |                |                      | Alt+M+N (<M-m-n>)       |
 * +----------------+----------------------+-------------------------+
 * | example:bar    | /call example:bar    | Ctrl+G,T (<C-g>t)       |
 * |                |                      | (separate input chords) |
 * +----------------+----------------------+-------------------------+
 * | example:foobar | /call example:foobar | (no keymap)             |
 * +----------------+----------------------+-------------------------+
 * ```
 *
 * @see [RadialMenuAction]
 */
sealed class Action(
    val id: Identifier,
    var name: String,
    // Record the names instead of direct references to allow player-overrides
    val triggers: MutableSet<String> = HashSet()
) {
    var callback: (Keymap, InputAction) -> Unit = { _, _ -> }
        set(value) {
            field = value
            for (name in triggers) {
                val keymap = KeymapManager[name] ?: continue
                keymap.listeners.remove(this)
                keymap.listeners[this] = value
            }
        }

    override fun toString(): String {
        return "Action(id=$id, name='$name')"
    }
}

/**
 * An action containing a number of sub-actions. Activating such action brings up a radial menu, as its name suggests,
 * and the player can activate on of the actions inside.
 * Radial menus can also contain other radial menus, creating a tree structure.
 */
class RadialMenuAction(
    id: Identifier,
    name: String,
    triggers: MutableSet<String> = HashSet(),
    val subActions: Array<Action>
) : Action(id, name, triggers) {
    init {
        callback = { _, action -> this.openRadialMenu(action) }
    }

    fun openRadialMenu(action: InputAction) {
        if (action != GLFW_PRESS) return
        MinecraftClient.getInstance().openScreen(RadialMenu(
            components = this.subActions,
            componentsPerPage = modConfig.radialMenuMinSides,
            trKey = "gui.actinoale.radialMenu.title"
        ))
    }
}

fun RadialMenuAction.serialize() =
    // RadialMenuAction -> JsonObject
    JsonObject().also { menuData ->
        menuData.addProperty("id", id)
        menuData.addProperty("name", name)
        // String[] -> JsonArray
        menuData.add("triggers", triggers.pack { trigger ->
            JsonPrimitive(trigger)
            // ...
        })
        // Action[] -> JsonArray
        menuData.add("subactions", subActions.pack { subaction ->
            // Action -> JsonPrimitive<String>
            JsonPrimitive(subaction.id.toString())
        })
    }

fun deserializeRadialMenuAction(data: JsonObject) =
    // JsonObject -> RadialMenuAction
    RadialMenuAction(
        id = data.get("id").asIdentifier,
        name = data.get("name").asString,
        // JsonArray -> String[]
        triggers = data.get("triggers").asJsonArray.unpack(HashSet()) { trigger ->
            trigger.asString
            // ...
        },
        // JsonArray -> Action[]
        subActions = data.get("subactions").asJsonArray.unpackArray { subaction ->
            val id = Identifier(subaction.asString)
            ActionManager.actions[id] ?: error("Invalid serialized data when trying to deserialize RadialMenuAction")
            // ...
        }
    )

object ActionManager {
    val actions: Map<Identifier, Action> = HashMap()

    fun registerAction(action: Action) {
        actions as MutableMap
        Preconditions.checkArgument(!actions.containsKey(action.id))
        actions[action.id] = action
    }

    fun serializeRadialMenus(): JsonArray {
        // RadialMenuAction[] -> JsonArray
        return JsonArray().apply {
            for ((_, action) in actions) {
                if (action !is RadialMenuAction) continue

                // Action -> JsonObject
                add(action.serialize())
                // ...
            }
        }
    }

    fun deserializeRadialMenus(data: JsonArray) {
        actions as MutableMap
        // JsonArray -> RadialMenuAction[]
        for (menuData in data) {
            // JsonObject -> RadialMenuAction
            val menu = deserializeRadialMenuAction(menuData.asJsonObject)
            actions[menu.id] = menu
            // ...
        }
    }
}