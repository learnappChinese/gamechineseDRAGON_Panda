import 'package:flutter/material.dart';
import '../../theme/app_colors.dart';
import '../../widgets/game_asset_image.dart';
import '../../widgets/game_button.dart';

class BossBattleVictoryScreen extends StatelessWidget {
  final VoidCallback onContinue;
  final VoidCallback onBackToHub;

  const BossBattleVictoryScreen({
    Key? key,
    required this.onContinue,
    required this.onBackToHub,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF1E0A24),
      body: Stack(
        children: [
          // 1. TREASURE ROOM VICTORY BACKGROUND
          Positioned.fill(
            child: Container(
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  colors: [Color(0xFF4A1024), Color(0xFF2E0815), Color(0xFF140718)],
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                ),
              ),
              child: const GameAssetImage(
                assetPath: 'assets/images/backgrounds/victory_treasure_bg.png',
                fit: BoxFit.cover,
                fallbackEmoji: '🏯',
              ),
            ),
          ),

          // 2. CELEBRATION CONTENT
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // 3 Glowing Stars
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: const [
                      Text('⭐', style: TextStyle(fontSize: 34)),
                      SizedBox(width: 8),
                      Text('⭐', style: TextStyle(fontSize: 52)),
                      SizedBox(width: 8),
                      Text('⭐', style: TextStyle(fontSize: 34)),
                    ],
                  ),
                  const SizedBox(height: 12),

                  // Red Ribbon "Chiến thắng!"
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 28, vertical: 8),
                    decoration: BoxDecoration(
                      gradient: AppColors.redGradient,
                      borderRadius: BorderRadius.circular(22),
                      boxShadow: [
                        BoxShadow(
                          color: AppColors.dangerRed.withOpacity(0.5),
                          blurRadius: 16,
                          offset: const Offset(0, 4),
                        ),
                      ],
                    ),
                    child: const Text(
                      'Chiến thắng!',
                      style: TextStyle(
                        fontSize: 26,
                        fontWeight: FontWeight.w900,
                        color: Colors.white,
                      ),
                    ),
                  ),
                  const SizedBox(height: 20),

                  // Happy Cheering Panda
                  const SizedBox(
                    width: 140,
                    height: 140,
                    child: GameAssetImage(
                      assetPath: 'assets/images/characters/panda_avatar.png',
                      fallbackEmoji: '🐼🏆',
                    ),
                  ),
                  const SizedBox(height: 20),

                  // Reward Card
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.symmetric(vertical: 18, horizontal: 16),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(24),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.25),
                          blurRadius: 14,
                          offset: const Offset(0, 6),
                        ),
                      ],
                    ),
                    child: Column(
                      children: [
                        const Text(
                          'Phần thưởng',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w900,
                            color: AppColors.textDark,
                          ),
                        ),
                        const SizedBox(height: 14),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceAround,
                          children: const [
                            _RewardBadge(icon: '🔷', text: '+100 XP', color: AppColors.primaryBlue),
                            _RewardBadge(icon: '🪙', text: '+50 coin', color: Color(0xFFD97706)),
                            _RewardBadge(icon: '💎', text: '+1 gem', color: Colors.purple),
                          ],
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 28),

                  // Primary "Tiếp tục" Button (Green)
                  GameButton(
                    text: 'Tiếp tục',
                    gradient: AppColors.greenGradient,
                    shadowColor: const Color(0xFF15803D),
                    onTap: onContinue,
                  ),
                  const SizedBox(height: 12),

                  // Secondary "Về Game Hub" Button (Dark Blue)
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

class _RewardBadge extends StatelessWidget {
  final String icon;
  final String text;
  final Color color;

  const _RewardBadge({required this.icon, required this.text, required this.color});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Text(icon, style: const TextStyle(fontSize: 18)),
        const SizedBox(width: 4),
        Text(
          text,
          style: TextStyle(
            fontSize: 14,
            fontWeight: FontWeight.w800,
            color: color,
          ),
        ),
      ],
    );
  }
}
