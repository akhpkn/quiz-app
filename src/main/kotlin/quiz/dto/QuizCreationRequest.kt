package quiz.dto

class QuizCreationRequest {
    var title: String = ""

    var questions: List<QuestionCreationRequest> = ArrayList()
}