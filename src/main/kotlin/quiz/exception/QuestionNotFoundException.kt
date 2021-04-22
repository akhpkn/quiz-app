package quiz.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class QuestionNotFoundException(questionId: Long) : RuntimeException("Вопрос с id = $questionId не найден")