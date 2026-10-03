package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.ChineseTTSHelper
import com.example.data.model.ChineseOption
import com.example.game.BattleArenaCanvas
import com.example.ui.theme.ChineseDarkRed
import com.example.ui.theme.ChineseRed
import com.example.ui.theme.ComboOrange
import com.example.ui.theme.DragonOrange
import com.example.ui.theme.HpBossRed
import com.example.ui.theme.HpPlayerGreen
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.JadeGreen
import com.example.ui.viewmodel.BossBattleViewModel

@Composable
fun BossBattleScreen(
    viewModel: BossBattleViewModel,
    onBackToHub: () -> Unit,
    onVictory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val ttsHelper = remember { ChineseTTSHelper(context) }

    BackHandler {
        viewModel.togglePause()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF140B18))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. TOP BAR: Pause button, Boss Info & HP, Speaker button
            BossBattleTopBar(
                bossName = uiState.bossStats.name,
                bossLevel = uiState.bossStats.level,
                currentHp = uiState.bossStats.currentHp,
                maxHp = uiState.bossStats.maxHp,
                onPauseClick = { viewModel.togglePause() },
                onSoundClick = {
                    uiState.currentQuestion?.speechText?.let { ttsHelper.speak(it) }
                }
            )

            // 2. BATTLE ARENA (2D GAME CANVAS LAYER)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.15f)
            ) {
                // Flame/Physics Canvas
                BattleArenaCanvas(
                    pandaState = uiState.pandaState,
                    dragonState = uiState.dragonState,
                    isArrowActive = uiState.isArrowActive,
                    arrowProgress = uiState.arrowProgress,
                    isFireActive = uiState.isFireActive,
                    fireProgress = uiState.fireProgress,
                    dragonMouthGlow = uiState.dragonMouthGlow,
                    cameraShakeOffset = uiState.cameraShakeOffset,
                    screenFlashAlpha = uiState.screenFlashAlpha,
                    floatingDamages = uiState.floatingDamages,
                    modifier = Modifier.fillMaxSize()
                )

                // Arena Bottom HUD Overlay: Player HP Bar (Left) & Combo Badge (Right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Player HP Bar
                    PlayerHpBadge(
                        currentHp = uiState.playerStats.currentHp,
                        maxHp = uiState.playerStats.maxHp
                    )

                    // Combo Badge
                    if (uiState.playerStats.combo > 0) {
                        ComboBadge(combo = uiState.playerStats.combo)
                    }
                }
            }

            // 3. QUIZ & ANSWER PANEL (BOTTOM HALF)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F6F0)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Feedback Banner (Chính xác! or Sai rồi!)
                    AnimatedVisibility(
                        visible = uiState.feedbackMessage != null,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (uiState.isCorrectSelection == true) JadeGreen else ChineseRed
                                )
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.feedbackMessage ?: "",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Question Prompt
                    val question = uiState.currentQuestion
                    if (question != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                IconButton(
                                    onClick = { ttsHelper.speak(question.speechText) },
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE0F2F1))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Nghe phát âm",
                                        tint = Color(0xFF00897B)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = question.prompt,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E24)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = question.instruction,
                                fontSize = 12.sp,
                                color = Color(0xFF757575)
                            )
                        }

                        // 4 Option Buttons in 2x2 Grid
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val chunked = question.options.chunked(2)
                            chunked.forEach { rowOptions ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    rowOptions.forEach { opt ->
                                        AnswerOptionCard(
                                            option = opt,
                                            isSelected = (uiState.selectedOptionId == opt.id),
                                            isCorrectTarget = (opt.id == question.correctOptionId),
                                            isProcessing = uiState.isProcessingAnswer,
                                            onSelect = { viewModel.selectOption(opt.id) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        // 4. Victory / Defeat Overlays
        if (uiState.isVictory) {
            BattleVictoryDialog(
                onContinue = {
                    onVictory()
                    onBackToHub()
                }
            )
        }

        if (uiState.isDefeat) {
            BattleDefeatDialog(
                onRetry = { viewModel.startNewBattle() },
                onBackToHub = onBackToHub
            )
        }

        // 5. Pause Dialog
        if (uiState.isPaused) {
            AlertDialog(
                onDismissRequest = { viewModel.togglePause() },
                title = { Text("Tạm dừng trận đấu", fontWeight = FontWeight.Bold) },
                text = { Text("Bạn có muốn tiếp tục trận chiến hay quay về Game Hub?") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.togglePause() },
                        colors = ButtonDefaults.buttonColors(containerColor = DragonOrange)
                    ) {
                        Text("Tiếp tục")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            viewModel.togglePause()
                            onBackToHub()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575))
                    ) {
                        Text("Thoát về Hub")
                    }
                }
            )
        }
    }
}

// -------------------------------------------------------------
// TOP BAR COMPONENT
// -------------------------------------------------------------
@Composable
private fun BossBattleTopBar(
    bossName: String,
    bossLevel: Int,
    currentHp: Int,
    maxHp: Int,
    onPauseClick: () -> Unit,
    onSoundClick: () -> Unit
) {
    val hpFraction = (currentHp / maxHp.toFloat()).coerceIn(0f, 1f)
    val animatedHp by animateFloatAsState(
        targetValue = hpFraction,
        animationSpec = tween(durationMillis = 350),
        label = "boss_hp_anim"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pause Button
        IconButton(
            onClick = onPauseClick,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0x33FFFFFF))
        ) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Tạm dừng",
                tint = Color.White
            )
        }

        // Boss Info & Animated HP Bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f).padding(horizontal = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ImperialGold)
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        "Lv. $bossLevel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A1000)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = bossName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$currentHp/$maxHp",
                    fontSize = 11.sp,
                    color = Color(0xFFFFCC80)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Boss HP Bar with Red/Orange Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(Color(0x44000000))
                    .border(1.dp, Color(0x66FF8A65), RoundedCornerShape(7.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedHp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF1744), Color(0xFFFF5252), Color(0xFFFF8A65))
                            )
                        )
                )
            }
        }

        // Sound / Audio icon
        IconButton(
            onClick = onSoundClick,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0x33FFFFFF))
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Âm thanh",
                tint = Color.White
            )
        }
    }
}

// -------------------------------------------------------------
// PLAYER HP BADGE
// -------------------------------------------------------------
@Composable
private fun PlayerHpBadge(currentHp: Int, maxHp: Int) {
    val hpFraction = (currentHp / maxHp.toFloat()).coerceIn(0f, 1f)
    val animatedHp by animateFloatAsState(
        targetValue = hpFraction,
        animationSpec = tween(durationMillis = 350),
        label = "player_hp_anim"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xBB190D24))
            .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Panda mini avatar
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFD54F)),
            contentAlignment = Alignment.Center
        ) {
            Text("🐼", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = "$currentHp / $maxHp",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(Color(0x66000000))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedHp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00E676), Color(0xFF69F0AE))
                            )
                        )
                )
            }
        }
    }
}

// -------------------------------------------------------------
// COMBO BADGE
// -------------------------------------------------------------
@Composable
private fun ComboBadge(combo: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFF3D00), Color(0xFFFF9100))
                )
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🔥", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Combo x$combo",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

// -------------------------------------------------------------
// ANSWER OPTION CARD
// -------------------------------------------------------------
@Composable
private fun AnswerOptionCard(
    option: ChineseOption,
    isSelected: Boolean,
    isCorrectTarget: Boolean,
    isProcessing: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine card background and border colors based on selection state
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected && isCorrectTarget -> Color(0xFFE8F5E9)
            isSelected && !isCorrectTarget -> Color(0xFFFFEBEE)
            isProcessing && isCorrectTarget -> Color(0xFFE8F5E9)
            else -> Color.White
        },
        label = "opt_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected && isCorrectTarget -> JadeGreen
            isSelected && !isCorrectTarget -> ChineseRed
            isProcessing && isCorrectTarget -> JadeGreen
            else -> Color(0xFFE0DDD5)
        },
        label = "opt_border"
    )

    Card(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = !isProcessing) { onSelect() }
            .testTag("option_${option.id}"),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = option.hanzi,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    isSelected && isCorrectTarget -> JadeGreen
                    isSelected && !isCorrectTarget -> ChineseRed
                    else -> Color(0xFF1E1E24)
                }
            )
            Text(
                text = option.pinyin,
                fontSize = 11.sp,
                color = Color(0xFF757575)
            )
        }
    }
}
