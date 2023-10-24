package info.jotajoti.jid.dev

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "samples")
data class SampleProperties(
    val users: Int = 1,
    val locations: Int = 1,
    val participantsPerLocation: Int = 1,
    val jidCodes: Int = 500,
    val maxFoundJidCodesPerParticipant: Int = 1,
)
