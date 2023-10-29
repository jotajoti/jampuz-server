package info.jotajoti.jid.location

import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.jidcode.JidCode
import info.jotajoti.jid.jidcode.ValidJidCode
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

@UniqueCodeAndYear
data class CreateLocationInput(
    @get:NotBlank
    val name: String,

    @ValidJidCode
    val code: String,

    @get:Min(2000) @get:Max(2100)
    val year: Int,
) {

    fun toEntity(admin: Admin) =
        Location(
            name = name,
            code = JidCode(code),
            year = year,
            owners = listOf(admin),
        )
}
