package info.jotajoti.jampuz.jidcode

import info.jotajoti.jampuz.event.*
import info.jotajoti.jampuz.exceptions.*
import info.jotajoti.jampuz.exceptions.ErrorCode.*
import info.jotajoti.jampuz.participant.*
import info.jotajoti.jampuz.security.*
import info.jotajoti.jampuz.subscription.*
import org.springframework.data.domain.*
import org.springframework.graphql.execution.ErrorType.*
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

        subscriptionService.publishMessage(EventSubscription(input.eventId))

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

class CodeAlreadyRegisteredForParticipantException : ErrorCodeException(
    "Code already registered for participant", CODE_ALREADY_REGISTERED_FOR_PARTICIPANT, BAD_REQUEST
)
