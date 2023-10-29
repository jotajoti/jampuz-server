package info.jotajoti.jid.participant

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.reflect.KClass

@Target(CLASS)
@Retention(RUNTIME)
@Constraint(validatedBy = [UniqueNameAndLocationValidator::class])
annotation class UniqueNameAndLocation(
    val message: String = "A participant with the same name already exists in the location",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class UniqueNameAndLocationValidator(
    private val participantRepository: ParticipantRepository,
) : ConstraintValidator<UniqueNameAndLocation, CreateParticipantInput> {

    override fun isValid(createParticipantInput: CreateParticipantInput, context: ConstraintValidatorContext?) =
        participantRepository.findFirstByNameAndLocationId(
            createParticipantInput.name,
            createParticipantInput.locationId
        ) == null
}
