package info.jotajoti.jampuz.participant

import info.jotajoti.jampuz.exceptions.*
import info.jotajoti.jampuz.exceptions.ErrorCode.*
import jakarta.validation.*
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.*

@Target(CLASS)
@Retention(RUNTIME)
@Constraint(validatedBy = [UniqueNameAndEventValidator::class])
@ExposeErrorCode(PARTICIPANT_NAME_NOT_AVAILABLE)
annotation class UniqueNameAndEvent(
    val message: String = "A participant with the same name already exists in the event",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class UniqueNameAndEventValidator(
    private val participantRepository: ParticipantRepository,
) : ConstraintValidator<UniqueNameAndEvent, CreateParticipantInput> {

    override fun isValid(createParticipantInput: CreateParticipantInput, context: ConstraintValidatorContext?) =
        participantRepository.findFirstByNameAndEventId(
            createParticipantInput.name,
            createParticipantInput.eventId
        ) == null
}
