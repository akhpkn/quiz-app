package quiz.dto

class QuizCreationRequest {
    var title: String = ""

    var list: List<QuestionCreationRequest> = ArrayList()
}