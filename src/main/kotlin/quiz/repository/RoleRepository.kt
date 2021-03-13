package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import quiz.model.Role
import quiz.model.RoleName

interface RoleRepository : JpaRepository<Role, Long> {

    fun findByName(name: RoleName): Role?
}