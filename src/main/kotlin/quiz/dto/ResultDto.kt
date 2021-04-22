package quiz.dto

class ResultDto(
    var resultId: Long,
    var user: UserDto,
    var quiz: BlankQuizDto,
    var score: Double,
    var correctAnswers: Int,
)