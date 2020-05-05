package io.github.hnosmium0001.actionale.integration

import io.github.hnosmium0001.actionale.core.input.*
import io.github.hnosmium0001.actionale.mixinextension.ExtendedKeyBinding
import net.minecraft.client.options.KeyBinding
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

/**
 * Generate and add migrated keymaps
 */
fun generateVanillaMigrations(keyBindings: Array<out KeyBinding>) {
    KeymapManager.migratedKeymaps as MutableMap
    for (keyBinding in keyBindings) {
        val migration = Keymap(
            name = keyBinding.id,
            type = KeymapType.MIGRATED,
            combination = arrayOf(KeyChordManager.obtain(keyBinding.defaultKeyCode))
        )
        val target = KeymapManager.keymapOverrides[keyBinding.id] ?: migration
        target.listeners += { _, action ->
            keyBinding as ExtendedKeyBinding
            when (action) {
                GLFW_PRESS -> keyBinding.press()
                GLFW_RELEASE -> keyBinding.release()
                else -> throw RuntimeException()
            }
        }
        KeymapManager.migratedKeymaps[keyBinding.id] = Pair(keyBinding, migration)
    }
}

/**
 * Update key combination of the named migrated keymap. This should **NOT** be called by developers for
 * customization usages. Instead this is meant for syncing key combination when other mods tries to modify
 * [KeyBinding]'s key code
 *
 * @see io.github.hnosmium0001.actionale.mixin.MixinKeyBinding.setKeyCodeHook
 */
fun updateMigration(name: String, newKey: Key) {
    KeymapManager.migratedKeymaps as MutableMap
    val (_, original) = KeymapManager.migratedKeymaps[name] ?: return
    original.combination = arrayOf(KeyChordManager.obtain(newKey))
}