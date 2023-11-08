package info.jotajoti.jid.security

import com.auth0.jwt.*
import com.auth0.jwt.algorithms.*
import com.auth0.jwt.exceptions.*
import com.auth0.jwt.interfaces.*
import org.springframework.beans.factory.annotation.*
import org.springframework.stereotype.*
import java.time.*
import java.time.ZoneOffset.*
import java.util.*

@Service
class JwtService(@Value("\${jwt.sharedSecret}") private val sharedSecret: String) {

    private val algorithm = Algorithm.HMAC512(sharedSecret)
    private val verifier = JWT
        .require(algorithm)
        .withIssuer("jid")
        .build()

    fun validateToken(token: String?) = try {
        verifier
            .verify(token)
            .toSubject()
    } catch (jwtVerificationException: JWTVerificationException) {
        null
    }

    private fun DecodedJWT.toSubject() =
        subject
            // admin/dcaa8919-528d-4534-9bec-889faae38386
            ?.takeIf { it.matches("(participant|admin)/[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}".toRegex()) }
            ?.let {
                val subjectType = SubjectType.valueOf(it.substringBefore("/").uppercase())
                val id = UUID.fromString(it.substringAfter("/"))

                Subject(subjectType, id)
            }

    fun createToken(subject: Subject): String =
        JWT
            .create()
            .withIssuer("jid")
            .withSubject(subject.toString())
            .withExpiresAt(LocalDateTime.now().plusMonths(1).toInstant(UTC))
            .sign(algorithm)
}

data class Subject(
    val type: SubjectType,
    val id: UUID
) {

    override fun toString() = "${type.name.lowercase()}/$id"
}

enum class SubjectType {
    ADMIN,
    PARTICIPANT,
}
