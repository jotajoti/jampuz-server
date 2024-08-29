package info.jotajoti.jid.security

import info.jotajoti.jid.jidcode.*
import info.jotajoti.jid.location.*
import info.jotajoti.jid.participant.*
import info.jotajoti.jid.security.SubjectType.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.security.core.*
import org.springframework.stereotype.*

@Controller
class SecurityController(
    private val securityService: SecurityService,
    private val participantService: ParticipantService,
    private val locationService: LocationService,
    private val jwtService: JwtService,
) {

    @MutationMapping
    fun authenticateParticipant(@Argument locationId: LocationId, @Argument name: String, @Argument pinCode: String) =
        securityService
            .authenticateParticipant(locationId, name, PinCode(pinCode))
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
            locationCode == null || locationService.findByCode(locationCode, year)?.id == it.location.id
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