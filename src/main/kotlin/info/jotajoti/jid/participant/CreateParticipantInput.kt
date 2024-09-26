package info.jotajoti.jid.participant

import info.jotajoti.jid.event.*
import jakarta.validation.constraints.*

@UniqueNameAndEvent
data class CreateParticipantInput(
    @get:NotBlank
    val name: String,

    @ValidEvent
    val eventId: EventId,
)
