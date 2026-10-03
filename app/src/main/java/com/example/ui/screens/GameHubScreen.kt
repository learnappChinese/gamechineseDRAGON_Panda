package com.example.ui.screens

import com.example.ui.components.GameArt
import androidx.compose.ui.layout.ContentScale

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChineseDarkRed
import com.example.ui.theme.ChineseRed
import com.example.ui.theme.DragonOrange
import com.example.ui.theme.ImperialGold

@Composable
fun GameHubScreen(
    onSelectBossBattle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    val filters = listOf("Tất cả", "Đang phát triển", "Sắp ra mắt")

    Box(modifier = modifier.fillMaxSize()) {
        GameArt("backgrounds/game_hub_bg.png", Modifier.matchParentSize(), contentScale = ContentScale.Crop)
        Box(Modifier.matchParentSize().background(Color(0xE6FFF7E8)))
    Column(
        modifier = Modifier
            .fillMaxSize()

            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Trò chơi",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1E1E24)
        )
        Text(
            text = "Học tiếng Trung qua những trò chơi thú vị!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF757575)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DragonOrange,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 1. Featured Card: Boss Battle (Ưu tiên phát triển)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .clickable { onSelectBossBattle() }
                .testTag("game_boss_battle_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Panda Avatar with Headband Box
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFCC80), Color(0xFFFF8A65))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    GameArt("icons/boss_battle_thumb.png", Modifier.fillMaxSize(), "Boss Battle", ContentScale.Crop)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Boss Battle",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1E1E24)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Ưu tiên phát triển",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChineseRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Trả lời đúng để đánh bại Boss!\nHọc từ vựng, ngữ pháp qua trận chiến thú vị.",
                        fontSize = 12.sp,
                        color = Color(0xFF616161),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Radical Builder
        SecondaryGameCard(
            title = "Radical Builder",
            description = "Xây chữ Hán từ bộ thủ",
            emoji = "🧱",
            badge = "Sắp ra mắt"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Tone Ninja
        SecondaryGameCard(
            title = "Tone Ninja",
            description = "Luyện thanh điệu như ninja",
            emoji = "🥷",
            badge = "Sắp ra mắt"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Chinese Restaurant
        SecondaryGameCard(
            title = "Chinese Restaurant",
            description = "Phục vụ món ăn bằng tiếng Trung",
            emoji = "🥟",
            badge = "Sắp ra mắt"
        )
    }
    }
}

@Composable
fun SecondaryGameCard(
    title: String,
    description: String,
    emoji: String,
    badge: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF0EAE1)),
                contentAlignment = Alignment.Center
            ) {
                GameArt(when (title) {
                    "Radical Builder" -> "backgrounds/radical_builder_bg.png"
                    "Tone Ninja" -> "characters/panda_ninja.png"
                    else -> "characters/panda_chef.png"
                }, Modifier.fillMaxSize(), title, ContentScale.Crop)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E1E24)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEEEEEE))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

