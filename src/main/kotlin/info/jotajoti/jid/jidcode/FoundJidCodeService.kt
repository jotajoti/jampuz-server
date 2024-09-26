package info.jotajoti.jid.jidcode

import info.jotajoti.jid.event.*
import info.jotajoti.jid.participant.*
import info.jotajoti.jid.security.*
import info.jotajoti.jid.subscription.*
import org.springframework.data.domain.*
import org.springframework.security.core.*
import org.springframework.stereotype.*

@Service
class FoundJidCodeService(
    private val foundJidCodeRepository: FoundJidCodeRepository,
    private val participantService: ParticipantService,
    private val subscriptionService: SubscriptionService,
) {

    fun createFoundJidCode(input: RegisterFoundJidCodeInput, authentication: Authentication): FoundJidCode {
        val participant = authentication.getParticipant(input.eventId)

        val foundJidCode = FoundJidCode(
            participant = participant,
            code = JidCode(input.code),
        )

        foundJidCode.ensureNotAlreadyRegistered()

        val savedJidCode = foundJidCodeRepository.save(foundJidCode)

        subscriptionService.publishMessage(JidCodeStatsSubscription(input.eventId))
        subscriptionService.publishMessage(ParticipantsSubscription(input.eventId))

        return savedJidCode
    }

    private fun Authentication.getParticipant(eventId: EventId) = when (this) {
        is ParticipantAuthentication -> participant
        is AdminAuthentication -> participantService.getParticipantForAdmin(eventId, admin)
        else -> TODO()
    }

    private fun FoundJidCode.ensureNotAlreadyRegistered() {
        if (foundJidCodeRepository.exists(Example.of(this))) {
            throw CodeAlreadyRegisteredForParticipantException()
        }
    }
}

class CodeAlreadyRegisteredForParticipantException : Exception("Code already registered for participant")
