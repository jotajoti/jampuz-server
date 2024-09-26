package info.jotajoti.jid.participant

import graphql.*
import info.jotajoti.jid.event.*
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
    private val eventService: EventService,
    private val subscriptionService: SubscriptionService,
) {

    @SchemaMapping
    fun pinCode(
        participant: Participant,
        authentication: Authentication,
        @ContextValue(required = false) participantCreationContext: ParticipantCreationContext?
    ) = when (authentication) {
        is AdminAuthentication -> eventService
            .getByIdAndOwner(participant.event.id!!, authentication.admin)
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

    @MutationMapping
    fun createParticipant(@Valid @Argument input: CreateParticipantInput, graphQlContext: GraphQLContext) =
        participantService
            .createParticipant(input)
            .also {
                graphQlContext.put("participantCreationContext", ParticipantCreationContext(it.id))
            }
            .also {
                subscriptionService
                    .publishMessage(ParticipantsSubscription(it.event.id!!))
            }

    @SubscriptionMapping
    fun participants(@Argument eventId: EventId) =
        subscriptionService
            .subscribe(ParticipantsSubscription::class)
            .map {
                participantService.findParticipantsForEvent(it.eventId)
            }

}

data class ParticipantCreationContext(
    var createdParticipantId: ParticipantId?
)
