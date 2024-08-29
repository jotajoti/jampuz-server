package info.jotajoti.jid.security

import org.springframework.boot.context.properties.*

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val sharedSecret: String,
    val hoursValid: Long,
)
