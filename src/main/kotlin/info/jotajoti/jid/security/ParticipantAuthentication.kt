package info.jotajoti.jid.security

import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.participant.Participant
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

val PARTICIPANT_AUTHORITY = SimpleGrantedAuthority("PARTICIPANT")

class ParticipantAuthentication(val participant: Participant) : AbstractAuthenticationToken(listOf(PARTICIPANT_AUTHORITY)) {
    override fun getCredentials(): Any {
        TODO("Not yet implemented")
    }

    override fun getPrincipal() = this

    override fun getName() = participant.name
}
