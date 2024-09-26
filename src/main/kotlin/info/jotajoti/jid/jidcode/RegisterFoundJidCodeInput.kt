package info.jotajoti.jid.jidcode

import info.jotajoti.jid.event.*

data class RegisterFoundJidCodeInput(

    @ValidJidCode
    val code: String,

    @ValidEvent
    val eventId: EventId,
)
