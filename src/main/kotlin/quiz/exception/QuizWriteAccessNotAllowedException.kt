package quiz.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
class QuizWriteAccessNotAllowedException(username: String, quizId: Long) :
    RuntimeException("У пользователя $username нет прав на рекдактирование квиза с id = $quizId")