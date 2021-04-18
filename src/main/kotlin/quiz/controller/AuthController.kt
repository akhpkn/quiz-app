package quiz.controller

import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import quiz.dto.JwtAuthenticationResponse
import quiz.dto.SignInRequest
import quiz.dto.SignUpRequest
import quiz.dto.UserDto
import quiz.service.AuthService

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/signup")
    @ApiOperation("Метод для регистрации пользователя")
    fun signUp(@RequestBody signUpRequest: SignUpRequest): UserDto {
        return authService.signUp(signUpRequest)
    }

    @PostMapping("signin")
    @ApiOperation("Метод для авторизации пользователя")
    fun signIn(@RequestBody signInRequest: SignInRequest): JwtAuthenticationResponse {
        return authService.signIn(signInRequest)
    }
}