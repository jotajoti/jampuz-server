package info.jotajoti.jid.participant

import graphql.*
import info.jotajoti.jid.location.*
import info.jotajoti.jid.security.*
import info.jotajoti.jid.subscription.*
import jakarta.validation.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.security.core.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*

@Controller
@Validated
class ParticipantController(
    private val participantService: ParticipantService,
    private val locationService: LocationService,
    private val subscriptionService: SubscriptionService,
) {

    @SchemaMapping
    fun pinCode(
        participant: Participant,
        authentication: Authentication,
        @ContextValue(required = false) participantCreationContext: ParticipantCreationContext?
    ) = when (authentication) {
        is AdminAuthentication -> locationService
            .getByIdAndOwner(participant.location.id!!, authentication.admin)
            .let { participant.pinCode }

        is ParticipantAuthentication -> participant
            .pinCode
            .takeIf {
                participant.id == authentication.participant.id
                        || participantCreationContext?.createdParticipantId == participant.id
            }

        else -> participant
            .pinCode
            .takeIf { participantCreationContext?.createdParticipantId == participant.id }
    }
        ?.value

    @MutationMapping
    fun createParticipant(@Valid @Argument input: CreateParticipantInput, graphQlContext: GraphQLContext) =
        participantService
            .createParticipant(input)
            .also {
                graphQlContext.put("participantCreationContext", ParticipantCreationContext(it.id))
            }
            .also {
                subscriptionService
                    .publishMessage(ParticipantsSubscription(it.location.id!!))
            }

    @SubscriptionMapping
    fun participants(@Argument locationId: LocationId) =
        subscriptionService
            .subscribe(ParticipantsSubscription::class)
            .map {
                participantService.findParticipantsForLocation(it.locationId)
            }

}

data class ParticipantCreationContext(
    var createdParticipantId: ParticipantId?
)
