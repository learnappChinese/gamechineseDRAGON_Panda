package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class PandaState {
    IDLE,
    ATTACKING,
    HURT,
    VICTORY,
    DEFEATED
}

enum class DragonState {
    IDLE,
    ATTACKING,
    HURT,
    DEFEATED
}

data class ArrowProjectile(
    val p0x: Float,
    val p0y: Float,
    val p1x: Float,
    val p1y: Float,
    val p2x: Float,
    val p2y: Float,
    val progress: Float, // 0.0 -> 1.0
    val currentX: Float = 0f,
    val currentY: Float = 0f,
    val angleDegrees: Float = 0f
)

data class FireProjectile(
    val startX: Float,
    val startY: Float,
    val targetX: Float,
    val targetY: Float,
    val progress: Float, // 0.0 -> 1.0
    val currentX: Float = 0f,
    val currentY: Float = 0f
)

data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val radius: Float,
    val alpha: Float,
    val age: Float, // seconds
    val maxAge: Float // seconds
)

data class FloatingDamage(
    val id: Long,
    val text: String,
    val color: Color,
    val x: Float,
    val y: Float,
    val scale: Float = 1f,
    val alpha: Float = 1f
)

data class BossStats(
    val name: String = "Rồng Lửa",
    val level: Int = 3,
    val maxHp: Int = 500,
    val currentHp: Int = 320
)

data class PlayerStats(
    val name: String = "Đại Hiệp Panda",
    val maxHp: Int = 200,
    val currentHp: Int = 180,
    val combo: Int = 3
)
