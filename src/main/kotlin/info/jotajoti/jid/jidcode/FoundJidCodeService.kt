package info.jotajoti.jid.jidcode

import info.jotajoti.jid.location.LocationId
import info.jotajoti.jid.participant.ParticipantService
import info.jotajoti.jid.security.AdminAuthentication
import info.jotajoti.jid.security.ParticipantAuthentication
import info.jotajoti.jid.subscription.SubscriptionService
import org.springframework.data.domain.Example
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class FoundJidCodeService(
    private val foundJidCodeRepository: FoundJidCodeRepository,
    private val participantService: ParticipantService,
    private val subscriptionService: SubscriptionService,
) {

    fun createFoundJidCode(input: RegisterFoundJidCodeInput, authentication: Authentication): FoundJidCode {
        val participant = authentication.getParticipant(input.locationId)

        val foundJidCode = FoundJidCode(
            participant = participant,
            code = JidCode(input.code),
        )

        foundJidCode.ensureNotAlreadyRegistered()

        val savedJidCode = foundJidCodeRepository.save(foundJidCode)

        subscriptionService.publishMessage(JidCodeStatsSubscription(input.locationId))

        return savedJidCode
    }

    private fun Authentication.getParticipant(locationId: LocationId) = when (this) {
        is ParticipantAuthentication -> participant
        is AdminAuthentication -> participantService.getParticipantForAdmin(locationId, admin)
        else -> TODO()
    }

    private fun FoundJidCode.ensureNotAlreadyRegistered() {
        if (foundJidCodeRepository.exists(Example.of(this))) {
            throw CodeAlreadyRegisteredForParticipantException()
        }
    }
}

class CodeAlreadyRegisteredForParticipantException : Exception("Code already registered for participant")
