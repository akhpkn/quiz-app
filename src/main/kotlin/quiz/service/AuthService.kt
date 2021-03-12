package quiz.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import quiz.dto.JwtAuthenticationResponse
import quiz.dto.SignInRequest
import quiz.dto.SignUpRequest
import quiz.exception.UserAlreadyExistsException
import quiz.exception.WrongUsernameOrPasswordException
import quiz.model.Role
import quiz.model.RoleName
import quiz.model.User
import quiz.repository.RoleRepository
import quiz.repository.UserRepository
import quiz.security.JwtTokenProvider
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val tokenProvider: JwtTokenProvider,
) {

    fun signUp(signUpRequest: SignUpRequest): User {
        if (userRepository.existsByUsername(signUpRequest.username)) {
            throw UserAlreadyExistsException(signUpRequest.username)
        }
        val user = User(signUpRequest.username, signUpRequest.password)

        var roleUser = roleRepository.findByName(RoleName.ROLE_USER)
        val userRole: Role

        if (roleUser != null) {
            userRole = roleUser
        } else {
            roleUser = Role(RoleName.ROLE_USER)
            val roleAdmin = Role(RoleName.ROLE_ADMIN)

            roleRepository.save(roleAdmin)
            userRole = roleRepository.save(roleUser)
        }
        user.roles = Collections.singleton(userRole)

        user.password = passwordEncoder.encode(user.password)

        return userRepository.save(user)
    }

    fun signIn(signInRequest: SignInRequest): JwtAuthenticationResponse {
        val authentication: Authentication
        try {
            authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    signInRequest.username,
                    signInRequest.password)
            )
        } catch (ex: BadCredentialsException) {
            throw WrongUsernameOrPasswordException()
        }

        SecurityContextHolder.getContext().authentication = authentication

        val jwtToken = tokenProvider.generateToken(authentication)
        return JwtAuthenticationResponse(jwtToken)
    }
}