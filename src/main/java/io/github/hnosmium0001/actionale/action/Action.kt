package io.github.hnosmium0001.actionale.action

import io.github.hnosmium0001.actionale.*
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.StringTag
import net.minecraft.util.Identifier

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
    val keymaps: MutableSet<String> = HashSet()
) {
    var callback: () -> Unit = {}

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
    keymaps: MutableSet<String> = HashSet(),
    val subActions: Array<Action>
) : Action(id, name, keymaps) {
    init {
        callback = this::openRadialMenu
    }

    fun openRadialMenu() {
        TODO("unimplemented")
    }
}

fun RadialMenuAction.serialize() =
    CompoundTag().apply {
        putIdentifier("ID", id)
        putString("Name", name)
        put("Keymaps", keymaps.pack {
            StringTag.of(it)
        })
        put("SubActions", subActions.pack {
            StringTag.of(it.id.toString())
        })
    }

fun deserializeRadialMenuAction(data: CompoundTag) =
    RadialMenuAction(
        id = data.getIdentifier("ID")!!,
        name = data.getString("Name"),
        keymaps = data.getList("Keymaps", NbtType.STRING).unpack<String, MutableSet<String>, StringTag>(HashSet()) { tag ->
            tag.asString()
        },
        subActions = data.getList("SubActions", NbtType.STRING).unpackArray<Action, StringTag> { tag ->
            val id = Identifier(tag.asString())
            ActionManager.actions[id] ?: error("Invalid serialized data when trying to deserialize RadialMenuAction")
        })

object ActionManager {
    val actions: Map<Identifier, Action> = HashMap()

    fun registerAction(id: Identifier, action: Action) {
        actions as MutableMap
        actions[id] = action
    }

    fun serializeRadialMenus(): CompoundTag {
        return CompoundTag().apply {
            for ((id, action) in actions) {
                if (action is RadialMenuAction) {
                    put(id.toString(), action.serialize())
                }
            }
        }
    }

    fun deserializeRadialMenus(data: CompoundTag) {
        actions as MutableMap
        for (key in data.keys) {
            actions[Identifier(key)] = deserializeRadialMenuAction(data.getCompound(key))
        }
    }
}