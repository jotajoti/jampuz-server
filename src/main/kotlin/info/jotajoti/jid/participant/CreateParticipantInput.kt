package info.jotajoti.jid.participant

import info.jotajoti.jid.location.LocationId
import info.jotajoti.jid.location.ValidLocation
import jakarta.validation.constraints.NotBlank

@UniqueNameAndLocation
data class CreateParticipantInput(
    @get:NotBlank
    val name: String,

    @ValidLocation
    val locationId: LocationId,
)
