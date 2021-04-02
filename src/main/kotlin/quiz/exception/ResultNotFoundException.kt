package quiz.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResultNotFoundException(userId: Long, quizId: Long) :
    RuntimeException("Нет результата для пользователя с id = $userId и квиза с id = $quizId")