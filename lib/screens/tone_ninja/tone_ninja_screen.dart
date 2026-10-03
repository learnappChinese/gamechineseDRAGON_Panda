import 'package:flutter/material.dart';
import '../../theme/app_colors.dart';
import '../../widgets/game_asset_image.dart';

class ToneNinjaScreen extends StatelessWidget {
  final VoidCallback onBack;

  const ToneNinjaScreen({Key? key, required this.onBack}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF131034),
      body: Stack(
        children: [
          // 1. NIGHT NINJA VILLAGE BACKGROUND
          Positioned.fill(
            child: Container(
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  colors: [Color(0xFF0F0B26), Color(0xFF1B144A), Color(0xFF281D66)],
                  begin: Alignment.topCenter,
                  end: Alignment.bottomCenter,
                ),
              ),
              child: const GameAssetImage(
                assetPath: 'assets/images/backgrounds/ninja_night_bg.png',
                fit: BoxFit.cover,
                fallbackEmoji: '🏯🌙',
              ),
            ),
          ),

          // 2. CONTENT
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
              child: Column(
                children: [
                  // App Bar
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      IconButton(
                        icon: const Icon(Icons.arrow_back_ios_new_rounded, color: Colors.white),
                        onPressed: onBack,
                      ),
                      const Text(
                        'Tone Ninja',
                        style: TextStyle(fontSize: 20, fontWeight: FontWeight.w900, color: Colors.white),
                      ),
                      IconButton(
                        icon: const Icon(Icons.settings, color: Colors.white),
                        onPressed: () {},
                      ),
                    ],
                  ),

                  // Score & 3 Hearts
                  Padding(
                    padding: const EdgeInsets.symmetric(vertical: 6),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: const [
                        Text(
                          '⭐ 3 / 10',
                          style: TextStyle(color: AppColors.primaryGold, fontWeight: FontWeight.w900, fontSize: 16),
                        ),
                        Row(
                          children: [
                            Text('❤️', style: TextStyle(fontSize: 20)),
                            SizedBox(width: 4),
                            Text('❤️', style: TextStyle(fontSize: 20)),
                            SizedBox(width: 4),
                            Text('❤️', style: TextStyle(fontSize: 20)),
                          ],
                        ),
                      ],
                    ),
                  ),
                  const Spacer(),

                  // Ninja Panda Character & Audio Box
                  Container(
                    width: 140,
                    height: 140,
                    decoration: BoxDecoration(
                      color: Colors.white.withOpacity(0.08),
                      shape: BoxShape.circle,
                      border: Border.all(color: Colors.white24, width: 2),
                    ),
                    clipBehavior: Clip.antiAlias,
                    child: const GameAssetImage(
                      assetPath: 'assets/images/characters/panda_ninja.png',
                      fallbackEmoji: '🥷🐼',
                    ),
                  ),
                  const SizedBox(height: 14),

                  // Speaker Button & Target Pinyin
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                    decoration: BoxDecoration(
                      color: const Color(0xFFFFF7E8),
                      borderRadius: BorderRadius.circular(20),
                      boxShadow: [
                        BoxShadow(color: Colors.black.withOpacity(0.3), blurRadius: 10, offset: const Offset(0, 4)),
                      ],
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: const [
                        Icon(Icons.volume_up_rounded, color: AppColors.primaryBlue, size: 30),
                        SizedBox(width: 12),
                        Text(
                          'mǎ',
                          style: TextStyle(
                            fontSize: 38,
                            fontWeight: FontWeight.w900,
                            color: AppColors.textDark,
                          ),
                        ),
                      ],
                    ),
                  ),
                  const Spacer(),

                  // 4 Tone Options in 2x2 Grid
                  GridView.count(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    crossAxisCount: 2,
                    crossAxisSpacing: 12,
                    mainAxisSpacing: 12,
                    childAspectRatio: 2.1,
                    children: [
                      _buildToneCard('mǎ (3)', isCorrect: true),
                      _buildToneCard('mā (1)'),
                      _buildToneCard('má (2)'),
                      _buildToneCard('mà (4)'),
                    ],
                  ),
                  const SizedBox(height: 20),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildToneCard(String text, {bool isCorrect = false}) {
    return Container(
      decoration: BoxDecoration(
        color: isCorrect ? const Color(0xFF20D66B).withOpacity(0.25) : Colors.white.withOpacity(0.12),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(
          color: isCorrect ? AppColors.successGreen : Colors.white30,
          width: isCorrect ? 2.5 : 1.2,
        ),
        boxShadow: [
          if (isCorrect)
            BoxShadow(
              color: AppColors.successGreen.withOpacity(0.4),
              blurRadius: 12,
              offset: const Offset(0, 2),
            ),
        ],
      ),
      child: Center(
        child: Text(
          text,
          style: TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.w900,
            color: isCorrect ? AppColors.successGreen : Colors.white,
          ),
        ),
      ),
    );
  }
}
