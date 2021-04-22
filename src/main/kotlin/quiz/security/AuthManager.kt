package quiz.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import quiz.dto.JwtAuthenticationResponse
import quiz.exception.NoAuthException
import quiz.exception.UserNotFoundException
import quiz.exception.WrongUsernameOrPasswordException
import quiz.model.Role
import quiz.model.User
import quiz.repository.UserRepository

@Component
class AuthManager(
    private val authenticationManager: AuthenticationManager,
    private val tokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
) {

    fun authenticate(username: String, password: String): JwtAuthenticationResponse {
        val authentication: Authentication
        try {
            authentication = authenticationManager
                .authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (ex: BadCredentialsException) {
            throw WrongUsernameOrPasswordException()
        }

        SecurityContextHolder.getContext().authentication = authentication

        val jwtToken = tokenProvider.generateToken(authentication)
        return JwtAuthenticationResponse(jwtToken)
    }

    fun userWithEncodedPassword(
        username: String,
        password: String,
        name: String,
        userRoles: Set<Role>,
    ) = User(username, passwordEncoder.encode(password), name, userRoles)

    fun getAuthorizedUser(currentUser: CustomUserDetails?): User {
        if (currentUser == null) {
            throw NoAuthException()
        }
        val id = currentUser.userId
        return userRepository.findUserById(id) ?: throw UserNotFoundException(id)
    }
}