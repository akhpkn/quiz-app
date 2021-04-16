package quiz.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ResultAlreadyExistsException(userId: Long, quizId: Long) :
    RuntimeException("Результат пользователя с id = $userId по квизу с id = $quizId уже существует")