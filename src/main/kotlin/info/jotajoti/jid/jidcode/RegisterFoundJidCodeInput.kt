package info.jotajoti.jid.jidcode

import info.jotajoti.jid.location.*

data class RegisterFoundJidCodeInput(

    @ValidJidCode
    val code: String,

    @ValidLocation
    val locationId: LocationId,
)
