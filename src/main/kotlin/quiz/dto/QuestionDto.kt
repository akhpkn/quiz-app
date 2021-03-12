package quiz.dto

class QuestionDto(
    val questionId: Long,
    val text: String,
    val multiple: Boolean,
    val answers: List<AnswerDto>,
)