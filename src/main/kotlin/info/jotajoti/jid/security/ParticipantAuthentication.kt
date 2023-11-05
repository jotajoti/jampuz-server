package info.jotajoti.jid.security

import info.jotajoti.jid.participant.Participant
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

val PARTICIPANT_AUTHORITY = SimpleGrantedAuthority("PARTICIPANT")

class ParticipantAuthentication(val participant: Participant) : AbstractAuthenticationToken(listOf(PARTICIPANT_AUTHORITY)) {

    private val principal = ParticipantPrincipal(participant)

    override fun getCredentials(): Any {
        TODO("Not yet implemented")
    }

    override fun getPrincipal(): Any = principal

    override fun getName() = participant.name
}

private class ParticipantPrincipal(private val participant: Participant) {
    override fun toString(): String {
        return participant.name
    }
}
