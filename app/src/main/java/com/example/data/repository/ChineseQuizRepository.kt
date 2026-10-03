package com.example.data.repository

import com.example.data.model.ChineseOption
import com.example.data.model.ChineseQuestion

class ChineseQuizRepository {

    private val questions = listOf(
        ChineseQuestion(
            id = "q1",
            prompt = "trà",
            pinyinPrompt = "chá",
            instruction = "Hãy chọn đáp án đúng",
            options = listOf(
                ChineseOption("opt_1", "茶", "chá", "Trà"),
                ChineseOption("opt_2", "水", "shuǐ", "Nước"),
                ChineseOption("opt_3", "咖啡", "kāfēi", "Cà phê"),
                ChineseOption("opt_4", "牛奶", "niúnǎi", "Sữa")
            ),
            correctOptionId = "opt_1",
            speechText = "茶",
            category = "Đồ uống"
        ),
        ChineseQuestion(
            id = "q2",
            prompt = "nước lọc",
            pinyinPrompt = "shuǐ",
            instruction = "Hãy chọn đáp án đúng",
            options = listOf(
                ChineseOption("opt_1", "果汁", "guǒzhī", "Nước ép"),
                ChineseOption("opt_2", "水", "shuǐ", "Nước lọc"),
                ChineseOption("opt_3", "茶", "chá", "Trà"),
                ChineseOption("opt_4", "可乐", "kělè", "Nước ngọt")
            ),
            correctOptionId = "opt_2",
            speechText = "水",
            category = "Đồ uống"
        ),
        ChineseQuestion(
            id = "q3",
            prompt = "gấu trúc",
            pinyinPrompt = "xióngmāo",
            instruction = "Hãy chọn đáp án đúng",
            options = listOf(
                ChineseOption("opt_1", "老虎", "lǎohǔ", "Hổ"),
                ChineseOption("opt_2", "龙", "lóng", "Rồng"),
                ChineseOption("opt_3", "熊猫", "xióngmāo", "Gấu trúc"),
                ChineseOption("opt_4", "兔子", "tùzi", "Thỏ")
            ),
            correctOptionId = "opt_3",
            speechText = "熊猫",
            category = "Động vật"
        ),
        ChineseQuestion(
            id = "q4",
            prompt = "rồng lửa",
            pinyinPrompt = "huǒ lóng",
            instruction = "Hãy chọn đáp án đúng",
            options = listOf(
                ChineseOption("opt_1", "火龙", "huǒ lóng", "Rồng lửa"),
                ChineseOption("opt_2", "凤凰", "fènghuáng", "Phượng hoàng"),
                ChineseOption("opt_3", "狮子", "shīzi", "Sư tử"),
                ChineseOption("opt_4", "大象", "dàxiàng", "Voi")
            ),
            correctOptionId = "opt_1",
            speechText = "火龙",
            category = "Thần thoại"
        ),
        ChineseQuestion(
            id = "q5",
            prompt = "cảm ơn",
            pinyinPrompt = "xièxie",
            instruction = "Hãy chọn đáp án đúng",
            options = listOf(
                ChineseOption("opt_1", "再见", "zàijiàn", "Tạm biệt"),
                ChineseOption("opt_2", "你好", "nǐ hǎo", "Xin chào"),
                ChineseOption("opt_3", "对不起", "duìbuqǐ", "Xin lỗi"),
                ChineseOption("opt_4", "谢谢", "xièxie", "Cảm ơn")
            ),
            correctOptionId = "opt_4",
            speechText = "谢谢",
            category = "Giao tiếp"
        ),
        ChineseQuestion(
            id = "q6",
            prompt = "cơm trắng",
            pinyinPrompt = "mǐfàn",
            instruction = "Hãy chọn đáp án đúng",
            options = listOf(
                ChineseOption("opt_1", "面条", "miàntiáo", "Mì sợi"),
                ChineseOption("opt_2", "米饭", "mǐfàn", "Cơm"),
                ChineseOption("opt_3", "包子", "bāozi", "Bánh bao"),
                ChineseOption("opt_4", "饺子", "jiǎozi", "Sủi cảo")
            ),
            correctOptionId = "opt_2",
            speechText = "米饭",
            category = "Ẩm thực"
        ),
        ChineseQuestion(
            id = "q7",
            prompt = "gia đình",
            pinyinPrompt = "jiātíng",
            instruction = "Hãy chọn đáp án đúng",
            options = listOf(
                ChineseOption("opt_1", "家庭", "jiātíng", "Gia đình"),
                ChineseOption("opt_2", "朋友", "péngyou", "Bạn bè"),
                ChineseOption("opt_3", "老师", "lǎoshī", "Thầy cô"),
                ChineseOption("opt_4", "同学", "tóngxué", "Bạn học")
            ),
            correctOptionId = "opt_1",
            speechText = "家庭",
            category = "Gia đình"
        )
    )

    fun getQuestions(): List<ChineseQuestion> = questions.shuffled()
}
