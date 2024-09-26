package info.jotajoti.jid.event

import info.jotajoti.jid.jidcode.*
import info.jotajoti.jid.security.*
import jakarta.validation.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*

@Controller
@Validated
class EventController(
    private val eventService: EventService,
) {

    @SchemaMapping
    fun code(event: Event) = event.code

    @SchemaMapping
    fun isLatest(event: Event) =
        eventService.isLatest(event)

    @SchemaMapping
    fun previous(event: Event) =
        eventService.findPrevious(event)

    @SchemaMapping
    fun next(event: Event) =
        eventService.findNext(event)

    @QueryMapping
    fun event(@Argument code: JidCode, @Argument year: Int?) =
        eventService.findByCode(code, year)

    @IsOwnerOfLocation
    @MutationMapping
    fun createEvent(
        @Valid @Argument input: CreateEventInput,
        adminAuthentication: AdminAuthentication,
    ) = eventService.createEvent(input)
}
