package io.github.hnosmium0001.actionale.core

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.Vector3d

val textRenderer: TextRenderer get() = MinecraftClient.getInstance().textRenderer
val fontHeight get() = textRenderer.fontHeight

fun VertexConsumer.vertex(vert: Vector3d): VertexConsumer {
    this.vertex(vert.x, vert.y, vert.z)
    return this
}

fun VertexConsumer.color(hex: Int): VertexConsumer {
    val alpha = (hex shr 24) and 0xff
    val red = (hex shr 16) and 0xff
    val green = (hex shr 8) and 0xff
    val blue = hex and 0xff
    this.color(red, green, blue, alpha)
    return this
}

fun TextRenderer.draw(text: String, pos: Vector3d, color: Int = 0xff000000.toInt()) {
    this.draw(text, pos.x.toFloat(), pos.y.toFloat(), color)
}