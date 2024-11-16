package info.jotajoti.jampuz.security

import info.jotajoti.jampuz.event.*
import info.jotajoti.jampuz.exceptions.*
import info.jotajoti.jampuz.exceptions.ErrorCode.*
import info.jotajoti.jampuz.jidcode.*
import info.jotajoti.jampuz.participant.*
import info.jotajoti.jampuz.security.SubjectType.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.graphql.execution.ErrorType.*
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
            ?: throw ParticipantAuthenticationFailedException()


    @MutationMapping
    fun authenticateAdmin(@Argument email: String, @Argument password: String) =
        securityService
            .authenticateAdmin(email, password)
            ?.let {
                jwtService.createToken(Subject(ADMIN, it.id!!))
            }
            ?: throw AdminAuthenticationFailedException()

    @QueryMapping
    fun authenticatedAdmin(authentication: Authentication) = when (authentication) {
        is AdminAuthentication -> authentication.admin
        else -> null
    }

    @QueryMapping
    fun authenticatedParticipant(
        @Argument eventJidCode: JidCode?,
        @Argument year: Int?,
        authentication: Authentication
    ) = when (authentication) {
        is ParticipantAuthentication -> authentication.participant.takeIf {
            eventJidCode == null || eventService.findByCode(eventJidCode, year)?.id == it.event.id
        }

        is AdminAuthentication -> eventJidCode?.let {
            participantService.findParticipantForAdmin(
                authentication.admin,
                eventJidCode,
                year
            )
        }

        else -> null
    }
}

class AdminAuthenticationFailedException :
    ErrorCodeException("Authentication failed", ADMIN_AUTHENTICATION_FAILED, UNAUTHORIZED)

class ParticipantAuthenticationFailedException :
    ErrorCodeException("Authentication failed", PARTICIPANT_AUTHENTICATION_FAILED, UNAUTHORIZED)
