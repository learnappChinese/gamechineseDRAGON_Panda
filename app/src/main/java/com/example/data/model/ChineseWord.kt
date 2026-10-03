package com.example.data.model

data class ChineseOption(
    val id: String,
    val hanzi: String,
    val pinyin: String,
    val meaning: String
)

data class ChineseQuestion(
    val id: String,
    val prompt: String,             // Ví dụ: "trà" hoặc "Con mèo"
    val pinyinPrompt: String? = null,
    val instruction: String = "Hãy chọn đáp án đúng",
    val options: List<ChineseOption>,
    val correctOptionId: String,
    val speechText: String,          // Chữ để TTS phát âm (ví dụ: 茶)
    val category: String = "Từ vựng cơ bản"
)
