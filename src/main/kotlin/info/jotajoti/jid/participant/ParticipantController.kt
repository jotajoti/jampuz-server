package info.jotajoti.jid.participant

import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated

@Controller
@Validated
class ParticipantController(
    private val participantService: ParticipantService,
) {

    @MutationMapping
    fun createParticipant(@Valid @Argument input: CreateParticipantInput) =
        participantService.createParticipant(input)

}
