package info.jotajoti.jampuz.event

import info.jotajoti.jampuz.security.*
import org.springframework.security.core.*
import org.springframework.stereotype.*

@Service
class EventSecurityService(
    private val eventRepository: EventRepository,
) {

    fun isParticipatingInEvent(eventId: EventId, authentication: Authentication) = when (authentication) {
        is ParticipantAuthentication -> authentication.participant.event.id == eventId
        is AdminAuthentication -> authentication.admin.locations.any { location -> location.events.any { event -> event.id == eventId } }
        else -> false
    }
}