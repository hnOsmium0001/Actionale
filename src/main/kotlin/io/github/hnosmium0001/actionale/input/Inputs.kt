package io.github.hnosmium0001.actionale.input

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

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
        KeymapManager.onKeyInput(keyCode, action)
    }

    fun getKeyStatus(keyCode: InputUtil.KeyCode): InputAction {
        return chooseBy(keyCode.category)[keyCode.keyCode] ?: GLFW_RELEASE
    }

    private fun chooseBy(category: InputUtil.Type) = when (category) {
        InputUtil.Type.MOUSE -> mouseStatuses
        InputUtil.Type.KEYSYM, InputUtil.Type.SCANCODE -> statuses
    }


    fun keyStatusCallback(keyCode: KeyCode, scancode: Scancode, action: InputAction, mods: ModFlags) {
        setKeyStatus(InputUtil.getKeyCode(keyCode, scancode), action)
    }

    fun mouseStatusCallback(button: MouseCode, action: InputAction, mods: ModFlags) {
        setKeyStatus(InputUtil.Type.MOUSE.createFromCode(button), action)
    }
}
