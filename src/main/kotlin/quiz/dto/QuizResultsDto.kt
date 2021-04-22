package quiz.dto

class QuizResultsDto(
    var quizId: Long,
    var title: String,
    var code: String,
    var questionsNumber: Int,
    var results: List<ResultSmallDto>,
)