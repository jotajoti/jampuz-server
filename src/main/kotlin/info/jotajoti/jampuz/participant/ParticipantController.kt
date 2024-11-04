package info.jotajoti.jampuz.participant

import graphql.*
import info.jotajoti.jampuz.event.*
import info.jotajoti.jampuz.security.*
import info.jotajoti.jampuz.subscription.*
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
    ): String? = when (authentication) {
        is AdminAuthentication -> eventService
            .getByIdAndOwner(participant.event.id!!, authentication.admin)
            .let { participant.pinCode.value }

        is ParticipantAuthentication -> participant
            .pinCode
            .takeIf {
                participant.id == authentication.participant.id
                        || participantCreationContext?.createdParticipantId == participant.id
            }
            ?.value

        else -> participant
            .pinCode
            .takeIf { participantCreationContext?.createdParticipantId == participant.id }
            ?.value
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
                    .publishMessage(EventSubscription(it.event.id!!))
            }

}

data class ParticipantCreationContext(
    var createdParticipantId: ParticipantId?
)
