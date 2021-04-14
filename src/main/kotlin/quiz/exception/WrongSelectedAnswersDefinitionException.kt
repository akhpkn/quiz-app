package quiz.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class WrongSelectedAnswersDefinitionException(quizId: Long) :
    RuntimeException("Ответы должны относиться к квизу с id = $quizId")