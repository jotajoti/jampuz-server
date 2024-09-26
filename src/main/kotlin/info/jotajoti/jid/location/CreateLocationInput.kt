package info.jotajoti.jid.location

import info.jotajoti.jid.admin.*
import jakarta.validation.constraints.*

data class CreateLocationInput(
    @get:NotBlank
    val name: String,
) {

    fun toEntity(admin: Admin) =
        Location(
            name = name,
            owners = listOf(admin),
        )
}
