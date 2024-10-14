package info.jotajoti.jampuz.jidcode

import info.jotajoti.jampuz.event.*

data class RegisterFoundJidCodeInput(

    @ValidJidCode
    val code: String,

    @ValidEvent
    val eventId: EventId,
)
