package info.jotajoti.jampuz.event

import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.exceptions.*
import info.jotajoti.jampuz.exceptions.ErrorCode.*
import info.jotajoti.jampuz.jidcode.*
import info.jotajoti.jampuz.location.*
import info.jotajoti.jampuz.participant.*
import org.springframework.graphql.execution.ErrorType.*
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
            active = input.active,
        )

        return eventRepository.save(event)
    }

    fun updateEvent(input: UpdateEventInput): Event {

        val event = eventRepository.getReferenceById(input.id)

        event.apply {
            if (input.code != null) {
                code = JidCode(input.code)
            }
            if (input.year != null) {
                year = input.year
            }
            if (input.active != null) {
                active = input.active
            }
        }

        if (event.otherEventExistWithCodeAndYear()) {
            throw EventCodeAndYearNotAvailableException()
        }

        return eventRepository.save(event)
    }

    private fun Event.otherEventExistWithCodeAndYear(): Boolean {
        val existingEvent = eventRepository.findFirstByCodeCodeIgnoreCaseAndYear(code.code, year)
        return when {
            existingEvent == null -> false
            existingEvent.id != id -> true
            else -> false
        }
    }
}

class EventCodeAndYearNotAvailableException : ErrorCodeException(
    "An event with the same code and year is already created",
    EVENT_CODE_AND_YEAR_NOT_AVAILABLE,
    BAD_REQUEST
)
