package info.jotajoti.jid.location

import info.jotajoti.jid.jidcode.ValidJidCodeValidator
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
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

@Target(FUNCTION, FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidLocationValidator::class])
annotation class ValidLocation(
    val message: String = "Location must exist",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidLocationValidator(
    private val locationRepository: LocationRepository
) : ConstraintValidator<ValidLocation, LocationId> {

    override fun isValid(locationId: LocationId, context: ConstraintValidatorContext?) =
        locationRepository.existsById(locationId)
}