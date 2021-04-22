package quiz.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import quiz.repository.UserRepository

@Service
open class CustomUserDetailsService(private var userRepository: UserRepository) : UserDetailsService {

    @Transactional
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("Пользователь с юзернеймом $username не найден")
        return CustomUserDetails.create(user)
    }

    @Transactional
    open fun loadUserById(userId: Long): UserDetails {
        val user = userRepository.findUserById(userId)
            ?: throw UsernameNotFoundException("Пользователь с id = $userId не найден")
        return CustomUserDetails.create(user)
    }
}