package info.jotajoti.jampuz.dev

import info.jotajoti.jampuz.test.*
import org.springframework.boot.context.properties.*
import kotlin.math.*
import kotlin.random.*

@ConfigurationProperties(prefix = "samples")
data class SampleProperties(
    val users: Int = 1,
    val locations: Int = 1,
    val yearsPerLocation: Int = 1,
    val minParticipantsPerLocation: Int = 1,
    val maxParticipantsPerLocation: Int = 1,
    val jidCodes: Int = 500,
    val maxFoundJidCodesPerParticipant: Int = 1,
) {

    fun randomNumberOfParticipants() = Random.nextInt(minParticipantsPerLocation..maxParticipantsPerLocation)

    fun randomNumberOfFoundJidCodes(): Int {
        val randomValue = Random.nextGaussian(18.0, 6.0).roundToInt()
        return minOf(maxOf(1, randomValue), maxFoundJidCodesPerParticipant)
    }
}
