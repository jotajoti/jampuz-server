package info.jotajoti.jid.event

import info.jotajoti.jid.admin.*
import info.jotajoti.jid.jidcode.*
import info.jotajoti.jid.location.*
import info.jotajoti.jid.participant.*
import org.springframework.stereotype.*

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val locationService: LocationService,
) {

    fun getById(eventId: EventId) = eventRepository.getReferenceById(eventId)

    fun findByParticipant(participant: Participant) = eventRepository.findByParticipant(participant)

    fun findByCode(jidCode: JidCode, year: Int?) =
        if (year == null) {
            eventRepository.findFirstByCodeCodeIgnoreCaseOrderByYearDesc(jidCode.code)
        } else {
            eventRepository.findFirstByCodeCodeIgnoreCaseAndYear(jidCode.code, year)
        }

    fun findByOwnerAndCode(admin: Admin, jidCode: JidCode, year: Int?) =
        findByCode(jidCode, year)
            ?.also {
                if (!it.location.owners.any { owner -> owner.id == admin.id }) {
                    throw AdminNotInLocationException()
                }
            }

    fun getByIdAndOwner(eventId: EventId, admin: Admin) =
        eventRepository.findByIdAndOwner(eventId, admin)
            ?: throw AdminNotInLocationException()

    fun isLatest(event: Event) =
        event.id == eventRepository.findFirstByLocationIdOrderByYearDesc(event.location.id!!)?.id

    fun findPrevious(currentEvent: Event) =
        eventRepository.findFirstByLocationIdAndYearLessThanOrderByYearDesc(
            currentEvent.location.id!!,
            currentEvent.year
        )

    fun findNext(currentEvent: Event) =
        eventRepository.findFirstByLocationIdAndYearGreaterThanOrderByYearAsc(
            currentEvent.location.id!!,
            currentEvent.year
        )

    fun createEvent(input: CreateEventInput): Event {

        val location = locationService.getById(input.locationId)

        val event = Event(
            location = location,
            code = JidCode(input.code),
            year = input.year,
        )

        return eventRepository.save(event)
    }
}
