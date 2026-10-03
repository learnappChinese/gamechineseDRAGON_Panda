import 'dart:async';
import 'package:flutter/material.dart';
import '../../theme/app_colors.dart';
import '../../widgets/game_asset_image.dart';
import '../../widgets/hp_bar.dart';
import '../../widgets/answer_button.dart';

class BossBattleGameplayScreen extends StatefulWidget {
  final VoidCallback onVictory;
  final VoidCallback onDefeat;
  final VoidCallback onExit;

  const BossBattleGameplayScreen({
    Key? key,
    required this.onVictory,
    required this.onDefeat,
    required this.onExit,
  }) : super(key: key);

  @override
  State<BossBattleGameplayScreen> createState() => _BossBattleGameplayScreenState();
}

class _BossBattleGameplayScreenState extends State<BossBattleGameplayScreen> {
  int bossHp = 320;
  final int maxBossHp = 500;
  int playerHp = 180;
  final int maxPlayerHp = 200;
  int combo = 3;

  bool isAnimating = false;
  String? feedbackText;
  int? floatingDamage;
  Color? damageColor;
  bool isWrongAttack = false;
  int? selectedAnswerIndex;

  // Arrow animation progress (0.0 -> 1.0)
  double arrowProgress = 0.0;
  Timer? arrowTimer;

  void handleAnswer(int index) {
    if (isAnimating) return;
    setState(() {
      isAnimating = true;
      selectedAnswerIndex = index;
    });

    final bool isCorrect = (index == 0); // "uống" is correct for "喝"

    if (isCorrect) {
      // SCREEN 5: TRẢ LỜI ĐÚNG - PANDA TẤN CÔNG BOSS
      setState(() {
        combo += 1;
        feedbackText = 'Chính xác!';
        arrowProgress = 0.0;
      });

      // Animate arrow flight along Bézier arc
      const int steps = 20;
      int currentStep = 0;
      arrowTimer = Timer.periodic(const Duration(milliseconds: 20), (timer) {
        currentStep++;
        if (currentStep >= steps) {
          timer.cancel();
          setState(() {
            arrowProgress = 1.0;
            floatingDamage = -120;
            damageColor = AppColors.primaryGold;
            bossHp = (bossHp - 120).clamp(0, maxBossHp);
          });

          Timer(const Duration(milliseconds: 900), () {
            if (bossHp <= 0) {
              widget.onVictory();
            } else {
              setState(() {
                isAnimating = false;
                feedbackText = null;
                floatingDamage = null;
                arrowProgress = 0.0;
                selectedAnswerIndex = null;
              });
            }
          });
        } else {
          setState(() {
            arrowProgress = currentStep / steps;
          });
        }
      });
    } else {
      // SCREEN 6: TRẢ LỜI SAI - RỒNG PHUN LỬA TẤN CÔNG PANDA
      setState(() {
        combo = 0;
        feedbackText = 'Sai rồi!';
        isWrongAttack = true;
      });

      Timer(const Duration(milliseconds: 400), () {
        setState(() {
          floatingDamage = -30;
          damageColor = AppColors.dangerRed;
          playerHp = (playerHp - 30).clamp(0, maxPlayerHp);
        });

        Timer(const Duration(milliseconds: 900), () {
          if (playerHp <= 0) {
            widget.onDefeat();
          } else {
            setState(() {
              isAnimating = false;
              feedbackText = null;
              floatingDamage = null;
              isWrongAttack = false;
              selectedAnswerIndex = null;
            });
          }
        });
      });
    }
  }

  @override
  void dispose() {
    arrowTimer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF140B18),
      body: Stack(
        children: [
          Column(
            children: [
              // 1. TOP BAR HUD (Pause, Boss Level, Boss HP Bar, Settings)
              SafeArea(
                bottom: false,
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      // Pause Button
                      IconButton(
                        icon: const Icon(Icons.pause_circle_filled_rounded, color: Colors.white, size: 34),
                        onPressed: widget.onExit,
                      ),
                      // Boss Info & Red HP Bar
                      Column(
                        children: [
                          Row(
                            children: const [
                              Text(
                                'Lv.3 ',
                                style: TextStyle(color: AppColors.primaryGold, fontWeight: FontWeight.w900, fontSize: 13),
                              ),
                              Text(
                                'Rồng Lửa',
                                style: TextStyle(color: Colors.white, fontWeight: FontWeight.w800, fontSize: 14),
                              ),
                            ],
                          ),
                          const SizedBox(height: 4),
                          GameHpBar(
                            currentHp: bossHp,
                            maxHp: maxBossHp,
                            barColor: AppColors.dangerRed,
                            width: 180,
                            height: 15,
                            label: '$bossHp / $maxBossHp',
                          ),
                        ],
                      ),
                      // Settings Icon
                      IconButton(
                        icon: const Icon(Icons.settings, color: Colors.white),
                        onPressed: () {},
                      ),
                    ],
                  ),
                ),
              ),

              // 2. BATTLE ARENA (Landscape view inside portrait screen)
              Expanded(
                flex: 11,
                child: Stack(
                  children: [
                    // Arena background: Valley, pagoda, cherry blossoms
                    Positioned.fill(
                      child: Container(
                        decoration: const BoxDecoration(
                          gradient: LinearGradient(
                            colors: [Color(0xFF230B1C), Color(0xFF5D1728), Color(0xFF9E3A1C)],
                            begin: Alignment.topCenter,
                            end: Alignment.bottomCenter,
                          ),
                        ),
                        child: const GameAssetImage(
                          assetPath: 'assets/images/backgrounds/battle_arena_bg.png',
                          fit: BoxFit.cover,
                          fallbackEmoji: '🏯',
                        ),
                      ),
                    ),

                    // Panda Archer on Left
                    Positioned(
                      left: 12,
                      bottom: 40,
                      width: 155,
                      height: 155,
                      child: const GameAssetImage(
                        assetPath: 'assets/images/characters/panda_archer.png',
                        fallbackEmoji: '🐼🏹',
                      ),
                    ),

                    // Fire Dragon on Right
                    Positioned(
                      right: -10,
                      top: 15,
                      width: 210,
                      height: 210,
                      child: const GameAssetImage(
                        assetPath: 'assets/images/characters/dragon_fire.png',
                        fallbackEmoji: '🐲🔥',
                      ),
                    ),

                    // Animated Arrow Flight (Quadratic Bézier curve)
                    if (arrowProgress > 0 && arrowProgress < 1.0)
                      Positioned(
                        left: 110 + (210 * arrowProgress),
                        bottom: 120 + (80 * (1 - (arrowProgress - 0.5).abs() * 2)),
                        child: Transform.rotate(
                          angle: -0.3 + (arrowProgress * 0.6),
                          child: const Text('🏹⚡', style: TextStyle(fontSize: 28)),
                        ),
                      ),

                    // Dragon Continuous Fire Breath Stream (When Wrong Answer)
                    if (isWrongAttack)
                      Positioned(
                        right: 80,
                        top: 70,
                        width: 240,
                        height: 120,
                        child: const Center(
                          child: Text(
                            '🔥🔥🔥🔥🔥',
                            style: TextStyle(fontSize: 42, shadows: [Shadow(color: Colors.yellow, blurRadius: 16)]),
                          ),
                        ),
                      ),

                    // Floating Damage Number (-120 or -30)
                    if (floatingDamage != null)
                      Center(
                        child: Container(
                          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
                          decoration: BoxDecoration(
                            color: Colors.black.withOpacity(0.55),
                            borderRadius: BorderRadius.circular(16),
                            border: Border.all(color: damageColor ?? Colors.white, width: 2),
                          ),
                          child: Text(
                            '$floatingDamage',
                            style: TextStyle(
                              fontSize: 46,
                              fontWeight: FontWeight.w900,
                              color: damageColor,
                              shadows: const [
                                Shadow(color: Colors.black, blurRadius: 10),
                              ],
                            ),
                          ),
                        ),
                      ),

                    // Bottom Battle HUD: Player HP (Left) & Combo (Right)
                    Positioned(
                      bottom: 10,
                      left: 16,
                      right: 16,
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          // Player Panda HP Bar
                          Row(
                            children: [
                              Container(
                                width: 38,
                                height: 38,
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
                              const SizedBox(width: 8),
                              GameHpBar(
                                currentHp: playerHp,
                                maxHp: maxPlayerHp,
                                barColor: AppColors.successGreen,
                                width: 110,
                                height: 14,
                                label: '$playerHp / $maxPlayerHp',
                              ),
                            ],
                          ),

                          // Fiery Combo Badge
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 5),
                            decoration: BoxDecoration(
                              gradient: AppColors.comboFlameGradient,
                              borderRadius: BorderRadius.circular(16),
                              boxShadow: [
                                BoxShadow(
                                  color: const Color(0xFFFF3D00).withOpacity(0.6),
                                  blurRadius: 8,
                                ),
                              ],
                            ),
                            child: Row(
                              children: [
                                const Text('🔥', style: TextStyle(fontSize: 14)),
                                const SizedBox(width: 4),
                                Text(
                                  'Combo x$combo',
                                  style: const TextStyle(
                                    fontSize: 14,
                                    fontWeight: FontWeight.w900,
                                    color: Colors.white,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),

              // 3. QUIZ & ANSWER PANEL (BOTTOM HALF)
              Expanded(
                flex: 9,
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  decoration: const BoxDecoration(
                    color: AppColors.cardCream,
                    borderRadius: BorderRadius.vertical(top: Radius.circular(28)),
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      // Feedback Banner (Chính xác! or Sai rồi!)
                      if (feedbackText != null)
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 4),
                          decoration: BoxDecoration(
                            color: feedbackText == 'Chính xác!' ? AppColors.successGreen : AppColors.dangerRed,
                            borderRadius: BorderRadius.circular(14),
                            boxShadow: [
                              BoxShadow(
                                color: (feedbackText == 'Chính xác!' ? AppColors.successGreen : AppColors.dangerRed).withOpacity(0.4),
                                blurRadius: 8,
                              ),
                            ],
                          ),
                          child: Text(
                            feedbackText!,
                            style: const TextStyle(
                              color: Colors.white,
                              fontWeight: FontWeight.w900,
                              fontSize: 16,
                            ),
                          ),
                        ),

                      // Question Prompt
                      Column(
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: const [
                              Icon(Icons.volume_up_rounded, color: AppColors.primaryBlue, size: 30),
                              SizedBox(width: 8),
                              Text(
                                '喝',
                                style: TextStyle(
                                  fontSize: 40,
                                  fontWeight: FontWeight.bold,
                                  color: AppColors.textDark,
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 2),
                          const Text(
                            'hē\nHãy chọn đáp án đúng',
                            textAlign: TextAlign.center,
                            style: TextStyle(fontSize: 12, color: AppColors.textMuted),
                          ),
                        ],
                      ),

                      // 2x2 Answer Grid
                      Row(
                        children: [
                          AnswerButton(
                            text: 'uống',
                            state: selectedAnswerIndex == 0 ? AnswerButtonState.correct : AnswerButtonState.idle,
                            onTap: () => handleAnswer(0),
                          ),
                          const SizedBox(width: 10),
                          AnswerButton(
                            text: 'ăn',
                            state: selectedAnswerIndex == 1 ? AnswerButtonState.wrong : AnswerButtonState.idle,
                            onTap: () => handleAnswer(1),
                          ),
                        ],
                      ),
                      Row(
                        children: [
                          AnswerButton(
                            text: 'cà phê',
                            state: selectedAnswerIndex == 2 ? AnswerButtonState.wrong : AnswerButtonState.idle,
                            onTap: () => handleAnswer(2),
                          ),
                          const SizedBox(width: 10),
                          AnswerButton(
                            text: 'sữa',
                            state: selectedAnswerIndex == 3 ? AnswerButtonState.wrong : AnswerButtonState.idle,
                            onTap: () => handleAnswer(3),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),

          // Red screen overlay flash when damaged
          if (isWrongAttack)
            Positioned.fill(
              child: IgnorePointer(
                child: Container(
                  color: Colors.red.withOpacity(0.22),
                ),
              ),
            ),
        ],
      ),
    );
  }
}
