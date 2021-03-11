package quiz.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import quiz.repository.UserRepository

@Service
class CustomUserDetailsService(private var userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("Пользователь с юзернеймом $username не найден")
        return CustomUserDetails.create(user)
    }

    fun loadUserById(userId: Long): UserDetails {
        val user = userRepository.findById(userId)
        if (user.isEmpty) {
            throw UsernameNotFoundException("Пользователь с id = $userId не найден")
        }
        return CustomUserDetails.create(user.get())
    }
}