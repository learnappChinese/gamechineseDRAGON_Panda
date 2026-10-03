import 'package:flutter/material.dart';
import '../../theme/app_colors.dart';
import '../../widgets/game_asset_image.dart';
import '../../widgets/game_button.dart';

class BossBattleIntroScreen extends StatelessWidget {
  final VoidCallback onStartGame;
  final VoidCallback onBack;

  const BossBattleIntroScreen({
    Key? key,
    required this.onStartGame,
    required this.onBack,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF1E0A24),
      body: Stack(
        children: [
          // 1. FULL BACKGROUND FANTASY ART
          Positioned.fill(
            child: Container(
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  colors: [Color(0xFF5C0E1A), Color(0xFF2E0815), Color(0xFF14081E)],
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                ),
              ),
              child: const GameAssetImage(
                assetPath: 'assets/images/backgrounds/boss_battle_intro_bg.png',
                fit: BoxFit.cover,
                fallbackEmoji: '🏯',
              ),
            ),
          ),

          // 2. DRAGON UPPER RIGHT
          Positioned(
            top: 60,
            right: -20,
            width: 250,
            height: 250,
            child: const GameAssetImage(
              assetPath: 'assets/images/characters/dragon_fire.png',
              fit: BoxFit.contain,
              fallbackEmoji: '🐲🔥',
            ),
          ),

          // 3. PANDA ARCHER LOWER LEFT
          Positioned(
            bottom: 230,
            left: 10,
            width: 200,
            height: 200,
            child: const GameAssetImage(
              assetPath: 'assets/images/characters/panda_archer.png',
              fit: BoxFit.contain,
              fallbackEmoji: '🐼🏹',
            ),
          ),

          // 4. UI OVERLAYS & CONTENT
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: Column(
                children: [
                  // Back Button
                  Align(
                    alignment: Alignment.topLeft,
                    child: Container(
                      decoration: BoxDecoration(
                        color: Colors.black.withOpacity(0.35),
                        shape: BoxShape.circle,
                      ),
                      child: IconButton(
                        icon: const Icon(Icons.arrow_back_ios_new_rounded, color: Colors.white, size: 20),
                        onPressed: onBack,
                      ),
                    ),
                  ),
                  const SizedBox(height: 10),

                  // Epic Title & Subtitle
                  const Text(
                    'Boss Battle',
                    style: TextStyle(
                      fontSize: 36,
                      fontWeight: FontWeight.w900,
                      color: Colors.white,
                      shadows: [
                        Shadow(color: Colors.black87, blurRadius: 12, offset: Offset(0, 3)),
                      ],
                    ),
                  ),
                  const SizedBox(height: 6),
                  const Text(
                    'Đánh bại Boss bằng kiến thức tiếng Trung!',
                    style: TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.w700,
                      color: AppColors.primaryGold,
                      shadows: [
                        Shadow(color: Colors.black87, blurRadius: 8, offset: Offset(0, 2)),
                      ],
                    ),
                  ),
                  const Spacer(),

                  // Bottom Info Card (3 Bullet Features)
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
                    decoration: BoxDecoration(
                      color: const Color(0xFF1E1729).withOpacity(0.85),
                      borderRadius: BorderRadius.circular(24),
                      border: Border.all(color: Colors.white.withOpacity(0.2), width: 1.2),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.4),
                          blurRadius: 16,
                          offset: const Offset(0, 6),
                        ),
                      ],
                    ),
                    child: Column(
                      children: const [
                        _FeatureRow(icon: '📖', text: 'Học từ vựng qua trận chiến'),
                        SizedBox(height: 12),
                        _FeatureRow(icon: '⚔️', text: 'Càng đúng càng mạnh'),
                        SizedBox(height: 12),
                        _FeatureRow(icon: '⭐', text: 'Thu thập phần thưởng hấp dẫn'),
                      ],
                    ),
                  ),
                  const SizedBox(height: 20),

                  // Start Battle CTA Button
                  GameButton(
                    text: '⚔️ Bắt đầu chơi',
                    gradient: AppColors.orangeGoldGradient,
                    height: 56,
                    borderRadius: 24,
                    onTap: onStartGame,
                  ),
                  const SizedBox(height: 16),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _FeatureRow extends StatelessWidget {
  final String icon;
  final String text;

  const _FeatureRow({required this.icon, required this.text});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Container(
          width: 38,
          height: 38,
          decoration: BoxDecoration(
            color: Colors.white.withOpacity(0.12),
            shape: BoxShape.circle,
          ),
          child: Center(child: Text(icon, style: const TextStyle(fontSize: 18))),
        ),
        const SizedBox(width: 14),
        Expanded(
          child: Text(
            text,
            style: const TextStyle(
              fontSize: 15,
              fontWeight: FontWeight.w700,
              color: Colors.white,
            ),
          ),
        ),
      ],
    );
  }
}
