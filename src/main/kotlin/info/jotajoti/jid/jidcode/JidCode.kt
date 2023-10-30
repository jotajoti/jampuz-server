package info.jotajoti.jid.jidcode

import jakarta.persistence.Embeddable
import jakarta.persistence.Transient
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import java.util.*
import java.util.Locale.IsoCountryCode.PART1_ALPHA2
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.reflect.KClass

@Embeddable
data class JidCode(
    val code: String
) {

    init {
        assert(isValid(code))
    }

    companion object {

        private val pattern = "[1-7]([a-z]{2})[0-9]{2}[a-z]".toRegex()

        fun isValid(code: String) =
            pattern
                .find(code.lowercase())
                ?.let {
                    val countryCode = it.groupValues[1].uppercase()
                    Locale.getISOCountries(PART1_ALPHA2).contains(countryCode)
                }
                ?: false
    }

    @get:Transient
    val region: Region
        get() = Region.values().find { it.code == code[0].digitToInt() }!!

    @get:Transient
    val country: String
        get() = code.lowercase().substring(1, 3)
}

fun String.toJidCode() = JidCode(this)

enum class Region(val code: Int) {
    AFRICA(1),
    ARAB(2),
    ASIA_PACIFIC(3),
    EUROASIA(4),
    EUROPE(5),
    INTERAMERICA(6),
    SPECIAL(7)
}

@Target(FUNCTION, FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidJidCodeValidator::class])
annotation class ValidJidCode(
    val message: String = "Code must be a valid JID code",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidJidCodeValidator : ConstraintValidator<ValidJidCode, String> {

    override fun isValid(code: String, context: ConstraintValidatorContext?) =
        JidCode.isValid(code)
}
