package info.jotajoti.jampuz.participant

import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.event.*
import info.jotajoti.jampuz.jidcode.*
import info.jotajoti.jampuz.security.*
import org.springframework.stereotype.*

@Service
class ParticipantService(
    private val participantRepository: ParticipantRepository,
    private val eventService: EventService,
) {

    fun createParticipant(input: CreateParticipantInput): Participant {

        val event = eventService.getById(input.eventId)

        val participant = Participant(
            name = input.name,
            pinCode = PinCode.random(),
            event = event,
        )

        return participantRepository.save(participant)
    }

    fun findParticipantsForEvent(eventId: EventId) = participantRepository.findByEventId(eventId)

    fun findParticipantForAdmin(admin: Admin, eventJidCode: JidCode, year: Int?) =
        eventService
            .findByOwnerAndCode(admin, eventJidCode, year)
            ?.let { event ->
                participantRepository
                    .findFirstByAdminAndEvent(admin, event)
            }

    fun findParticipantForAdmin(eventId: EventId, admin: Admin) =
        eventService
            .getByIdAndOwner(eventId, admin)
            .let { event ->
                participantRepository
                    .findFirstByAdminAndEvent(admin, event)
            }

    fun getParticipantForAdmin(eventId: EventId, admin: Admin) =
        findParticipantForAdmin(eventId, admin)
            ?: participantRepository.save(admin.toParticipant(eventService.getById(eventId)))

    private fun Admin.toParticipant(event: Event) =
        Participant(
            name = name,
            pinCode = PinCode.random(),
            admin = this,
            event = event,
        )
}
