package quiz.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import quiz.model.User
import java.util.*

class CustomUserDetails : UserDetails {

    var userId: Long private set

    private var username: String

    private var password: String

    private var authorities: Collection<GrantedAuthority>

    constructor(userId: Long, username: String, password: String, authorities: Collection<GrantedAuthority>) {
        this.userId = userId
        this.username = username
        this.password = password
        this.authorities = authorities
    }

    companion object Factory {
        fun create(user: User): CustomUserDetails {
            val authorities: List<GrantedAuthority> = user.roles
                .map { SimpleGrantedAuthority(it.name?.name) }
                .toList()

            return CustomUserDetails(user.id, user.username, user.password, authorities)
        }
    }

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }

        other as CustomUserDetails

        if (userId != other.userId) {
            return false
        }
        if (username != other.username) {
            return false
        }
        if (password != other.password) {
            return false
        }

        return true
    }

    override fun hashCode(): Int = Objects.hash(userId)
}