package quiz.dto

class QuizDto(
    val quizId: Long,
    val title: String,
    val author: UserDto,
    val questions: List<QuestionDto>,
)