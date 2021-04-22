package quiz.dto

class QuestionDto(
    var questionId: Long,
    var text: String,
    var multiple: Boolean,
    var answers: List<AnswerDto>,
)