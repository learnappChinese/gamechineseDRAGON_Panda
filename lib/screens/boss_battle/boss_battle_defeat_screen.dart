import 'package:flutter/material.dart';
import '../../theme/app_colors.dart';
import '../../widgets/game_asset_image.dart';
import '../../widgets/game_button.dart';

class BossBattleDefeatScreen extends StatelessWidget {
  final VoidCallback onRetry;
  final VoidCallback onBackToHub;

  const BossBattleDefeatScreen({
    Key? key,
    required this.onRetry,
    required this.onBackToHub,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF220914),
      body: Stack(
        children: [
          // 1. DEFEAT BACKGROUND
          Positioned.fill(
            child: Container(
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  colors: [Color(0xFF380816), Color(0xFF1F0611), Color(0xFF12030A)],
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                ),
              ),
              child: const GameAssetImage(
                assetPath: 'assets/images/backgrounds/defeat_temple_bg.png',
                fit: BoxFit.cover,
                fallbackEmoji: '🏯',
              ),
            ),
          ),

          // 2. CONTENT
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // Defeat Title
                  const Text(
                    'Thất bại!',
                    style: TextStyle(
                      fontSize: 36,
                      fontWeight: FontWeight.w900,
                      color: AppColors.dangerRed,
                      shadows: [
                        Shadow(color: Colors.black, blurRadius: 10, offset: Offset(0, 3)),
                      ],
                    ),
                  ),
                  const SizedBox(height: 12),

                  // Subtitle
                  const Text(
                    'Đừng bỏ cuộc!\nHãy luyện tập thêm để mạnh hơn nhé!',
                    textAlign: TextAlign.center,
                    style: TextStyle(
                      fontSize: 14,
                      color: Color(0xFFE2E8F0),
                      height: 1.5,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 28),

                  // Tired Panda
                  const SizedBox(
                    width: 160,
                    height: 160,
                    child: GameAssetImage(
                      assetPath: 'assets/images/characters/panda_dizzy.png',
                      fallbackEmoji: '🐼💫🥀',
                    ),
                  ),
                  const SizedBox(height: 38),

                  // "Thử lại" Button (Orange)
                  GameButton(
                    text: 'Thử lại',
                    gradient: AppColors.orangeGoldGradient,
                    onTap: onRetry,
                  ),
                  const SizedBox(height: 12),

                  // "Về Game Hub" Button (Dark Blue)
                  GameButton(
                    text: 'Về Game Hub',
                    gradient: AppColors.darkBlueGradient,
                    shadowColor: const Color(0xFF0F172A),
                    onTap: onBackToHub,
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
