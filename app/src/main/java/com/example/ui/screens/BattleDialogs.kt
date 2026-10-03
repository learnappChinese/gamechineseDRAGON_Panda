package com.example.ui.screens

import com.example.ui.components.GameArt
import androidx.compose.ui.layout.ContentScale

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DragonOrange
import com.example.ui.theme.ImperialGold
import com.example.ui.theme.JadeGreen

@Composable
fun BattleVictoryDialog(
    onContinue: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box {
                GameArt("backgrounds/victory_bg.png", Modifier.matchParentSize(), contentScale = ContentScale.Crop)
                Box(Modifier.matchParentSize().background(Color(0xD9FFF7E8)))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 3 Shiny Golden Stars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐", fontSize = 28.sp)
                    Text("⭐", fontSize = 42.sp)
                    Text("⭐", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Victory Banner
                Text(
                    text = "Chiến thắng!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD32F2F)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Cheering Panda Avatar
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFFF9C4), Color(0xFFFFE082))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    GameArt("characters/panda_victory.png", Modifier.fillMaxSize(), "Panda chiến thắng")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Phần thưởng",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Rewards Row (XP, Gold, Chest)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFF8E1))
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RewardItem(icon = "🔷", label = "+100 XP")
                    RewardItem(icon = "🪙", label = "+50 Vàng")
                    RewardItem(icon = "🎁", label = "+1 Rương")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_victory_continue"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JadeGreen)
                ) {
                    Text(
                        text = "Tiếp tục",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            }
        }
    }
}

@Composable
fun BattleDefeatDialog(
    onRetry: () -> Unit,
    onBackToHub: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF231826)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box {
                GameArt("backgrounds/defeat_bg.png", Modifier.matchParentSize(), contentScale = ContentScale.Crop)
                Box(Modifier.matchParentSize().background(Color(0x99120B24)))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Thất bại!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFF5252)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dizzy Panda Avatar
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF38243C)),
                    contentAlignment = Alignment.Center
                ) {
                    GameArt("characters/panda_dizzy.png", Modifier.fillMaxSize(), "Panda nghỉ ngơi")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Đừng bỏ cuộc!\nHãy luyện tập thêm để mạnh hơn nhé!",
                    fontSize = 14.sp,
                    color = Color(0xFFD7CCC8),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onRetry,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("btn_defeat_retry"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ImperialGold)
                    ) {
                        Text("Thử lại", fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
                    }

                    Button(
                        onClick = onBackToHub,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("btn_defeat_hub"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64))
                    ) {
                        Text("Về Game Hub", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun RewardItem(icon: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4E342E)
        )
    }
}

