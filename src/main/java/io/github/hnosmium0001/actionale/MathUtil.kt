package io.github.hnosmium0001.actionale

import net.minecraft.client.util.math.Vector3d

fun Int.isEven() = this % 2 == 0
fun Int.isOdd() = this % 2 == 1

fun Vector3d.pointInTriangle(p1: Vector3d, p2: Vector3d, p3: Vector3d): Boolean {
    val d1 = signOf(this, p1, p2)
    val d2 = signOf(this, p2, p3)
    val d3 = signOf(this, p3, p1)
    val hasNeg = d1 < 0 || d2 < 0 || d3 < 0
    val hasPos = d1 > 0 || d2 > 0 || d3 > 0
    return !(hasNeg && hasPos)
}

private fun signOf(p1: Vector3d, p2: Vector3d, p3: Vector3d): Double {
    return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
}