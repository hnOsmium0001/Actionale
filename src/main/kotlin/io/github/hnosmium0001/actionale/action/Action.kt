package io.github.hnosmium0001.actionale.action

import net.minecraft.util.Identifier

/**
 * An action is a callback wrapper that is triggered on player input.
 * Every action is callable through the command `/call <action-id>`. Every action is also remappable to multiple key
 * inputs by registering `Keymap`s bound to an `Action` by either the developer or players.
 * Actions can additionally be arranged into `ActionWheel`s (in-game radial menu) by players, which is also
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
 */
class Action(
        val id: Identifier,
        val callback: () -> Unit
)