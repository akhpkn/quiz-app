package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import quiz.model.User

interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String?): User?

    fun findUserById(id: Long): User?

    fun existsByUsername(username: String): Boolean
}