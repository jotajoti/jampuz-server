package info.jotajoti.jampuz.location

import jakarta.validation.*
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.*

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
