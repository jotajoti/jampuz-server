package info.jotajoti.jid.participant

import info.jotajoti.jid.location.Location
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class ParticipantController {

    @SchemaMapping
    fun participants(location: Location) =
        location.participants

}