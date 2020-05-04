package io.github.hnosmium0001.actionale.core

import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.Vector3d

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