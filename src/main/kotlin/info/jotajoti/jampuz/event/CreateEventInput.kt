package info.jotajoti.jampuz.event

import info.jotajoti.jampuz.jidcode.*
import info.jotajoti.jampuz.location.*
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
