package quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import quiz.enums.RoleName
import quiz.model.Role

interface RoleRepository : JpaRepository<Role, Long> {

    fun findByName(name: RoleName): Role?
}