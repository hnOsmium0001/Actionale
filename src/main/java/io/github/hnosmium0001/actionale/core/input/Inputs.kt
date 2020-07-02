@file:Suppress("NAME_SHADOWING")

package io.github.hnosmium0001.actionale.core.input

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import io.github.hnosmium0001.actionale.core.firstOtherwise
import io.github.hnosmium0001.actionale.modConfig
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW.*

typealias Key = InputUtil.KeyCode
typealias KeyCode = Int
typealias Scancode = Int
typealias MouseCode = Int

/**
 * @see GLFW_PRESS
 * @see GLFW_RELEASE
 * @see GLFW_REPEAT
 */
typealias InputAction = Int
/**
 * @see GLFW_MOD_CONTROL
 * @see GLFW_MOD_SHIFT
 * @see GLFW_MOD_ALT
 * @see GLFW_MOD_CAPS_LOCK
 * @see GLFW_MOD_SUPER
 * @see GLFW_MOD_NUM_LOCK
 */
typealias ModFlags = Int

// Even though `InputUtil.KeyCode` have categories, Actionale's abstraction layer doesn't. We want the indicators to be
// distinguishable even when we don't provide a category
private val keyIndicatorMap: BiMap<Int, String> = HashBiMap.create<Int, String>().apply {
    put(GLFW_KEY_LEFT_CONTROL, "C")
    put(GLFW_KEY_RIGHT_CONTROL, "Cr")
    put(GLFW_KEY_LEFT_ALT, "A")
    put(GLFW_KEY_RIGHT_ALT, "Ar")
    put(GLFW_KEY_LEFT_SHIFT, "S")
    put(GLFW_KEY_RIGHT_SHIFT, "Sr")

    for ((keyCode, indicator) in (GLFW_KEY_A..GLFW_KEY_Z).zip('a'..'z')) {
        put(keyCode, indicator.toString())
    }
    for ((keyCode, indicator) in (GLFW_KEY_0..GLFW_KEY_9).zip('0'..'9')) {
        put(keyCode, indicator.toString())
    }

    put(GLFW_KEY_MINUS, "minus")
    put(GLFW_KEY_EQUAL, "equal")
    put(GLFW_KEY_BACKSPACE, "backspace")
    put(GLFW_KEY_ENTER, "enter")
    put(GLFW_KEY_LEFT_BRACKET, "left_bracket")
    put(GLFW_KEY_RIGHT_BRACKET, "right_bracket")
    put(GLFW_KEY_SEMICOLON, "semicolon")
    put(GLFW_KEY_APOSTROPHE, "apostrophe")
    put(GLFW_KEY_COMMA, "comma")
    put(GLFW_KEY_PERIOD, "period")
    put(GLFW_KEY_SLASH, "slash")
    put(GLFW_KEY_BACKSLASH, "backslash")

    put(GLFW_KEY_INSERT, "ins")
    put(GLFW_KEY_DELETE, "del")
    put(GLFW_KEY_HOME, "home")
    put(GLFW_KEY_END, "end")
    put(GLFW_KEY_PAGE_UP, "pg_up")
    put(GLFW_KEY_PAGE_DOWN, "pg_down")
    put(GLFW_KEY_UP, "up")
    put(GLFW_KEY_DOWN, "down")
    put(GLFW_KEY_LEFT, "left")
    put(GLFW_KEY_RIGHT, "right")

    // Set <leader> key last so that it overrides whatever it equals to
    put(modConfig.leaderKey, "leader")
}
private val mouseIndicatorMap: BiMap<Int, String> = HashBiMap.create<Int, String>().apply {
    put(GLFW_MOUSE_BUTTON_LEFT, "M_left")
    put(GLFW_MOUSE_BUTTON_RIGHT, "M_right")
    put(GLFW_MOUSE_BUTTON_MIDDLE, "M_mid")
    for (keyCode in (GLFW_MOUSE_BUTTON_4..GLFW_MOUSE_BUTTON_8)) {
        put(keyCode, "Mi$keyCode")
    }
}

val Key.categoryName
    get() = when (this.category!!) {
        InputUtil.Type.KEYSYM -> "keyboard"
        InputUtil.Type.SCANCODE -> "scancode"
        InputUtil.Type.MOUSE -> "mouse"
    }

val Key.indicator: String
    get() = when (this.category!!) {
        InputUtil.Type.KEYSYM -> keyIndicatorMap.getOrDefault(keyCode, "?")
        InputUtil.Type.SCANCODE -> keyCode.toString()
        InputUtil.Type.MOUSE -> mouseIndicatorMap.getOrDefault(keyCode, "M?")
    }

val Key.translation: String
    get() = I18n.translate(name)

fun keyFrom(indicator: String): Key {
    val (keyCode, category) = (keyIndicatorMap.inverse()[indicator] to InputUtil.Type.KEYSYM)
        .firstOtherwise { mouseIndicatorMap.inverse()[indicator] to InputUtil.Type.MOUSE }
        .firstOtherwise { indicator.substring(2).toInt() to InputUtil.Type.SCANCODE }
    return category.createFromCode(keyCode!!)
}

fun keyFrom(category: String, indicator: String): Key {
    val category = when (category) {
        "keyboard" -> InputUtil.Type.KEYSYM
        "scancode" -> InputUtil.Type.SCANCODE
        "mouse" -> InputUtil.Type.MOUSE
        else -> throw RuntimeException()
    }
    val keyCode = when (category) {
        InputUtil.Type.KEYSYM -> keyIndicatorMap.inverse()[indicator] ?: error("Unknown key indicator $indicator")
        // Remove first 2 chars of "Mi$scancode"
        InputUtil.Type.SCANCODE -> indicator.substring(2).toInt()
        InputUtil.Type.MOUSE -> mouseIndicatorMap.inverse()[indicator] ?: error("Unknown mouse indicator $indicator")
    }
    // Note that InputUtil::fromName is for translation keys, doesn't work for us here
    return category.createFromCode(keyCode)
}

fun keyFromTranslation(translationKey: String): Key {
    return InputUtil.fromName(translationKey)
}

object InputManager {
    fun setKeyStatus(key: Key, action: InputAction) {
        // Handle key chord triggering
        if (key.keyCode == GLFW_KEY_UNKNOWN) {
            return
        }
        when (action) {
            GLFW_PRESS -> {
                SimpleKeymapTriggerer.onKeyPress(key)
                KeyChordTriggerer.onKeyPress(key)
            }
            GLFW_RELEASE -> {
                SimpleKeymapTriggerer.onKeyRelease(key)
                KeyChordTriggerer.onKeyRelease(key)
            }
            else -> {
            }
        }
    }
}