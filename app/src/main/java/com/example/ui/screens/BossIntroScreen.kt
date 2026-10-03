package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DragonOrange
import com.example.ui.theme.ImperialGold

@Composable
fun BossIntroScreen(
    onBack: () -> Unit,
    onStartBattle: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E0815),
                        Color(0xFF4A1020),
                        Color(0xFF1E0A24),
                        Color(0xFF120517)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Navigation Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Epic Hero Artwork Card with Panda & Fire Dragon
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF8B0000),
                                    Color(0xFFD32F2F),
                                    Color(0xFF2E0815)
                                )
                            )
                        )
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Golden moon aura
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFFEEB3), Color(0x33FFB300), Color.Transparent),
                                center = Offset(w * 0.75f, h * 0.45f),
                                radius = 90f
                            ),
                            radius = 90f,
                            center = Offset(w * 0.75f, h * 0.45f)
                        )

                        // Clashing fire energy beams
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Color(0xFFFFD54F), Color(0xFFFF3D00), Color.Transparent)
                            ),
                            start = Offset(w * 0.35f, h * 0.5f),
                            end = Offset(w * 0.65f, h * 0.5f),
                            strokeWidth = 3f
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Panda Hero Side
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x33000000))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text("🐼🏹", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Đại Hiệp Panda", color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("HP 200", color = Color(0xFF69F0AE), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // VS Badge
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(0xFFFFEB3B), Color(0xFFFF3D00))
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Text("VS", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }

                        // Fire Dragon Boss Side
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x33000000))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text("🐲🔥", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Rồng Lửa Lv.3", color = Color(0xFFFF8A65), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("HP 500", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title & Subtitle
            Text(
                text = "Boss Battle",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Đánh bại Boss bằng kiến thức tiếng Trung!",
                fontSize = 15.sp,
                color = Color(0xFFFFCC80),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Feature Highlights
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FeatureRow(
                    emoji = "📖",
                    title = "Học từ vựng qua trận chiến",
                    subtitle = "Mỗi câu trả lời đúng tung một mũi tên sát thương cực mạnh!"
                )
                FeatureRow(
                    emoji = "⚡",
                    title = "Càng đúng càng mạnh",
                    subtitle = "Duy trì chuỗi Combo để nhân đôi lượng damage dồn lên Boss."
                )
                FeatureRow(
                    emoji = "🏆",
                    title = "Thu thập phần thưởng hấp dẫn",
                    subtitle = "Nhận ngay +100 XP, 50 Vàng và Rương báu khi chiến thắng."
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Start Battle CTA Button
            Button(
                onClick = onStartBattle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("btn_start_battle"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DragonOrange
                ),
                elevation = ButtonDefaults.buttonElevation(6.dp)
            ) {
                Text(
                    text = "Bắt đầu chơi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FeatureRow(
    emoji: String,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x33FFFFFF))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0x22FFA000)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFFD7CCC8)
            )
        }
    }
}
