package com.example.game

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.ui.components.rememberGameBitmap
import com.example.data.model.DragonState
import com.example.data.model.FloatingDamage
import com.example.data.model.PandaState
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun BattleArenaCanvas(
    pandaState: PandaState,
    dragonState: DragonState,
    isArrowActive: Boolean,
    arrowProgress: Float,
    isFireActive: Boolean,
    fireProgress: Float,
    dragonMouthGlow: Float,
    cameraShakeOffset: Offset,
    screenFlashAlpha: Float,
    floatingDamages: List<FloatingDamage>,
    modifier: Modifier = Modifier
) {
    val background = rememberGameBitmap("backgrounds/boss_battle_bg.png")
    val panda = rememberGameBitmap("characters/panda_archer.png")
    val pandaVictory = rememberGameBitmap("characters/panda_victory.png")
    val pandaDefeated = rememberGameBitmap("characters/panda_dizzy.png")
    val dragon = rememberGameBitmap("characters/dragon_fire.png")

    // 60fps game ticker for ambient animations & particles
    var gameTime by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var lastTime = 0L
        while (true) {
            withFrameNanos { now ->
                if (lastTime != 0L) {
                    gameTime += ((now - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                }
                lastTime = now
            }
        }
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // 1. Draw Asian Fantasy Fortress / Temple Background
            drawArt(background, Offset.Zero, Size(canvasW, canvasH), cover = true)

            // Apply camera shake transform to arena actors
            translate(cameraShakeOffset.x, cameraShakeOffset.y) {
                // Layout coordinates
                val groundY = canvasH * 0.82f

                // All actor boxes and attachment points scale with the arena.
                val pandaBox = fitArtSize(panda, Size(canvasW * 0.38f, canvasH * 0.53f))
                val pandaOrigin = Offset(canvasW * 0.015f, groundY - pandaBox.height)
                val pandaBob = if (pandaState == PandaState.IDLE) sin(gameTime * 3.2f) * canvasH * 0.006f else 0f
                val pandaKnock = if (pandaState == PandaState.HURT) sin(gameTime * 24f) * canvasW * 0.018f else 0f
                val dragonBox = fitArtSize(dragon, Size(canvasW * 0.57f, canvasH * 0.84f))
                val dragonOrigin = Offset(canvasW * 0.43f, groundY - dragonBox.height)
                val dragonBob = sin(gameTime * 2.2f) * canvasH * 0.015f
                val dragonKnock = if (dragonState == DragonState.HURT) sin(gameTime * 25f) * canvasW * 0.015f else 0f
                val bowWorldPos = pandaOrigin + Offset(pandaBox.width * 0.91f, pandaBox.height * 0.32f)
                val pandaHitPos = pandaOrigin + Offset(pandaBox.width * 0.5f, pandaBox.height * 0.50f)
                val dragonMouthPos = dragonOrigin + Offset(dragonBox.width * 0.13f, dragonBox.height * 0.37f + dragonBob)
                val dragonHitPos = dragonOrigin + Offset(dragonBox.width * 0.46f, dragonBox.height * 0.54f)

                drawArt(dragon, dragonOrigin + Offset(dragonKnock, dragonBob), dragonBox,
                    alpha = if (dragonState == DragonState.DEFEATED) 0.35f else 1f,
                    flash = dragonState == DragonState.HURT)
                val pandaArt = when (pandaState) {
                    PandaState.VICTORY -> pandaVictory
                    PandaState.DEFEATED -> pandaDefeated
                    else -> panda
                }
                rotate(if (pandaState == PandaState.ATTACKING) -5f else 0f, pandaHitPos) {
                    drawArt(pandaArt, pandaOrigin + Offset(pandaKnock, pandaBob), pandaBox,
                        flash = pandaState == PandaState.HURT)
                }
                if (dragonMouthGlow > 0f) {
                    val radius = canvasW * 0.07f
                    drawCircle(Brush.radialGradient(
                        listOf(Color.White.copy(alpha = dragonMouthGlow), Color(0xFFFF9800).copy(alpha = dragonMouthGlow * 0.7f), Color.Transparent),
                        center = dragonMouthPos, radius = radius), radius, dragonMouthPos)
                }

                // 2. Draw Arrow Projectile (Quadratic Bézier Trajectory)
                if (isArrowActive && arrowProgress in 0f..1f) {
                    val p0 = bowWorldPos
                    val p2 = dragonHitPos
                    val p1 = Offset(
                        (p0.x + p2.x) * 0.5f,
                        (p0.y + p2.y) * 0.5f - (canvasH * 0.28f) // High arc
                    )

                    val t = arrowProgress
                    // Bézier: B(t) = (1-t)^2 P0 + 2(1-t)t P1 + t^2 P2
                    val arrowX = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x
                    val arrowY = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y

                    // Tangent derivative for arrow rotation: B'(t) = 2(1-t)(P1 - P0) + 2t(P2 - P1)
                    val dx = 2 * (1 - t) * (p1.x - p0.x) + 2 * t * (p2.x - p1.x)
                    val dy = 2 * (1 - t) * (p1.y - p0.y) + 2 * t * (p2.y - p1.y)
                    val angleDeg = (atan2(dy, dx) * 180f / PI).toFloat()

                    // Draw golden trajectory trail
                    val trailPoints = 12
                    for (i in 1..trailPoints) {
                        val subT = (t - (i * 0.015f)).coerceAtLeast(0f)
                        val tx = (1 - subT) * (1 - subT) * p0.x + 2 * (1 - subT) * subT * p1.x + subT * subT * p2.x
                        val ty = (1 - subT) * (1 - subT) * p0.y + 2 * (1 - subT) * subT * p1.y + subT * subT * p2.y
                        val trailAlpha = ((trailPoints - i) / trailPoints.toFloat()) * 0.7f
                        drawCircle(
                            color = Color(0xFFFFD54F).copy(alpha = trailAlpha),
                            radius = (trailPoints - i) * 0.8f + 1.5f,
                            center = Offset(tx, ty)
                        )
                    }

                    // Draw Arrow
                    rotate(degrees = angleDeg, pivot = Offset(arrowX, arrowY)) {
                        // Shaft
                        drawLine(
                            color = Color(0xFF8D6E63),
                            start = Offset(arrowX - 22f, arrowY),
                            end = Offset(arrowX + 16f, arrowY),
                            strokeWidth = 3.5f
                        )
                        // Arrowhead
                        val headPath = Path().apply {
                            moveTo(arrowX + 26f, arrowY)
                            lineTo(arrowX + 14f, arrowY - 6f)
                            lineTo(arrowX + 16f, arrowY)
                            lineTo(arrowX + 14f, arrowY + 6f)
                            close()
                        }
                        drawPath(headPath, color = Color(0xFFFFC107))
                        // Arrowhead Glow
                        drawCircle(
                            color = Color(0xFFFFF176).copy(alpha = 0.8f),
                            radius = 6f,
                            center = Offset(arrowX + 22f, arrowY)
                        )
                        // Fletching (feathers)
                        drawLine(
                            color = Color(0xFFE53935),
                            start = Offset(arrowX - 22f, arrowY - 5f),
                            end = Offset(arrowX - 16f, arrowY),
                            strokeWidth = 2.5f
                        )
                        drawLine(
                            color = Color(0xFFE53935),
                            start = Offset(arrowX - 22f, arrowY + 5f),
                            end = Offset(arrowX - 16f, arrowY),
                            strokeWidth = 2.5f
                        )
                    }
                }

                // 3. Draw Torrential Fire Breath Stream (Luồng Lửa Cuộn Trào Từ Miệng Rồng)
                if (isFireActive && fireProgress in 0f..1f) {
                    val p0 = dragonMouthPos
                    val p1 = pandaHitPos
                    val fireX = p0.x + (p1.x - p0.x) * fireProgress
                    val fireY = p0.y + (p1.y - p0.y) * fireProgress

                    // Full continuous fire torrent stream matching user reference image
                    drawContinuousFireBreathTorrent(
                        mouthPos = p0,
                        headPos = Offset(fireX, fireY),
                        progress = fireProgress,
                        gameTime = gameTime
                    )
                }

                // 4. Draw Floating Damage Text
                floatingDamages.forEach { dmg ->
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = dmg.color.copy(alpha = dmg.alpha).toArgb()
                            textSize = 42f * dmg.scale
                            isFakeBoldText = true
                            textAlign = Paint.Align.CENTER
                            setShadowLayer(8f, 0f, 4f, android.graphics.Color.BLACK)
                        }
                        val hit = if (dmg.text == "-30") pandaHitPos else dragonHitPos
                        // y is animated in the legacy 520/480 coordinate space.
                        val initialY = if (dmg.text == "-30") 520f else 480f
                        drawText(dmg.text, hit.x, hit.y + (dmg.y - initialY) * canvasH / 600f, paint)
                    }
                }
            }

            // 5. Red Screen Flash (when player takes hit)
            if (screenFlashAlpha > 0.01f) {
                drawRect(
                    color = Color.Red.copy(alpha = screenFlashAlpha),
                    size = size
                )
            }
        }
    }
}

private fun fitArtSize(image: ImageBitmap, bounds: Size): Size {
    val factor = minOf(bounds.width / image.width, bounds.height / image.height)
    return Size(image.width * factor, image.height * factor)
}

/** Contain actors so hats, feet, horns and tails stay visible; cover the backdrop. */
private fun DrawScope.drawArt(
    image: ImageBitmap,
    origin: Offset,
    box: Size,
    cover: Boolean = false,
    alpha: Float = 1f,
    flash: Boolean = false
) {
    val sx = box.width / image.width
    val sy = box.height / image.height
    val factor = if (cover) maxOf(sx, sy) else minOf(sx, sy)
    val width = (image.width * factor).toInt().coerceAtLeast(1)
    val height = (image.height * factor).toInt().coerceAtLeast(1)
    clipRect(origin.x, origin.y, origin.x + box.width, origin.y + box.height) {
    drawImage(image,
        dstOffset = IntOffset((origin.x + (box.width - width) / 2).toInt(), (origin.y + (box.height - height) / 2).toInt()),
        dstSize = IntSize(width, height),
        alpha = alpha,
        colorFilter = if (flash) ColorFilter.lighting(Color(0xFFFFEEEE), Color(0xFF553333)) else null)
    }
}

private fun DrawScope.drawContinuousFireBreathTorrent(
    mouthPos: Offset,
    headPos: Offset,
    progress: Float,
    gameTime: Float
) {
    val totalDist = (headPos - mouthPos)
    val length = totalDist.getDistance()
    if (length < 5f) return

    val numSegments = 16
    for (i in 0..numSegments) {
        val segT = i / numSegments.toFloat()
        val curX = mouthPos.x + totalDist.x * segT
        val curY = mouthPos.y + totalDist.y * segT

        // Radius expands as flame travels away from mouth
        val waveTurbulence = sin(gameTime * 20f + i * 1.5f) * 6f
        val segRadius = (14f + segT * 42f) + waveTurbulence

        // 1. Outer Crimson Flame Smoke Puff
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF3D00), Color(0xFFD50000), Color(0x00D50000)),
                center = Offset(curX, curY),
                radius = segRadius * 1.35f
            ),
            radius = segRadius * 1.35f,
            center = Offset(curX, curY)
        )

        // 2. Mid Vibrant Amber-Orange Flame
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFEA00), Color(0xFFFF9100), Color.Transparent),
                center = Offset(curX, curY),
                radius = segRadius * 0.95f
            ),
            radius = segRadius * 0.95f,
            center = Offset(curX, curY)
        )

        // 3. Inner White-Hot Plasma Core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color(0xFFFFF9C4), Color.Transparent),
                center = Offset(curX, curY),
                radius = segRadius * 0.55f
            ),
            radius = segRadius * 0.55f,
            center = Offset(curX, curY)
        )
    }

    // Billowing Flame Plume at the Leading Head of Fire
    for (petal in 0..5) {
        val pAngle = (petal * 60f + gameTime * 150f) * PI / 180f
        val pOffset = 22f + sin(gameTime * 12f + petal) * 8f
        val px = headPos.x + (cos(pAngle) * pOffset).toFloat()
        val py = headPos.y + (sin(pAngle) * pOffset).toFloat()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFD600), Color(0xFFFF3D00), Color.Transparent),
                center = Offset(px, py),
                radius = 28f
            ),
            radius = 28f,
            center = Offset(px, py)
        )
    }

    // Radiant Flying Sparks & Embers Swirling Around Stream
    for (s in 0..12) {
        val sT = (s / 12f)
        val sx = mouthPos.x + totalDist.x * sT + sin(gameTime * 15f + s) * (30f * sT)
        val sy = mouthPos.y + totalDist.y * sT + cos(gameTime * 18f + s * 2f) * (26f * sT)
        drawCircle(
            color = if (s % 2 == 0) Color.White else Color(0xFFFFD54F),
            radius = (Random(s).nextFloat() * 3.5f + 1.5f),
            center = Offset(sx, sy)
        )
    }
}


