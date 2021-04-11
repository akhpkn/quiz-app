package quiz.service

import org.springframework.stereotype.Service
import quiz.dto.SignInRequest
import quiz.dto.SignUpRequest
import quiz.dto.UserDto
import quiz.enums.RoleName
import quiz.exception.UserAlreadyExistsException
import quiz.mapper.DtoMapper
import quiz.model.Role
import quiz.repository.RoleRepository
import quiz.repository.UserRepository
import quiz.security.AuthenticationProvider
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val authenticationProvider: AuthenticationProvider,
    private val dtoMapper: DtoMapper,
) {

    fun signUp(signUpRequest: SignUpRequest): UserDto {
        if (userRepository.existsByUsername(signUpRequest.username)) {
            throw UserAlreadyExistsException(signUpRequest.username)
        }

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

        val user = authenticationProvider.userWithEncodedPassword(
            signUpRequest.username,
            signUpRequest.password,
            signUpRequest.name,
            Collections.singleton(userRole)
        )

        val savedUser = userRepository.save(user)
        return dtoMapper.userToDto(savedUser)
    }

    fun signIn(signInRequest: SignInRequest) =
        authenticationProvider.authenticate(signInRequest.username, signInRequest.password)
}