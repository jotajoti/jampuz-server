package info.jotajoti.jid.viewer

import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.participant.Participant

data class Viewer(
    val admin: Admin?,
    val participant: Participant?
)
