package info.jotajoti.jid.event

import info.jotajoti.jid.jidcode.*
import info.jotajoti.jid.location.*
import jakarta.validation.constraints.*

@UniqueCodeAndYear
data class CreateEventInput(
    @ValidLocation
    val locationId: LocationId,

    @ValidJidCode
    val code: String,

    @get:Min(2000) @get:Max(2100)
    val year: Int,
)
