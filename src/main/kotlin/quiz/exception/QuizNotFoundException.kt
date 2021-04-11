package quiz.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class QuizNotFoundException : RuntimeException {

    constructor(quizId: Long) : super("Квиз с id = $quizId не найден")

    constructor(code: String) : super("Квиз с кодом $code не найден")
}