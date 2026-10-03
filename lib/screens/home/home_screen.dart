import 'package:flutter/material.dart';
import '../../theme/app_colors.dart';
import '../../widgets/game_asset_image.dart';
import '../../widgets/game_button.dart';
import '../../widgets/stat_pill.dart';
import '../../widgets/bottom_navigation.dart';

class HomeScreen extends StatelessWidget {
  final VoidCallback onNavigateToGameHub;

  const HomeScreen({Key? key, required this.onNavigateToGameHub}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.backgroundLight,
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // 1. TOP HEADER
              Row(
                children: [
                  Container(
                    width: 48,
                    height: 48,
                    decoration: const BoxDecoration(
                      color: AppColors.primaryGold,
                      shape: BoxShape.circle,
                    ),
                    clipBehavior: Clip.antiAlias,
                    child: const GameAssetImage(
                      assetPath: 'assets/images/characters/panda_avatar.png',
                      fallbackEmoji: '🐼',
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: const [
                        Text(
                          'Xin chào!',
                          style: TextStyle(
                            fontSize: 19,
                            fontWeight: FontWeight.w900,
                            color: AppColors.textDark,
                          ),
                        ),
                        SizedBox(height: 2),
                        Text(
                          'Tiếp tục hành trình học tiếng Trung nào!',
                          style: TextStyle(
                            fontSize: 12,
                            color: AppColors.textMuted,
                          ),
                        ),
                      ],
                    ),
                  ),
                  IconButton(
                    icon: const Icon(Icons.settings_outlined, color: AppColors.textDark),
                    onPressed: () {},
                  ),
                  IconButton(
                    icon: const Icon(Icons.notifications_none_rounded, color: AppColors.textDark),
                    onPressed: () {},
                  ),
                ],
              ),
              const SizedBox(height: 16),

              // 2. STATS ROW (7 Ngày học, 12 Bài học, 156 Điểm XP)
              Row(
                children: const [
                  StatPill(emoji: '🔥', count: '7', label: 'Ngày học', color: AppColors.primaryOrange),
                  SizedBox(width: 10),
                  StatPill(emoji: '📖', count: '12', label: 'Bài học', color: AppColors.successGreen),
                  SizedBox(width: 10),
                  StatPill(emoji: '⭐', count: '156', label: 'Điểm XP', color: AppColors.primaryGold),
                ],
              ),
              const SizedBox(height: 18),

              // 3. HERO AREA (Ancient City & Panda Warrior)
              Container(
                height: 230,
                width: double.infinity,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(24),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.14),
                      blurRadius: 12,
                      offset: const Offset(0, 5),
                    ),
                  ],
                ),
                child: Stack(
                  children: [
                    // Background Image
                    Positioned.fill(
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(24),
                        child: Container(
                          decoration: const BoxDecoration(
                            gradient: LinearGradient(
                              colors: [Color(0xFF8B2500), Color(0xFFE65100), Color(0xFF1E293B)],
                              begin: Alignment.topCenter,
                              end: Alignment.bottomCenter,
                            ),
                          ),
                          child: const GameAssetImage(
                            assetPath: 'assets/images/backgrounds/home_ancient_town.png',
                            fit: BoxFit.cover,
                            fallbackEmoji: '🏯',
                          ),
                        ),
                      ),
                    ),
                    // Panda Warrior Centered
                    Positioned(
                      top: 15,
                      bottom: 70,
                      left: 0,
                      right: 0,
                      child: const Center(
                        child: GameAssetImage(
                          assetPath: 'assets/images/characters/panda_warrior.png',
                          fallbackEmoji: '🐼🏹',
                        ),
                      ),
                    ),
                    // Large "Tiếp tục học >" Button
                    Positioned(
                      bottom: 14,
                      left: 18,
                      right: 18,
                      child: GameButton(
                        text: 'Tiếp tục học >',
                        gradient: AppColors.orangeGoldGradient,
                        height: 52,
                        borderRadius: 20,
                        onTap: onNavigateToGameHub,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 20),

              // 4. QUICK ACTION GRID (6 Buttons)
              GridView.count(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                crossAxisCount: 3,
                crossAxisSpacing: 12,
                mainAxisSpacing: 12,
                childAspectRatio: 1.05,
                children: [
                  _buildActionCard(Icons.menu_book_rounded, 'Học từ vựng', AppColors.primaryOrange, () {}),
                  _buildActionCard(Icons.headphones_rounded, 'Luyện nghe', Colors.purple, () {}),
                  _buildActionCard(Icons.mic_rounded, 'Luyện nói', Colors.teal, () {}),
                  _buildActionCard(Icons.sports_esports_rounded, 'Trò chơi', Colors.deepOrange, onNavigateToGameHub),
                  _buildActionCard(Icons.emoji_events_rounded, 'Thử thách', AppColors.primaryGold, () {}),
                  _buildActionCard(Icons.storefront_rounded, 'Cửa hàng', AppColors.dangerRed, () {}),
                ],
              ),
            ],
          ),
        ),
      ),
      bottomNavigationBar: GameBottomNavBar(
        currentIndex: 0,
        onTabSelected: (idx) {
          if (idx == 2) onNavigateToGameHub();
        },
      ),
    );
  }

  Widget _buildActionCard(IconData icon, String title, Color color, VoidCallback onTap) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(20),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              padding: const EdgeInsets.all(10),
              decoration: BoxDecoration(
                color: color.withOpacity(0.12),
                shape: BoxShape.circle,
              ),
              child: Icon(icon, color: color, size: 26),
            ),
            const SizedBox(height: 8),
            Text(
              title,
              style: const TextStyle(
                fontSize: 12,
                fontWeight: FontWeight.w800,
                color: AppColors.textDark,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
