package info.jotajoti.jid.location

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.reflect.KClass

@Target(CLASS)
@Retention(RUNTIME)
@Constraint(validatedBy = [UniqueCodeAndYearValidator::class])
annotation class UniqueCodeAndYear(
    val message: String = "A location with the same code and year is already created",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class UniqueCodeAndYearValidator(
    private val locationRepository: LocationRepository,
) : ConstraintValidator<UniqueCodeAndYear, CreateLocationInput> {

    override fun isValid(createLocationInput: CreateLocationInput, context: ConstraintValidatorContext?) =
        locationRepository.findFirstByCodeAndYear(createLocationInput.code, createLocationInput.year) == null
}
