package quiz.dto

import quiz.model.User

class QuizDto(
    val quizId: Long,
    val title: String,
    val author: User,
    val questions: List<QuestionDto>,
)