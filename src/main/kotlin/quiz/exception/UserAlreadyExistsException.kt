package quiz.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import kotlin.RuntimeException

@ResponseStatus(HttpStatus.BAD_REQUEST)
class UserAlreadyExistsException : RuntimeException {
    constructor() : super("Такой пользователь уже существует")

    constructor(username: String) : super("Пользователь с именем $username уже существует")
}