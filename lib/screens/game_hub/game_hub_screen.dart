import 'package:flutter/material.dart';
import '../../theme/app_colors.dart';
import '../../widgets/game_asset_image.dart';
import '../../widgets/bottom_navigation.dart';

class GameHubScreen extends StatelessWidget {
  final VoidCallback onSelectBossBattle;
  final VoidCallback onSelectRadicalBuilder;
  final VoidCallback onSelectToneNinja;
  final VoidCallback onSelectRestaurant;
  final VoidCallback onSelectQuickAnswer;
  final VoidCallback onBackToHome;

  const GameHubScreen({
    Key? key,
    required this.onSelectBossBattle,
    required this.onSelectRadicalBuilder,
    required this.onSelectToneNinja,
    required this.onSelectRestaurant,
    required this.onSelectQuickAnswer,
    required this.onBackToHome,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF131D31),
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios_new_rounded, color: Colors.white),
          onPressed: onBackToHome,
        ),
        title: const Text(
          'Trò chơi',
          style: TextStyle(fontWeight: FontWeight.w900, color: Colors.white, fontSize: 20),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Học tiếng Trung qua những trò chơi thú vị!',
              style: TextStyle(fontSize: 13, color: Color(0xFF94A3B8)),
            ),
            const SizedBox(height: 16),

            // 1. BOSS BATTLE (Hero Highlighted Card)
            _buildGameBanner(
              title: 'Boss Battle',
              description: 'Đánh bại Boss bằng kiến thức tiếng Trung!',
              assetPath: 'assets/images/icons/boss_battle_thumb.png',
              fallbackEmoji: '🐲🔥',
              badge: 'HOT',
              gradient: const LinearGradient(
                colors: [Color(0xFF8B0000), Color(0xFFD32F2F), Color(0xFFFF6F00)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              onTap: onSelectBossBattle,
            ),
            const SizedBox(height: 14),

            // 2. XÂY CHỮ HÁN
            _buildGameBanner(
              title: 'Xây chữ Hán',
              description: 'Xây chữ Hán từ bộ thủ',
              assetPath: 'assets/images/icons/radical_builder_thumb.png',
              fallbackEmoji: '🧱',
              gradient: const LinearGradient(
                colors: [Color(0xFF0F766E), Color(0xFF0D9488)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              onTap: onSelectRadicalBuilder,
            ),
            const SizedBox(height: 14),

            // 3. TONE NINJA
            _buildGameBanner(
              title: 'Tone Ninja',
              description: 'Luyện thanh điệu như ninja',
              assetPath: 'assets/images/icons/tone_ninja_thumb.png',
              fallbackEmoji: '🥷',
              gradient: const LinearGradient(
                colors: [Color(0xFF4338CA), Color(0xFF6366F1)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              onTap: onSelectToneNinja,
            ),
            const SizedBox(height: 14),

            // 4. NHÀ HÀNG TRUNG HOA
            _buildGameBanner(
              title: 'Nhà hàng Trung Hoa',
              description: 'Phục vụ món ăn bằng tiếng Trung',
              assetPath: 'assets/images/icons/restaurant_thumb.png',
              fallbackEmoji: '🥟',
              gradient: const LinearGradient(
                colors: [Color(0xFFB45309), Color(0xFFD97706)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              onTap: onSelectRestaurant,
            ),
            const SizedBox(height: 14),

            // 5. TRẢ LỜI NHANH
            _buildGameBanner(
              title: 'Trả lời nhanh',
              description: 'Thử thách phản xạ tiếng Trung',
              assetPath: 'assets/images/icons/quick_answer_thumb.png',
              fallbackEmoji: '⚡',
              gradient: const LinearGradient(
                colors: [Color(0xFF0284C7), Color(0xFF0EA5E9)],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
              onTap: onSelectQuickAnswer,
            ),
            const SizedBox(height: 16),
          ],
        ),
      ),
      bottomNavigationBar: GameBottomNavBar(
        currentIndex: 2,
        onTabSelected: (idx) {
          if (idx == 0) onBackToHome();
        },
      ),
    );
  }

  Widget _buildGameBanner({
    required String title,
    required String description,
    required String assetPath,
    required String fallbackEmoji,
    required Gradient gradient,
    required VoidCallback onTap,
    String? badge,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        height: 94,
        padding: const EdgeInsets.symmetric(horizontal: 16),
        decoration: BoxDecoration(
          gradient: gradient,
          borderRadius: BorderRadius.circular(22),
          border: Border.all(color: Colors.white.withOpacity(0.2), width: 1.2),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.25),
              blurRadius: 10,
              offset: const Offset(0, 4),
            ),
          ],
        ),
        child: Row(
          children: [
            Container(
              width: 58,
              height: 58,
              decoration: BoxDecoration(
                color: Colors.black.withOpacity(0.22),
                shape: BoxShape.circle,
              ),
              clipBehavior: Clip.antiAlias,
              child: GameAssetImage(
                assetPath: assetPath,
                fallbackEmoji: fallbackEmoji,
              ),
            ),
            const SizedBox(width: 14),
            Expanded(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(
                        title,
                        style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.w900,
                          color: Colors.white,
                        ),
                      ),
                      if (badge != null) ...[
                        const SizedBox(width: 8),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                          decoration: BoxDecoration(
                            color: AppColors.primaryGold,
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Text(
                            badge,
                            style: const TextStyle(
                              fontSize: 9,
                              fontWeight: FontWeight.w900,
                              color: Color(0xFF7F0000),
                            ),
                          ),
                        ),
                      ],
                    ],
                  ),
                  const SizedBox(height: 4),
                  Text(
                    description,
                    style: const TextStyle(
                      fontSize: 12,
                      color: Color(0xFFE2E8F0),
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
            const Icon(Icons.chevron_right_rounded, color: Colors.white70, size: 28),
          ],
        ),
      ),
    );
  }
}
