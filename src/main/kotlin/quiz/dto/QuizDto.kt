package quiz.dto

class QuizDto(
    var quizId: Long,
    var title: String,
    var code: String,
    var author: UserDto,
    var questions: List<QuestionDto>,
)