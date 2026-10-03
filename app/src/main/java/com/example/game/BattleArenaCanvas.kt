package com.example.game

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.data.model.DragonState
import com.example.data.model.FloatingDamage
import com.example.data.model.PandaState
import com.example.data.model.Particle
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
    // 60fps game ticker for ambient animations & particles
    var gameTime by remember { mutableFloatStateOf(0f) }
    val particles = remember { mutableStateListOf<Particle>() }

    LaunchedEffect(Unit) {
        var lastTime = 0L
        while (true) {
            withFrameNanos { now ->
                if (lastTime != 0L) {
                    val dt = ((now - lastTime) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                    gameTime += dt

                    // Update existing particles
                    val iterator = particles.listIterator()
                    while (iterator.hasNext()) {
                        val p = iterator.next()
                        val newAge = p.age + dt
                        if (newAge >= p.maxAge) {
                            iterator.remove()
                        } else {
                            val newAlpha = (1f - (newAge / p.maxAge)).coerceIn(0f, 1f)
                            iterator.set(
                                p.copy(
                                    x = p.x + p.vx * dt,
                                    y = p.y + p.vy * dt,
                                    age = newAge,
                                    alpha = newAlpha
                                )
                            )
                        }
                    }

                    // Ambient dragon fire embers
                    if (particles.size < 40 && Random.nextFloat() < 0.25f) {
                        particles.add(
                            Particle(
                                x = 0f, // will be offset in draw relative to dragon
                                y = 0f,
                                vx = Random.nextFloat() * 40f - 20f,
                                vy = -(Random.nextFloat() * 60f + 30f),
                                color = if (Random.nextBoolean()) Color(0xFFFF9800) else Color(0xFFFF5722),
                                radius = Random.nextFloat() * 4f + 2f,
                                alpha = 0.8f,
                                age = 0f,
                                maxAge = Random.nextFloat() * 0.8f + 0.4f
                            )
                        )
                    }
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
            drawBattleBackground(canvasW, canvasH, gameTime)

            // Apply camera shake transform to arena actors
            translate(cameraShakeOffset.x, cameraShakeOffset.y) {
                // Layout coordinates
                val groundY = canvasH * 0.82f

                // Panda coordinates
                val pandaX = canvasW * 0.22f
                val pandaY = groundY - 60f
                val bowWorldPos = Offset(pandaX + 48f, pandaY - 30f)
                val pandaHitPos = Offset(pandaX, pandaY - 40f)

                // Dragon coordinates - scaled up and placed majestically in upper-right
                val dragonHoverY = sin(gameTime * 2.2f) * 10f
                val dragonX = canvasW * 0.74f
                val dragonY = canvasH * 0.42f + dragonHoverY
                val dragonMouthPos = Offset(dragonX - 75f, dragonY + 16f)
                val dragonHitPos = Offset(dragonX - 20f, dragonY + 10f)

                // Draw Dragon
                drawDragonCharacter(
                    centerX = dragonX,
                    centerY = dragonY,
                    state = dragonState,
                    gameTime = gameTime,
                    mouthGlow = dragonMouthGlow
                )

                // Draw Panda
                drawPandaCharacter(
                    centerX = pandaX,
                    centerY = pandaY,
                    state = pandaState,
                    gameTime = gameTime
                )

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
                        drawText(dmg.text, dmg.x, dmg.y, paint)
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

private fun DrawScope.drawBattleBackground(w: Float, h: Float, time: Float) {
    // Mystic Asian Temple Sunset/Night Sky Gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF160A24),
                Color(0xFF311438),
                Color(0xFF7A1C24),
                Color(0xFFBA4A1E),
                Color(0xFFE67E22)
            ),
            startY = 0f,
            endY = h * 0.85f
        ),
        size = Size(w, h)
    )

    // Glowing Full Moon
    val moonX = w * 0.72f
    val moonY = h * 0.22f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFEEB3), Color(0x66FFD54F), Color.Transparent),
            center = Offset(moonX, moonY),
            radius = 65f
        ),
        radius = 65f,
        center = Offset(moonX, moonY)
    )
    drawCircle(
        color = Color(0xFFFFF9E6),
        radius = 32f,
        center = Offset(moonX, moonY)
    )

    // Distant Misty Mountain Silhouette
    val mountainPath = Path().apply {
        moveTo(0f, h * 0.65f)
        lineTo(w * 0.2f, h * 0.48f)
        lineTo(w * 0.45f, h * 0.58f)
        lineTo(w * 0.68f, h * 0.42f)
        lineTo(w * 0.88f, h * 0.54f)
        lineTo(w, h * 0.50f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(mountainPath, color = Color(0x66240E2C))

    // Chinese Temple Pagoda Rooftops Silhouette
    val templePath = Path().apply {
        // Left Pagoda Roof
        moveTo(w * 0.05f, h * 0.68f)
        quadraticTo(w * 0.12f, h * 0.62f, w * 0.20f, h * 0.64f)
        lineTo(w * 0.20f, h * 0.82f)
        lineTo(w * 0.05f, h * 0.82f)
        close()
        // Right Pagoda Roof
        moveTo(w * 0.78f, h * 0.70f)
        quadraticTo(w * 0.88f, h * 0.64f, w * 0.98f, h * 0.66f)
        lineTo(w * 0.98f, h * 0.82f)
        lineTo(w * 0.78f, h * 0.82f)
        close()
    }
    drawPath(templePath, color = Color(0xAA190A22))

    // Ground Platform with Glowing Border
    val groundY = h * 0.80f
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF281830), Color(0xFF140B18)),
            startY = groundY,
            endY = h
        ),
        topLeft = Offset(0f, groundY),
        size = Size(w, h - groundY)
    )
    drawLine(
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0x33FF9800), Color(0xFFFFB300), Color(0x33FF9800))
        ),
        start = Offset(0f, groundY),
        end = Offset(w, groundY),
        strokeWidth = 3f
    )

    // Hanging Chinese Lanterns swinging gently
    val lanternSwing = sin(time * 2f) * 6f
    drawHangingLantern(w * 0.12f, h * 0.22f + lanternSwing)
    drawHangingLantern(w * 0.88f, h * 0.24f - lanternSwing)
}

private fun DrawScope.drawHangingLantern(x: Float, y: Float) {
    // Cord
    drawLine(
        color = Color(0xFFFFD54F),
        start = Offset(x, 0f),
        end = Offset(x, y - 18f),
        strokeWidth = 1.5f
    )
    // Lantern body
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFF5252), Color(0xFFD50000)),
            center = Offset(x, y),
            radius = 20f
        ),
        topLeft = Offset(x - 14f, y - 18f),
        size = Size(28f, 36f)
    )
    // Gold Caps
    drawRect(color = Color(0xFFFFD54F), topLeft = Offset(x - 8f, y - 20f), size = Size(16f, 3f))
    drawRect(color = Color(0xFFFFD54F), topLeft = Offset(x - 8f, y + 17f), size = Size(16f, 3f))
    // Glow
    drawCircle(
        color = Color(0x44FF9800),
        radius = 24f,
        center = Offset(x, y)
    )
}

// -------------------------------------------------------------
// PANDA ARCHER MASTER (GẤU TRÚC ĐẠI HIỆP CUNG THỦ) RENDERER
// Matches exactly the user reference: Conical straw hat (nón lá),
// Royal blue warrior robes, red sash, golden medallion, and recurve bow
// -------------------------------------------------------------
private fun DrawScope.drawPandaCharacter(
    centerX: Float,
    centerY: Float,
    state: PandaState,
    gameTime: Float
) {
    val idleBob = if (state == PandaState.IDLE) sin(gameTime * 3.2f) * 2.5f else 0f
    val hurtKnockback = if (state == PandaState.HURT) sin(gameTime * 24f) * 10f else 0f
    val bowDrawDistance = if (state == PandaState.ATTACKING) 24f else 8f

    translate(centerX + hurtKnockback, centerY + idleBob) {
        scale(scaleX = 1.15f, scaleY = 1.15f, pivot = Offset(0f, 0f)) {
            // Shadow
            drawOval(
                color = Color(0x55000000),
                topLeft = Offset(-42f, 38f),
                size = Size(84f, 18f)
            )

            // 1. QUIVER & ARROWS ON BACK (Ống Đựng Tên Sau Lưng)
            val quiverPath = Path().apply {
                moveTo(-38f, -15f)
                lineTo(-20f, 10f)
                lineTo(-28f, 16f)
                lineTo(-44f, -9f)
                close()
            }
            drawPath(quiverPath, color = Color(0xFF6D4C41))
            // Arrow fletching feathers sticking out
            for (f in 0..2) {
                val fAngle = (-125f + f * 18f) * PI / 180f
                val fx = -40f + (cos(fAngle) * 24f).toFloat()
                val fy = -12f + (sin(fAngle) * 24f).toFloat()
                drawLine(
                    color = Color(0xFF8D6E63),
                    start = Offset(-38f, -12f),
                    end = Offset(fx, fy),
                    strokeWidth = 2.5f
                )
                drawCircle(color = Color.White, radius = 4f, center = Offset(fx, fy))
            }

            // 2. LEGS & MARTIAL HORSE STANCE (Tấn Mã Vững Chãi)
            // Left leg
            val leftLeg = Path().apply {
                moveTo(-32f, 12f)
                lineTo(-40f, 34f)
                lineTo(-26f, 36f)
                lineTo(-20f, 16f)
                close()
            }
            drawPath(leftLeg, color = Color(0xFF1E293B))
            // Right leg forward
            val rightLeg = Path().apply {
                moveTo(14f, 14f)
                lineTo(24f, 34f)
                lineTo(38f, 36f)
                lineTo(28f, 16f)
                close()
            }
            drawPath(rightLeg, color = Color(0xFF1E293B))

            // White Bandage Wraps on Lower Legs
            drawRect(color = Color.White, topLeft = Offset(-39f, 26f), size = Size(12f, 6f))
            drawRect(color = Color.White, topLeft = Offset(24f, 26f), size = Size(13f, 6f))

            // Black Warrior Shoes with Golden Clasps
            drawOval(color = Color(0xFF0F172A), topLeft = Offset(-46f, 32f), size = Size(22f, 12f))
            drawOval(color = Color(0xFF0F172A), topLeft = Offset(24f, 32f), size = Size(22f, 12f))
            drawCircle(color = Color(0xFFFFD54F), radius = 3.5f, center = Offset(-38f, 36f))
            drawCircle(color = Color(0xFFFFD54F), radius = 3.5f, center = Offset(32f, 36f))

            // 3. ROYAL BLUE WARRIOR TUNIC (Áo Đạo Bào Lam Dạ)
            val robePath = Path().apply {
                moveTo(-28f, -8f)
                cubicTo(-32f, 10f, -28f, 24f, -22f, 26f)
                lineTo(22f, 26f)
                cubicTo(28f, 24f, 32f, 10f, 28f, -8f)
                close()
            }
            drawPath(
                robePath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1976D2), Color(0xFF0D47A1), Color(0xFF0A2E68)),
                    startY = -8f,
                    endY = 26f
                )
            )

            // White Inner Robe Collar
            val whiteCollar = Path().apply {
                moveTo(-12f, -12f)
                lineTo(0f, 6f)
                lineTo(12f, -12f)
                close()
            }
            drawPath(whiteCollar, color = Color.White)

            // Red Warrior Belt Sash & Flowing Ribbons
            drawRect(
                color = Color(0xFFD32F2F),
                topLeft = Offset(-26f, 10f),
                size = Size(52f, 8f)
            )
            // Golden Medallion Amulet at Waist
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFFFD54F), Color(0xFFFF8F00)),
                    center = Offset(0f, 14f),
                    radius = 8f
                ),
                radius = 7f,
                center = Offset(0f, 14f)
            )
            drawCircle(color = Color(0xFFBF360C), radius = 4f, center = Offset(0f, 14f), style = Stroke(1.5f))

            // Red Hanging Belt Tassels
            val tasselSwing = sin(gameTime * 4.5f) * 5f
            drawLine(
                color = Color(0xFFD32F2F),
                start = Offset(-4f, 18f),
                end = Offset(-8f + tasselSwing, 30f),
                strokeWidth = 3f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            drawLine(
                color = Color(0xFFD32F2F),
                start = Offset(4f, 18f),
                end = Offset(8f + tasselSwing, 30f),
                strokeWidth = 3f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            // 4. PANDA HEAD & EXPRESSION
            // Round white head
            drawCircle(color = Color.White, radius = 28f, center = Offset(0f, -22f))

            // Fluffy Black Panda Ears poking from under hat
            drawOval(color = Color(0xFF1E293B), topLeft = Offset(-36f, -44f), size = Size(18f, 16f))
            drawOval(color = Color(0xFF1E293B), topLeft = Offset(18f, -44f), size = Size(18f, 16f))

            // Black Eye Patches
            rotate(degrees = -14f, pivot = Offset(-12f, -22f)) {
                drawOval(color = Color(0xFF1E293B), topLeft = Offset(-20f, -28f), size = Size(16f, 18f))
            }
            rotate(degrees = 14f, pivot = Offset(12f, -22f)) {
                drawOval(color = Color(0xFF1E293B), topLeft = Offset(4f, -28f), size = Size(16f, 18f))
            }

            // Eyes based on state
            when (state) {
                PandaState.HURT -> {
                    // > < Squint eyes
                    drawLine(color = Color.White, start = Offset(-17f, -24f), end = Offset(-9f, -20f), strokeWidth = 2.5f)
                    drawLine(color = Color.White, start = Offset(-17f, -16f), end = Offset(-9f, -20f), strokeWidth = 2.5f)
                    drawLine(color = Color.White, start = Offset(9f, -20f), end = Offset(17f, -24f), strokeWidth = 2.5f)
                    drawLine(color = Color.White, start = Offset(9f, -20f), end = Offset(17f, -16f), strokeWidth = 2.5f)
                }
                PandaState.DEFEATED -> {
                    // @ @ Dizzy spiral eyes
                    drawCircle(color = Color.White, radius = 4f, center = Offset(-12f, -20f), style = Stroke(2f))
                    drawCircle(color = Color.White, radius = 4f, center = Offset(12f, -20f), style = Stroke(2f))
                }
                else -> {
                    // Big sparkling determined brown/black eyes
                    drawCircle(color = Color(0xFF3E2723), radius = 4.5f, center = Offset(-11f, -21f))
                    drawCircle(color = Color(0xFF3E2723), radius = 4.5f, center = Offset(13f, -21f))
                    drawCircle(color = Color.White, radius = 2f, center = Offset(-10f, -22f))
                    drawCircle(color = Color.White, radius = 2f, center = Offset(14f, -22f))
                }
            }

            // Snout, Black Nose & Joyful Smile
            drawOval(color = Color(0xFF0F172A), topLeft = Offset(-4f, -14f), size = Size(8f, 5f))
            val smilePath = Path().apply {
                if (state == PandaState.HURT || state == PandaState.DEFEATED) {
                    moveTo(-5f, -6f); quadraticTo(0f, -10f, 5f, -6f)
                } else {
                    // Open happy mouth
                    moveTo(-6f, -8f)
                    quadraticTo(0f, -2f, 6f, -8f)
                    close()
                }
            }
            drawPath(smilePath, color = if (state == PandaState.HURT) Color(0xFF1E293B) else Color(0xFFD32F2F))

            // 5. CONICAL WOVEN BAMBOO HAT (NÓN LÁ TRE VIỀN VÀNG)
            val hatPath = Path().apply {
                moveTo(0f, -60f)             // Apex
                quadraticTo(28f, -48f, 54f, -34f)  // Right rim
                quadraticTo(0f, -30f, -54f, -34f)  // Front curved rim
                quadraticTo(-28f, -48f, 0f, -60f)  // Left back to apex
                close()
            }
            drawPath(
                hatPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFE082), Color(0xFFFFCA28), Color(0xFFFFA000)),
                    startY = -60f,
                    endY = -30f
                )
            )
            // Bamboo Weave Radial Line Struts
            for (w in -4..4) {
                val rimX = w * 12f
                drawLine(
                    color = Color(0xFFB57400),
                    start = Offset(0f, -58f),
                    end = Offset(rimX, -32f),
                    strokeWidth = 1.2f
                )
            }
            // Rim border
            drawPath(hatPath, color = Color(0xFF8D6E63), style = Stroke(2f))

            // Red Ribbon Knot on Apex & Fluttering Ribbon Streamers
            drawCircle(color = Color(0xFFD32F2F), radius = 5.5f, center = Offset(0f, -60f))
            val ribbonFlap1 = sin(gameTime * 5.5f) * 10f
            val ribbonFlap2 = cos(gameTime * 6f) * 8f
            val redRibbonLeft = Path().apply {
                moveTo(-2f, -60f)
                cubicTo(-25f, -66f + ribbonFlap1, -50f, -55f + ribbonFlap2, -75f, -62f + ribbonFlap1)
                lineTo(-70f, -54f + ribbonFlap1)
                cubicTo(-46f, -48f, -22f, -58f, -2f, -58f)
                close()
            }
            drawPath(redRibbonLeft, color = Color(0xFFE53935))

            // 6. WOODEN RECURVE BOW & ARROW DRAWING (Cung Tre Uốn Lượn & Giương Cung)
            // Left Arm Outstretched Holding Bow
            val leftArm = Path().apply {
                moveTo(20f, -4f)
                lineTo(44f, -12f)
                lineTo(48f, -4f)
                lineTo(22f, 4f)
                close()
            }
            drawPath(leftArm, color = Color(0xFF1976D2))
            // Left Paw
            drawCircle(color = Color(0xFF1E293B), radius = 7f, center = Offset(46f, -8f))

            // Ornate Recurve Bow (Cánh cung uốn cong với đầu hoa văn mây vàng)
            val bowTop = Offset(40f, -48f)
            val bowCenter = Offset(52f, -8f)
            val bowBottom = Offset(38f, 28f)

            val bowCurve = Path().apply {
                moveTo(bowTop.x, bowTop.y)
                cubicTo(
                    bowTop.x + 8f, bowTop.y + 14f,
                    bowCenter.x + 4f, bowCenter.y - 12f,
                    bowCenter.x, bowCenter.y
                )
                cubicTo(
                    bowCenter.x + 4f, bowCenter.y + 12f,
                    bowBottom.x + 8f, bowBottom.y - 14f,
                    bowBottom.x, bowBottom.y
                )
            }
            // Polished Wood Core
            drawPath(bowCurve, color = Color(0xFF5D4037), style = Stroke(5f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            // Golden Tips (Cloud ornaments)
            drawCircle(color = Color(0xFFFFD54F), radius = 5.5f, center = bowTop)
            drawCircle(color = Color(0xFFFFD54F), radius = 5.5f, center = bowBottom)
            // Red Grip Wrap in Center
            drawRect(color = Color(0xFFD32F2F), topLeft = Offset(48f, -14f), size = Size(6f, 12f))

            // Bowstring Pulled to Cheek
            val stringHandX = bowCenter.x - bowDrawDistance
            val stringHandY = bowCenter.y - 2f
            val bowString = Path().apply {
                moveTo(bowTop.x, bowTop.y)
                lineTo(stringHandX, stringHandY)
                lineTo(bowBottom.x, bowBottom.y)
            }
            drawPath(bowString, color = Color(0xFFFFECB3), style = Stroke(1.8f))

            // Right Arm pulling string to cheek
            val rightArm = Path().apply {
                moveTo(0f, 0f)
                lineTo(stringHandX - 8f, stringHandY + 4f)
                lineTo(stringHandX, stringHandY)
                lineTo(0f, -6f)
                close()
            }
            drawPath(rightArm, color = Color(0xFF1565C0))
            drawCircle(color = Color(0xFF1E293B), radius = 6.5f, center = Offset(stringHandX, stringHandY))

            // Arrow on bow (Ready to release)
            if (state == PandaState.ATTACKING) {
                // Wooden shaft aimed forward
                drawLine(
                    color = Color(0xFF8D6E63),
                    start = Offset(stringHandX, stringHandY),
                    end = Offset(bowCenter.x + 28f, stringHandY),
                    strokeWidth = 3f
                )
                // Golden arrowhead
                val arrowTip = Path().apply {
                    val ax = bowCenter.x + 28f
                    val ay = stringHandY
                    moveTo(ax + 10f, ay)
                    lineTo(ax, ay - 5f)
                    lineTo(ax + 2f, ay)
                    lineTo(ax, ay + 5f)
                    close()
                }
                drawPath(arrowTip, color = Color(0xFFFFD54F))
                // Arrowhead Sparkle
                drawCircle(color = Color.White, radius = 3.5f, center = Offset(bowCenter.x + 36f, stringHandY))
            }
        }
    }
}


// -------------------------------------------------------------
// EPIC ORIENTAL FIRE DRAGON (THẦN LONG XÍCH DIỄM) CHARACTER RENDERER
// -------------------------------------------------------------
private fun DrawScope.drawDragonCharacter(
    centerX: Float,
    centerY: Float,
    state: DragonState,
    gameTime: Float,
    mouthGlow: Float
) {
    val hurtFlash = state == DragonState.HURT
    val defeatedAlpha = if (state == DragonState.DEFEATED) 0.35f else 1f
    val hurtOffset = if (hurtFlash) sin(gameTime * 25f) * 14f else 0f

    translate(centerX + hurtOffset, centerY) {
        scale(scaleX = 1.18f, scaleY = 1.18f, pivot = Offset(0f, 0f)) {
            // Colors - Divine Fire & Imperial Gold Palette
            val rubyRed = if (hurtFlash) Color(0xFFFFCDD2) else Color(0xFF990000)
            val scarletRed = if (hurtFlash) Color.White else Color(0xFFD50000)
            val flameOrange = if (hurtFlash) Color(0xFFFFEBEE) else Color(0xFFFF5722)
            val dragonGold = Color(0xFFFFD54F)
            val goldenBelly = Color(0xFFFFB300)
            val darkCrimson = Color(0xFF4A0000)

            // 1. AURA: Dragon Fiery Presence & Heat Radiance
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x33FF5722),
                        Color(0x18FF9800),
                        Color.Transparent
                    ),
                    center = Offset(20f, 0f),
                    radius = 160f
                ),
                radius = 160f,
                center = Offset(20f, 0f)
            )

            // 2. SERPENTINE COILING BODY (Thân Rồng Uốn Lượn Uy Nghi)
            // Body arch looping from upper-right down to chest
            val bodyBackPath = Path().apply {
                moveTo(140f, -80f)
                cubicTo(110f, -110f, 60f, -90f, 40f, -50f)
                cubicTo(20f, -10f, 40f, 40f, 70f, 60f)
                cubicTo(100f, 80f, 130f, 60f, 150f, 40f)
                lineTo(160f, 65f)
                cubicTo(130f, 95f, 80f, 105f, 40f, 75f)
                cubicTo(5f, 50f, -5f, -10f, 20f, -60f)
                cubicTo(45f, -115f, 110f, -135f, 155f, -95f)
                close()
            }
            drawPath(
                path = bodyBackPath,
                brush = Brush.verticalGradient(
                    colors = listOf(flameOrange, scarletRed, rubyRed),
                    startY = -120f,
                    endY = 100f
                )
            )

            // Dynamic Flaming Dorsal Spines along Coiling Body (Vây Lưng Rồng Rực Lửa)
            for (i in 0..5) {
                val spineAngle = -110f + i * 28f
                val rad = spineAngle * PI / 180f
                val spineWave = sin(gameTime * 4f + i) * 6f
                val sx = 80f + (cos(rad) * 65f).toFloat()
                val sy = -30f + (sin(rad) * 65f).toFloat()

                val spinePath = Path().apply {
                    moveTo(sx, sy)
                    quadraticTo(
                        sx + (cos(rad) * (26f + spineWave)).toFloat(),
                        sy + (sin(rad) * (26f + spineWave)).toFloat(),
                        sx + 10f, sy - 8f
                    )
                    close()
                }
                drawPath(spinePath, color = if (i % 2 == 0) dragonGold else flameOrange)
            }

            // Main Neck Arch into Chest
            val neckPath = Path().apply {
                moveTo(40f, -50f)
                cubicTo(0f, -30f, -30f, -10f, -35f, 20f)
                cubicTo(-40f, 45f, -20f, 75f, 20f, 85f)
                lineTo(35f, 65f)
                cubicTo(0f, 55f, -15f, 35f, -10f, 15f)
                cubicTo(-5f, -5f, 15f, -25f, 40f, -35f)
                close()
            }
            drawPath(
                neckPath,
                brush = Brush.linearGradient(
                    colors = listOf(scarletRed, rubyRed, darkCrimson),
                    start = Offset(-40f, 0f),
                    end = Offset(40f, 60f)
                )
            )

            // Segmented Golden Underbelly (Yếm Bụng Rồng Hoàng Kim)
            val bellyPath = Path().apply {
                moveTo(25f, -30f)
                cubicTo(5f, -10f, -5f, 15f, 0f, 35f)
                cubicTo(5f, 55f, 20f, 70f, 35f, 65f)
                lineTo(25f, 78f)
                cubicTo(5f, 70f, -15f, 50f, -18f, 25f)
                cubicTo(-20f, 0f, -5f, -20f, 15f, -40f)
                close()
            }
            drawPath(
                bellyPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFE082), goldenBelly, Color(0xFFFF8F00)),
                    startX = -20f,
                    endX = 35f
                )
            )

            // Horizontal Segment Ribs on Golden Belly
            for (step in 0..5) {
                val by = -20f + step * 16f
                val bx = -10f + step * 6f
                drawLine(
                    color = Color(0xFFBF360C),
                    start = Offset(bx - 8f, by - 2f),
                    end = Offset(bx + 14f, by + 4f),
                    strokeWidth = 2f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }

            // Dragon Scales Texture (Scalloped Gold Accents)
            for (r in 0..3) {
                val scY = -40f + r * 22f
                val scX = 18f + r * 10f
                drawArc(
                    color = Color(0x66FFD54F),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(scX, scY),
                    size = Size(14f, 10f),
                    style = Stroke(1.8f)
                )
            }

            // 3. FRONT DRAGON CLAW / TALON (Vuốt Rồng Hoàng Kim Vương Giả)
            val armPath = Path().apply {
                moveTo(10f, 40f)
                quadraticTo(-15f, 60f, -30f, 70f)
                lineTo(-25f, 82f)
                quadraticTo(-5f, 72f, 25f, 55f)
                close()
            }
            drawPath(armPath, color = scarletRed)

            // 4 Curved Razor Talons
            val clawBaseX = -32f
            val clawBaseY = 72f
            for (c in 0..3) {
                val cAngle = (150f + c * 24f) * PI / 180f
                val talonPath = Path().apply {
                    val tx = clawBaseX + (cos(cAngle) * 4f).toFloat()
                    val ty = clawBaseY + (sin(cAngle) * 4f).toFloat()
                    moveTo(tx, ty)
                    quadraticTo(
                        tx + (cos(cAngle) * 16f).toFloat() + 4f,
                        ty + (sin(cAngle) * 16f).toFloat() + 8f,
                        tx + (cos(cAngle) * 22f).toFloat(),
                        ty + (sin(cAngle) * 22f).toFloat() + 14f
                    )
                    quadraticTo(
                        tx + (cos(cAngle) * 12f).toFloat(),
                        ty + (sin(cAngle) * 12f).toFloat() + 6f,
                        tx - 3f, ty + 2f
                    )
                    close()
                }
                drawPath(
                    talonPath,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White, dragonGold, Color(0xFFFF8F00)),
                        start = Offset(clawBaseX, clawBaseY),
                        end = Offset(clawBaseX - 25f, clawBaseY + 25f)
                    )
                )
            }

            // 4. FLOWING FIERY MANE (Bờm Lửa Thần Long Cuồn Cuộn)
            // Multiple layers of burning flame locks streaming back
            val maneStrands = listOf(
                Triple(Offset(25f, -45f), 55f, -165f),
                Triple(Offset(10f, -55f), 70f, -145f),
                Triple(Offset(-5f, -62f), 80f, -125f),
                Triple(Offset(-25f, -65f), 90f, -110f),
                Triple(Offset(-42f, -50f), 75f, -90f),
                Triple(Offset(-30f, -25f), 65f, -80f)
            )

            maneStrands.forEachIndexed { idx, (root, length, angleBase) ->
                val wave = sin(gameTime * 4.5f + idx * 0.9f) * 8f
                val rad = (angleBase + wave) * PI / 180f
                val tipX = root.x + (cos(rad) * length).toFloat()
                val tipY = root.y + (sin(rad) * length).toFloat()

                // Flame Strand Outer
                val strandPath = Path().apply {
                    moveTo(root.x - 8f, root.y)
                    quadraticTo(
                        root.x + (cos(rad) * length * 0.5f).toFloat() - 10f,
                        root.y + (sin(rad) * length * 0.5f).toFloat() - 10f,
                        tipX, tipY
                    )
                    quadraticTo(
                        root.x + (cos(rad) * length * 0.5f).toFloat() + 10f,
                        root.y + (sin(rad) * length * 0.5f).toFloat() + 10f,
                        root.x + 8f, root.y
                    )
                    close()
                }
                drawPath(
                    strandPath,
                    brush = Brush.linearGradient(
                        colors = listOf(rubyRed, flameOrange, dragonGold),
                        start = root,
                        end = Offset(tipX, tipY)
                    )
                )

                // Flame Strand Bright Core
                val corePath = Path().apply {
                    moveTo(root.x - 3f, root.y)
                    quadraticTo(
                        root.x + (cos(rad) * length * 0.5f).toFloat(),
                        root.y + (sin(rad) * length * 0.5f).toFloat(),
                        tipX - (cos(rad) * 12f).toFloat(),
                        tipY - (sin(rad) * 12f).toFloat()
                    )
                    lineTo(root.x + 3f, root.y)
                    close()
                }
                drawPath(corePath, color = Color(0xFFFFF59D))
            }

            // 5. MAJESTIC DRAGON HEAD & SKULL (Đầu Thần Long Uy Dũng)
            val headPath = Path().apply {
                moveTo(-85f, 0f)       // Snout tip
                quadraticTo(-75f, -15f, -50f, -28f) // Upper bridge of nose
                quadraticTo(-25f, -36f, 5f, -25f)   // Brow & cranium
                lineTo(15f, -10f)                   // Temple
                quadraticTo(0f, 15f, -20f, 25f)     // Cheeks / jaw hinge
                lineTo(-55f, 18f)                   // Lower snout / upper lip
                close()
            }
            drawPath(
                headPath,
                brush = Brush.linearGradient(
                    colors = listOf(flameOrange, scarletRed, rubyRed),
                    start = Offset(-85f, -20f),
                    end = Offset(15f, 20f)
                )
            )

            // Crown / Forehead Plate with Golden Ridges
            val browPath = Path().apply {
                moveTo(-55f, -24f)
                quadraticTo(-25f, -40f, 8f, -22f)
                lineTo(0f, -14f)
                quadraticTo(-28f, -28f, -50f, -16f)
                close()
            }
            drawPath(browPath, color = dragonGold)

            // DRAGON PEARL ON FOREHEAD (Ngọc Long Châu Giữa Trán)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFFFD54F), Color(0xFFFF3D00)),
                    center = Offset(-18f, -22f),
                    radius = 9f
                ),
                radius = 7.5f,
                center = Offset(-18f, -22f)
            )
            drawCircle(color = Color.White, radius = 2.5f, center = Offset(-20f, -24f))

            // Snout Nostril breathing smoke
            val nostrilPath = Path().apply {
                moveTo(-75f, -8f)
                quadraticTo(-72f, -14f, -65f, -10f)
            }
            drawPath(nostrilPath, color = darkCrimson, style = Stroke(3f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            // Subtle smoke puff
            drawCircle(color = Color(0x33FF9800), radius = 5f, center = Offset(-78f, -14f))

            // 6. GRAND GOLDEN ANTLERS / HORNS (Sừng Gạc Rồng Hoàng Kim Vương Giả)
            // Left main antler sweeping back with 3 distinct branch tines
            val hornPath = Path().apply {
                moveTo(-20f, -32f)
                cubicTo(-5f, -65f, 15f, -85f, 40f, -100f) // Main tip
                quadraticTo(28f, -80f, 20f, -65f)
                // Branch 1
                lineTo(45f, -75f)
                quadraticTo(30f, -60f, 15f, -50f)
                // Branch 2
                lineTo(30f, -45f)
                quadraticTo(15f, -40f, 5f, -25f)
                close()
            }
            drawPath(
                hornPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color.White, dragonGold, Color(0xFFFF8F00)),
                    start = Offset(-20f, -32f),
                    end = Offset(40f, -100f)
                )
            )

            // Right secondary antler in background
            val backHornPath = Path().apply {
                moveTo(-5f, -38f)
                cubicTo(10f, -70f, 35f, -95f, 60f, -110f)
                quadraticTo(45f, -88f, 35f, -70f)
                lineTo(55f, -80f)
                quadraticTo(38f, -62f, 20f, -48f)
                close()
            }
            drawPath(
                backHornPath,
                brush = Brush.linearGradient(
                    colors = listOf(dragonGold, Color(0xFFE65100)),
                    start = Offset(-5f, -38f),
                    end = Offset(60f, -110f)
                )
            )

            // 7. FIERCE GLOWING DRAGON EYE (Mắt Rồng Rực Lửa Uy Nghi)
            val eyeSocketPath = Path().apply {
                moveTo(-42f, -18f)
                quadraticTo(-28f, -28f, -14f, -18f)
                quadraticTo(-28f, -10f, -42f, -18f)
                close()
            }
            drawPath(eyeSocketPath, color = Color(0xFF1A0000))

            // Burning Golden Iris
            val irisPath = Path().apply {
                moveTo(-38f, -18f)
                quadraticTo(-28f, -25f, -18f, -18f)
                quadraticTo(-28f, -12f, -38f, -18f)
                close()
            }
            drawPath(
                irisPath,
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFFFEB3B), Color(0xFFFF6D00)),
                    center = Offset(-28f, -18f),
                    radius = 10f
                )
            )

            // Sharp Slit Pupil
            val pupilPath = Path().apply {
                moveTo(-28f, -24f)
                quadraticTo(-26f, -18f, -28f, -12f)
                quadraticTo(-30f, -18f, -28f, -24f)
                close()
            }
            drawPath(pupilPath, color = Color(0xFF310000))

            // Eye Highlights & Blazing Aura Trail
            drawCircle(color = Color.White, radius = 2f, center = Offset(-25f, -20f))
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(dragonGold, Color.Transparent),
                    startX = -16f,
                    endX = 12f
                ),
                start = Offset(-16f, -18f),
                end = Offset(12f, -20f),
                strokeWidth = 2.5f
            )

            // 8. JAWS, FANGS & BREATH OF FIRE (Hàm Răng Nanh & Miệng Lửa)
            if (state == DragonState.ATTACKING || mouthGlow > 0.05f) {
                // Wide Roaring Open Mouth
                val openThroat = Path().apply {
                    moveTo(-80f, 0f)
                    lineTo(-35f, 6f)
                    lineTo(-60f, 32f)
                    close()
                }
                drawPath(openThroat, color = Color(0xFF210000))

                // Lower Jaw opened
                val lowerJaw = Path().apply {
                    moveTo(-60f, 32f)
                    lineTo(-25f, 25f)
                    lineTo(-40f, 42f)
                    close()
                }
                drawPath(lowerJaw, color = rubyRed)

                // Upper Dagger Fangs
                val upperFangs = Path().apply {
                    moveTo(-75f, 0f); lineTo(-71f, 10f); lineTo(-67f, 0f)
                    moveTo(-62f, 2f); lineTo(-58f, 14f); lineTo(-54f, 2f)
                    moveTo(-48f, 4f); lineTo(-45f, 11f); lineTo(-42f, 4f)
                }
                drawPath(upperFangs, color = Color.White)

                // Lower Fangs
                val lowerFangs = Path().apply {
                    moveTo(-58f, 30f); lineTo(-55f, 20f); lineTo(-52f, 30f)
                    moveTo(-45f, 27f); lineTo(-42f, 18f); lineTo(-39f, 27f)
                }
                drawPath(lowerFangs, color = Color.White)

                // VORTEX OF RADIANT FIRE CHARGING IN MOUTH
                val glowRadius = 42f * mouthGlow.coerceIn(0.2f, 1.2f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFFFF176),
                            Color(0xFFFF9100),
                            Color(0xFFFF1744),
                            Color.Transparent
                        ),
                        center = Offset(-55f, 12f),
                        radius = glowRadius
                    ),
                    radius = glowRadius,
                    center = Offset(-55f, 12f)
                )

                // Blazing particle sparks from throat
                for (s in 0..4) {
                    val sAngle = (s * 45f + gameTime * 200f) * PI / 180f
                    val sDist = 18f * mouthGlow
                    drawCircle(
                        color = Color(0xFFFFD54F),
                        radius = 3.5f,
                        center = Offset(-55f - (cos(sAngle) * sDist).toFloat(), 12f + (sin(sAngle) * sDist).toFloat())
                    )
                }
            } else {
                // Closed Dignified Smirk
                val smirkLine = Path().apply {
                    moveTo(-80f, 0f)
                    quadraticTo(-50f, 8f, -25f, 15f)
                }
                drawPath(smirkLine, color = darkCrimson, style = Stroke(3.5f, cap = androidx.compose.ui.graphics.StrokeCap.Round))

                // Menacing Visible Canine Fang
                val fang = Path().apply {
                    moveTo(-68f, 1f)
                    lineTo(-64f, 10f)
                    lineTo(-60f, 2f)
                    close()
                }
                drawPath(fang, color = Color.White)
                // Chin Flame Beard (Râu Lửa Dưới Cằm)
                val chinBeard = Path().apply {
                    moveTo(-55f, 15f)
                    quadraticTo(-65f, 30f, -75f, 38f + sin(gameTime * 4f) * 6f)
                    quadraticTo(-55f, 26f, -42f, 18f)
                    close()
                }
                drawPath(
                    chinBeard,
                    brush = Brush.linearGradient(
                        colors = listOf(rubyRed, flameOrange, dragonGold),
                        start = Offset(-55f, 15f),
                        end = Offset(-75f, 38f)
                    )
                )
            }

            // 9. LONG SILKEN GOLDEN WHISKERS (Râu Rồng Long Vân Bay Lượn Trong Gió)
            // Primary Top Whisker (Sweeping forward and looping down gracefully)
            val w1Wave = sin(gameTime * 3.8f) * 12f
            val w1Wave2 = cos(gameTime * 4.2f) * 8f
            val topWhisker = Path().apply {
                moveTo(-70f, -4f)
                cubicTo(
                    -95f, -15f + w1Wave,
                    -120f, 10f + w1Wave2,
                    -135f, 30f + w1Wave
                )
                cubicTo(
                    -145f, 45f + w1Wave,
                    -130f, 65f,
                    -105f, 75f + w1Wave2
                )
            }
            // Whisker Glow
            drawPath(
                topWhisker,
                color = Color(0x66FFD54F),
                style = Stroke(5f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
            // Whisker Gold Core
            drawPath(
                topWhisker,
                brush = Brush.linearGradient(
                    colors = listOf(Color.White, dragonGold, Color(0xFFFF9100)),
                    start = Offset(-70f, -4f),
                    end = Offset(-105f, 75f)
                ),
                style = Stroke(2.8f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            // Secondary Bottom Whisker
            val w2Wave = sin(gameTime * 4.2f + 1.5f) * 10f
            val bottomWhisker = Path().apply {
                moveTo(-66f, 4f)
                cubicTo(
                    -85f, 8f + w2Wave,
                    -105f, 32f - w2Wave,
                    -115f, 52f + w2Wave
                )
            }
            drawPath(
                bottomWhisker,
                brush = Brush.linearGradient(
                    colors = listOf(dragonGold, Color(0xFFFF9800)),
                    start = Offset(-66f, 4f),
                    end = Offset(-115f, 52f)
                ),
                style = Stroke(2.2f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
        }
    }
}


// -------------------------------------------------------------
// TORRENTIAL FIRE BREATH STREAM (LUỒNG LỬA RỒNG CUỘN TRÀO)
// Matches the user's reference image: continuous expanding jet of fire
// spewing from the jaws, with billowing flame clouds and radiant embers
// -------------------------------------------------------------
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

