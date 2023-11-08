package info.jotajoti.jid.participant

import info.jotajoti.jid.location.*
import jakarta.validation.constraints.*

@UniqueNameAndLocation
data class CreateParticipantInput(
    @get:NotBlank
    val name: String,

    @ValidLocation
    val locationId: LocationId,
)
