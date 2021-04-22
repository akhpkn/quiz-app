package quiz.dto

class JwtAuthenticationResponse(var accessToken: String) {

    var tokenType: String = "Bearer"
}