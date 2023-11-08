package info.jotajoti.jid.participant

import jakarta.validation.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*

@Controller
@Validated
class ParticipantController(
    private val participantService: ParticipantService,
) {

    @MutationMapping
    fun createParticipant(@Valid @Argument input: CreateParticipantInput) =
        participantService.createParticipant(input)

}
