package info.jotajoti.jampuz.participant

import info.jotajoti.jampuz.event.*
import jakarta.validation.constraints.*

@UniqueNameAndEvent
data class CreateParticipantInput(
    @get:NotBlank
    val name: String,

    @ValidEvent
    val eventId: EventId,
)
