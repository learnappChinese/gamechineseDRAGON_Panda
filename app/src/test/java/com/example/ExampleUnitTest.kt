package com.example

import com.example.data.repository.ChineseQuizRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testQuizRepositoryQuestionsValid() {
        val repo = ChineseQuizRepository()
        val questions = repo.getQuestions()
        assertTrue(questions.isNotEmpty())

        val firstQ = questions.first()
        assertNotNull(firstQ.prompt)
        assertEquals(4, firstQ.options.size)
        assertTrue(firstQ.options.any { it.id == firstQ.correctOptionId })
    }
}
