package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChineseRed
import com.example.ui.theme.DragonOrange

data class NavigationTabItem(
    val title: String,
    val icon: ImageVector,
    val hasBadge: Boolean = false
)

@Composable
fun AppBottomNavigation(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavigationTabItem("Trang chủ", Icons.Default.Home),
        NavigationTabItem("Học tập", Icons.Default.MenuBook),
        NavigationTabItem("Trò chơi", Icons.Default.SportsEsports, hasBadge = true),
        NavigationTabItem("Tiến độ", Icons.Default.BarChart),
        NavigationTabItem("Cá nhân", Icons.Default.Person)
    )

    NavigationBar(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = Color.White,
        tonalElevation = 6.dp
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = (selectedTabIndex == index)
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    if (item.hasBadge) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = ChineseRed,
                                    contentColor = Color.White
                                ) {
                                    Text("HOT", fontSize = 8.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(text = item.title, fontSize = 11.sp)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DragonOrange,
                    selectedTextColor = DragonOrange,
                    indicatorColor = Color(0xFFFFE0B2),
                    unselectedIconColor = Color(0xFF757575),
                    unselectedTextColor = Color(0xFF757575)
                )
            )
        }
    }
}
