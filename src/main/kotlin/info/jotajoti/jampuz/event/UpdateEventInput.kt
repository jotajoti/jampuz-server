package info.jotajoti.jampuz.event

import info.jotajoti.jampuz.jidcode.*
import jakarta.validation.constraints.*

data class UpdateEventInput(
    @ValidEvent
    val id: EventId,

    @ValidJidCode
    val code: String?,

    @get:Min(2000) @get:Max(2100)
    val year: Int?,

    val active: Boolean?,
)
