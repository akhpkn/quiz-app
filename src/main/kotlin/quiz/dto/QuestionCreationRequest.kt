package quiz.dto

class QuestionCreationRequest {
    var text: String = ""

    var multiple: Boolean = false

    var answers: List<AnswerCreationRequest> = ArrayList()
}