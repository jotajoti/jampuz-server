package info.jotajoti.jampuz.security

import org.springframework.boot.context.properties.*

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val sharedSecret: String,
    val hoursValid: Long,
)
