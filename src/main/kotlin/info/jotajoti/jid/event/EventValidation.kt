package info.jotajoti.jid.event

import jakarta.validation.*
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.*

@Target(CLASS)
@Retention(RUNTIME)
@Constraint(validatedBy = [UniqueCodeAndYearValidator::class])
annotation class UniqueCodeAndYear(
    val message: String = "An event with the same code and year is already created",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class UniqueCodeAndYearValidator(
    private val eventRepository: EventRepository,
) : ConstraintValidator<UniqueCodeAndYear, CreateEventInput> {

    override fun isValid(createEventInput: CreateEventInput, context: ConstraintValidatorContext?) =
        eventRepository.findFirstByCodeCodeIgnoreCaseAndYear(
            createEventInput.code,
            createEventInput.year
        ) == null
}

@Target(FUNCTION, FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidEventValidator::class])
annotation class ValidEvent(
    val message: String = "Event must exist",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidEventValidator(
    private val eventRepository: EventRepository
) : ConstraintValidator<ValidEvent, EventId> {

    override fun isValid(eventId: EventId, context: ConstraintValidatorContext?) =
        eventRepository.existsById(eventId)
}
