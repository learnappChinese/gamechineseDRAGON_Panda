# Fantasy game artwork

The repository contains two clients: Android Kotlin/Compose (`app/`, launched by `MainActivity`) and Flutter UI sources (`lib/`). They share `assets/images/`. Android packages this directory through `sourceSets.main.assets`; Flutter includes it through the existing `pubspec.yaml` asset directories. No new dependency is needed by either client.

## Current battle architecture

Android's `BossBattleScreen` consumes `BossBattleViewModel`. The view model validates the selected answer, updates HP and combo, disables answers during an attack, and delays the next question or result until the attack completes. `BattleArenaCanvas` renders actors, arrows, fire, damage and flashes. These rules and timings are unchanged.

The new renderer loads PNGs once with `rememberGameBitmap`, contains each actor within a proportional box, preserves alpha, and keeps the existing arrow trajectory, layered fire, knockback, flash and damage feedback. The native scene uses a landscape arena. Flutter's fullscreen scene uses a portrait alias of the intro arena so a landscape image does not crop away the side temples on a narrow phone.

Home, Game Hub, Boss Intro, result dialogs and the HP avatar now use image artwork in Android. Existing Flutter asset references pick up the replacement artwork; Victory uses the new full-body victory panda. Other games already reference their dedicated backgrounds and characters in Flutter. Native navigation still exposes the original Home, Hub and Boss Battle routes.

## Asset map

| Screen | Background | Foreground |
|---|---|---|
| Home | `home_bg.png` | `panda_avatar.png`, `panda_archer.png` |
| Game Hub | `game_hub_bg.png` | Reused art thumbnails |
| Boss Intro | `boss_intro_bg.png` | `panda_archer.png`, `dragon_fire.png` |
| Native battle | `boss_battle_bg.png` (landscape) | Archer and dragon |
| Flutter battle | `boss_battle_portrait_bg.png` (portrait intro alias) | Archer and dragon |
| Victory | `victory_bg.png` | `panda_victory.png` |
| Defeat | `defeat_bg.png` | `panda_dizzy.png` |
| Radical Builder | `radical_builder_bg.png` | Puzzle widgets |
| Tone Ninja | `tone_ninja_bg.png` | `panda_ninja.png` |
| Restaurant | `restaurant_bg.png` | `panda_chef.png`, distinct noodle/rice/dumpling art |
| Quick Answer | `quick_answer_bg.png` | Question widgets |

All backgrounds are clean scenery without baked-in UI or characters. Characters and foods are separate alpha PNGs. Thumbnail files reuse the corresponding generated character/background without modifying image pixels. Existing effect PNGs are untouched; native arrows/fire continue to be animated procedurally rather than moving a single static flame.

These character assets are individual poses, not sprite animation sheets. Attack preparation, idle movement, hurt feedback and defeat fade are Canvas transforms. Frame-by-frame character animation would require separate sprite sheets.

## Tuning and verification

In `BattleArenaCanvas.kt`, adjust the proportional actor bounds and `bowWorldPos` / `dragonMouthPos` offsets to fine-tune placement. Bow and mouth offsets are measured within the fitted image box. Projectile travel timing remains in `BossBattleViewModel`; default travel is 450 ms. Keep HP/combo changes in the view model.

Run `python scripts/validate_game_assets.py` (Pillow required) to verify both clients' asset references, PNG decoding, portrait/landscape orientation, transparency and distinct food images. Test Home → Hub → Intro → Battle on Android, selecting correct and wrong answers, then test victory, defeat and retry. Inspect small and large screens for clipped hats/tails, arrow release, mouth glow, HP text and repeated taps during an attack.

The legacy placeholder generator now requires `--output-dir` outside the production assets directory. It cannot overwrite the new artwork.

Artwork was generated with the built-in image generation tool. `game_art_prompts.json` records the prompt set. `game_art_manifest.json` records dimensions and SHA-256 hashes. Full Android/Flutter compilation and device FPS testing were unavailable in the editing environment (no Android SDK, Gradle executable/wrapper jar, or Flutter SDK).
