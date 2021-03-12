package quiz.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import quiz.dto.JwtAuthenticationResponse
import quiz.dto.SignInRequest
import quiz.dto.SignUpRequest
import quiz.model.User
import quiz.service.AuthService

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/signup")
    fun signUp(@RequestBody signUpRequest: SignUpRequest): User {
        return authService.signUp(signUpRequest)
    }

    @PostMapping("signin")
    fun signIn(@RequestBody signInRequest: SignInRequest): JwtAuthenticationResponse {
        return authService.signIn(signInRequest)
    }
}