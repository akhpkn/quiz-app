package quiz.security

import io.jsonwebtoken.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider {

    private val logger: Logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)

    private val jwtSecret: String = "JWTSuperSecretKey"

    private val jwtExpirationInMs: Int = 604800000

    fun generateToken(authentication: Authentication): String {
        val customUserDetails: CustomUserDetails = authentication.principal as CustomUserDetails

        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs)

        return Jwts.builder()
            .setSubject(customUserDetails.userId.toString())
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact()
    }

    fun getUserIdFromJwt(token: String?): Long {
        val claims: Claims =Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token).body

        return claims.subject.toLong()
    }

    fun validateToken(token: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
            return true
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty")
        }
        return false
    }
}