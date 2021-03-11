package quiz.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException: RuntimeException {
    constructor() : super("Пользователь не найден")

    constructor(id: Long) : super("Пользователь с id = $id не найден")

    constructor(username: String) : super("Пользователь с именем $username не найден")
}