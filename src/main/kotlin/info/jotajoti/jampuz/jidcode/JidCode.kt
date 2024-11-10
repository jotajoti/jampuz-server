package info.jotajoti.jampuz.jidcode

import jakarta.persistence.*
import jakarta.validation.*
import java.util.*
import java.util.Locale.IsoCountryCode.*
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.*

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
        get() = Region.entries.find { it.code == code[0].digitToInt() }!!

    @get:Transient
    val country: String
        get() = code.lowercase().substring(1, 3)
}

@Target(FUNCTION, FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidJidCodeValidator::class])
annotation class ValidJidCode(
    val message: String = "Code must be a valid JID code",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidJidCodeValidator : ConstraintValidator<ValidJidCode, String?> {

    override fun isValid(code: String?, context: ConstraintValidatorContext?) =
        code?.let { JidCode.isValid(code) } ?: true
}
