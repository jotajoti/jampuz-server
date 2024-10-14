package info.jotajoti.jampuz.security

import info.jotajoti.jampuz.event.*
import info.jotajoti.jampuz.jidcode.*
import info.jotajoti.jampuz.participant.*
import info.jotajoti.jampuz.security.SubjectType.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.security.core.*
import org.springframework.stereotype.*

@Controller
class SecurityController(
    private val securityService: SecurityService,
    private val participantService: ParticipantService,
    private val eventService: EventService,
    private val jwtService: JwtService,
) {

    @MutationMapping
    fun authenticateParticipant(@Argument eventId: EventId, @Argument name: String, @Argument pinCode: String) =
        securityService
            .authenticateParticipant(eventId, name, PinCode(pinCode))
            ?.let {
                jwtService.createToken(Subject(PARTICIPANT, it.id!!))
            }


    @MutationMapping
    fun authenticateAdmin(@Argument email: String, @Argument password: String) =
        securityService
            .authenticateAdmin(email, password)
            ?.let {
                jwtService.createToken(Subject(ADMIN, it.id!!))
            }

    @QueryMapping
    fun authenticatedAdmin(authentication: Authentication) = when (authentication) {
        is AdminAuthentication -> authentication.admin
        else -> null
    }

    @QueryMapping
    fun authenticatedParticipant(
        @Argument locationCode: JidCode?,
        @Argument year: Int?,
        authentication: Authentication
    ) = when (authentication) {
        is ParticipantAuthentication -> authentication.participant.takeIf {
            locationCode == null || eventService.findByCode(locationCode, year)?.id == it.event.id
        }

        is AdminAuthentication -> locationCode?.let {
            participantService.findParticipantForAdmin(
                authentication.admin,
                locationCode,
                year
            )
        }

        else -> null
    }
}