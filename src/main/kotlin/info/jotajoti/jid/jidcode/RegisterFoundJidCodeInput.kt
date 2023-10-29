package info.jotajoti.jid.jidcode

import info.jotajoti.jid.location.LocationId
import info.jotajoti.jid.location.ValidLocation

data class RegisterFoundJidCodeInput(

    @ValidJidCode
    val code: String,

    @ValidLocation
    val locationId: LocationId,
)
