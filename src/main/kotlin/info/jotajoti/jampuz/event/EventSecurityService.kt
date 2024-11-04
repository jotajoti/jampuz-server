package info.jotajoti.jampuz.event

import info.jotajoti.jampuz.location.*
import info.jotajoti.jampuz.security.*
import org.springframework.security.core.*
import org.springframework.stereotype.*

@Service
class EventSecurityService(
    private val locationService: LocationService,
) {

    fun isParticipatingInEvent(eventId: EventId, authentication: Authentication) = when (authentication) {
        is ParticipantAuthentication -> authentication.participant.event.id == eventId
        is AdminAuthentication -> locationService.findAllByOwner(authentication.admin)
            .any { location -> location.events.any { event -> event.id == eventId } }

        else -> false
    }
}