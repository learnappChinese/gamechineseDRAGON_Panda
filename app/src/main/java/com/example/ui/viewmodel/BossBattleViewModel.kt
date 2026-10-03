package com.example.ui.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BossStats
import com.example.data.model.ChineseQuestion
import com.example.data.model.DragonState
import com.example.data.model.FloatingDamage
import com.example.data.model.PandaState
import com.example.data.model.PlayerStats
import com.example.data.repository.ChineseQuizRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class BossBattleUiState(
    val bossStats: BossStats = BossStats(currentHp = 320, maxHp = 500),
    val playerStats: PlayerStats = PlayerStats(currentHp = 180, maxHp = 200, combo = 3),
    val questions: List<ChineseQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionId: String? = null,
    val isCorrectSelection: Boolean? = null,
    val isProcessingAnswer: Boolean = false,
    val pandaState: PandaState = PandaState.IDLE,
    val dragonState: DragonState = DragonState.IDLE,
    val isArrowActive: Boolean = false,
    val arrowProgress: Float = 0f,
    val isFireActive: Boolean = false,
    val fireProgress: Float = 0f,
    val dragonMouthGlow: Float = 0f,
    val cameraShakeOffset: Offset = Offset.Zero,
    val screenFlashAlpha: Float = 0f,
    val floatingDamages: List<FloatingDamage> = emptyList(),
    val isVictory: Boolean = false,
    val isDefeat: Boolean = false,
    val isPaused: Boolean = false,
    val feedbackMessage: String? = null
) {
    val currentQuestion: ChineseQuestion?
        get() = questions.getOrNull(currentQuestionIndex)
}

class BossBattleViewModel(
    private val repository: ChineseQuizRepository = ChineseQuizRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BossBattleUiState())
    val uiState: StateFlow<BossBattleUiState> = _uiState.asStateFlow()

    init {
        startNewBattle()
    }

    fun startNewBattle() {
        val qList = repository.getQuestions()
        _uiState.value = BossBattleUiState(
            bossStats = BossStats(name = "Rồng Lửa", level = 3, maxHp = 500, currentHp = 320),
            playerStats = PlayerStats(name = "Đại Hiệp Panda", maxHp = 200, currentHp = 180, combo = 3),
            questions = qList,
            currentQuestionIndex = 0
        )
    }

    fun selectOption(optionId: String) {
        val currentState = _uiState.value
        if (currentState.isProcessingAnswer || currentState.isVictory || currentState.isDefeat) {
            return
        }

        val question = currentState.currentQuestion ?: return
        val isCorrect = (optionId == question.correctOptionId)

        _uiState.value = currentState.copy(
            selectedOptionId = optionId,
            isCorrectSelection = isCorrect,
            isProcessingAnswer = true
        )

        if (isCorrect) {
            handleCorrectAnswer()
        } else {
            handleWrongAnswer()
        }
    }

    private fun handleCorrectAnswer() {
        viewModelScope.launch {
            val state = _uiState.value
            val newCombo = state.playerStats.combo + 1
            val damage = 100 + (newCombo * 10)

            _uiState.value = _uiState.value.copy(
                playerStats = state.playerStats.copy(combo = newCombo),
                feedbackMessage = "Chính xác! Combo x$newCombo",
                pandaState = PandaState.ATTACKING
            )

            // Step 1: Panda draws bow (200ms)
            delay(220)

            // Step 2: Release arrow, fly toward Dragon along Bézier curve (450ms)
            _uiState.value = _uiState.value.copy(
                isArrowActive = true,
                arrowProgress = 0f
            )

            val arrowSteps = 25
            val stepDuration = 450L / arrowSteps
            for (i in 1..arrowSteps) {
                val progress = i / arrowSteps.toFloat()
                _uiState.value = _uiState.value.copy(arrowProgress = progress)
                delay(stepDuration)
            }

            // Step 3: Arrow Hits Dragon!
            val newBossHp = (_uiState.value.bossStats.currentHp - damage).coerceAtLeast(0)
            val isDragonDead = (newBossHp == 0)

            // Trigger Camera Shake & Hurt & Damage Float
            val damageItem = FloatingDamage(
                id = System.currentTimeMillis(),
                text = "-$damage",
                color = Color(0xFFFFD54F),
                x = 750f, // around dragon hit position
                y = 480f,
                scale = 1.2f,
                alpha = 1f
            )

            _uiState.value = _uiState.value.copy(
                isArrowActive = false,
                arrowProgress = 0f,
                dragonState = DragonState.HURT,
                bossStats = _uiState.value.bossStats.copy(currentHp = newBossHp),
                floatingDamages = _uiState.value.floatingDamages + damageItem
            )

            // Animate floating damage & camera shake
            launch { runCameraShake(amplitude = 9f, durationMs = 200) }
            launch { animateFloatingDamage(damageItem.id) }

            delay(350)

            if (isDragonDead) {
                _uiState.value = _uiState.value.copy(
                    dragonState = DragonState.DEFEATED,
                    pandaState = PandaState.VICTORY
                )
                delay(700)
                _uiState.value = _uiState.value.copy(isVictory = true)
            } else {
                _uiState.value = _uiState.value.copy(
                    dragonState = DragonState.IDLE,
                    pandaState = PandaState.IDLE
                )
                delay(200)
                nextQuestion()
            }
        }
    }

    private fun handleWrongAnswer() {
        viewModelScope.launch {
            val state = _uiState.value
            val damage = 30

            _uiState.value = _uiState.value.copy(
                playerStats = state.playerStats.copy(combo = 0),
                feedbackMessage = "Sai rồi!",
                dragonState = DragonState.ATTACKING,
                dragonMouthGlow = 0.2f
            )

            // Step 1: Dragon mouth glow & charging (200ms)
            for (i in 1..10) {
                _uiState.value = _uiState.value.copy(dragonMouthGlow = 0.2f + (i * 0.08f))
                delay(20)
            }

            // Step 2: Dragon breathes fire, travels to Panda (450ms)
            _uiState.value = _uiState.value.copy(
                isFireActive = true,
                fireProgress = 0f
            )

            val fireSteps = 25
            val stepDuration = 450L / fireSteps
            for (i in 1..fireSteps) {
                val progress = i / fireSteps.toFloat()
                _uiState.value = _uiState.value.copy(fireProgress = progress)
                delay(stepDuration)
            }

            // Step 3: Fire hits Panda!
            val newPlayerHp = (_uiState.value.playerStats.currentHp - damage).coerceAtLeast(0)
            val isPlayerDead = (newPlayerHp == 0)

            val damageItem = FloatingDamage(
                id = System.currentTimeMillis(),
                text = "-$damage",
                color = Color(0xFFFF5252),
                x = 240f, // near panda
                y = 520f,
                scale = 1.1f,
                alpha = 1f
            )

            _uiState.value = _uiState.value.copy(
                isFireActive = false,
                fireProgress = 0f,
                dragonMouthGlow = 0f,
                dragonState = DragonState.IDLE,
                pandaState = PandaState.HURT,
                playerStats = _uiState.value.playerStats.copy(currentHp = newPlayerHp),
                screenFlashAlpha = 0.25f,
                floatingDamages = _uiState.value.floatingDamages + damageItem
            )

            // Red screen flash decay & camera shake
            launch {
                for (step in 1..10) {
                    delay(20)
                    val alpha = 0.25f * (1f - (step / 10f))
                    _uiState.value = _uiState.value.copy(screenFlashAlpha = alpha)
                }
            }
            launch { runCameraShake(amplitude = 12f, durationMs = 220) }
            launch { animateFloatingDamage(damageItem.id) }

            delay(350)

            if (isPlayerDead) {
                _uiState.value = _uiState.value.copy(
                    pandaState = PandaState.DEFEATED
                )
                delay(600)
                _uiState.value = _uiState.value.copy(isDefeat = true)
            } else {
                _uiState.value = _uiState.value.copy(
                    pandaState = PandaState.IDLE
                )
                delay(200)
                nextQuestion()
            }
        }
    }

    private suspend fun runCameraShake(amplitude: Float, durationMs: Long) {
        val steps = (durationMs / 25).toInt()
        for (i in 0 until steps) {
            val decay = 1f - (i.toFloat() / steps)
            val rx = (Random.nextFloat() * 2f - 1f) * amplitude * decay
            val ry = (Random.nextFloat() * 2f - 1f) * amplitude * decay
            _uiState.value = _uiState.value.copy(cameraShakeOffset = Offset(rx, ry))
            delay(25)
        }
        _uiState.value = _uiState.value.copy(cameraShakeOffset = Offset.Zero)
    }

    private suspend fun animateFloatingDamage(damageId: Long) {
        val steps = 20
        for (i in 1..steps) {
            delay(30)
            val list = _uiState.value.floatingDamages.map { dmg ->
                if (dmg.id == damageId) {
                    val progress = i / steps.toFloat()
                    dmg.copy(
                        y = dmg.y - 3.5f,
                        alpha = (1f - progress).coerceIn(0f, 1f),
                        scale = 1.2f - (progress * 0.2f)
                    )
                } else dmg
            }
            _uiState.value = _uiState.value.copy(floatingDamages = list)
        }
        // Remove item after animation
        _uiState.value = _uiState.value.copy(
            floatingDamages = _uiState.value.floatingDamages.filterNot { it.id == damageId }
        )
    }

    private fun nextQuestion() {
        val state = _uiState.value
        val nextIdx = (state.currentQuestionIndex + 1) % state.questions.size
        _uiState.value = state.copy(
            currentQuestionIndex = nextIdx,
            selectedOptionId = null,
            isCorrectSelection = null,
            isProcessingAnswer = false,
            feedbackMessage = null
        )
    }

    fun togglePause() {
        _uiState.value = _uiState.value.copy(isPaused = !_uiState.value.isPaused)
    }
}
