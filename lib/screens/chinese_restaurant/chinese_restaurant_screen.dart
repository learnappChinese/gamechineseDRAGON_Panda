import 'package:flutter/material.dart';
import '../../theme/app_colors.dart';
import '../../widgets/game_asset_image.dart';

class ChineseRestaurantScreen extends StatelessWidget {
  final VoidCallback onBack;

  const ChineseRestaurantScreen({Key? key, required this.onBack}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF3F1905),
      body: Stack(
        children: [
          // 1. TRADITIONAL RESTAURANT BACKGROUND
          Positioned.fill(
            child: Container(
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  colors: [Color(0xFF542207), Color(0xFF331404)],
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                ),
              ),
              child: const GameAssetImage(
                assetPath: 'assets/images/backgrounds/restaurant_bg.png',
                fit: BoxFit.cover,
                fallbackEmoji: '🏮🍜',
              ),
            ),
          ),

          // 2. CONTENT
          SafeArea(
            child: Column(
              children: [
                // Top App Bar
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      IconButton(
                        icon: const Icon(Icons.arrow_back_ios_new_rounded, color: Colors.white),
                        onPressed: onBack,
                      ),
                      const Text(
                        'Nhà hàng Trung Hoa',
                        style: TextStyle(fontSize: 20, fontWeight: FontWeight.w900, color: Colors.white),
                      ),
                      IconButton(
                        icon: const Icon(Icons.settings, color: Colors.white),
                        onPressed: () {},
                      ),
                    ],
                  ),
                ),

                // Customer Speech Bubble
                Container(
                  margin: const EdgeInsets.symmetric(horizontal: 24, vertical: 8),
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(22),
                    boxShadow: [
                      BoxShadow(color: Colors.black.withOpacity(0.2), blurRadius: 10, offset: const Offset(0, 4)),
                    ],
                  ),
                  child: Column(
                    children: const [
                      Text(
                        '请给我一碗面。',
                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: AppColors.textDark),
                      ),
                      SizedBox(height: 2),
                      Text(
                        '(Làm ơn cho tôi một bát mì.)',
                        style: TextStyle(fontSize: 12, color: AppColors.textMuted),
                      ),
                    ],
                  ),
                ),

                // Progress Indicator 4/10
                Container(
                  width: 140,
                  height: 10,
                  margin: const EdgeInsets.only(top: 8),
                  decoration: BoxDecoration(
                    color: Colors.black.withOpacity(0.3),
                    borderRadius: BorderRadius.circular(5),
                  ),
                  child: FractionallySizedBox(
                    alignment: Alignment.centerLeft,
                    widthFactor: 0.4,
                    child: Container(
                      decoration: BoxDecoration(
                        color: AppColors.primaryOrange,
                        borderRadius: BorderRadius.circular(5),
                      ),
                    ),
                  ),
                ),
                const Spacer(),

                // Panda Chef Behind Counter
                const SizedBox(
                  width: 180,
                  height: 180,
                  child: GameAssetImage(
                    assetPath: 'assets/images/characters/panda_chef.png',
                    fallbackEmoji: '🐼🍜👨‍🍳',
                  ),
                ),
                const Spacer(),

                // 3 Food Choice Cards Tray
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 24),
                  decoration: const BoxDecoration(
                    color: Color(0xFFFFF7E8),
                    borderRadius: BorderRadius.vertical(top: Radius.circular(32)),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      _buildFoodCard('面', 'Mì', 'assets/images/foods/noodles.png', '🍜'),
                      _buildFoodCard('米饭', 'Cơm', 'assets/images/foods/rice.png', '🍚'),
                      _buildFoodCard('饺子', 'Sủi cảo', 'assets/images/foods/dumplings.png', '🥟'),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFoodCard(String hanzi, String vietnamese, String assetPath, String fallbackEmoji) {
    return GestureDetector(
      onTap: () {},
      child: Container(
        width: 100,
        height: 115,
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(20),
          border: Border.all(color: AppColors.primaryOrange, width: 2),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.08),
              blurRadius: 8,
              offset: const Offset(0, 3),
            ),
          ],
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            SizedBox(
              width: 44,
              height: 44,
              child: GameAssetImage(
                assetPath: assetPath,
                fallbackEmoji: fallbackEmoji,
              ),
            ),
            const SizedBox(height: 6),
            Text(
              hanzi,
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: AppColors.textDark),
            ),
            Text(
              vietnamese,
              style: const TextStyle(fontSize: 11, color: AppColors.textMuted),
            ),
          ],
        ),
      ),
    );
  }
}
