package info.jotajoti.jampuz.participant

import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.event.*
import org.springframework.data.jpa.repository.*

interface ParticipantRepository : JpaRepository<Participant, ParticipantId> {

    fun findFirstByNameAndEventId(name: String, eventId: EventId): Participant?

    fun findFirstByAdminAndEvent(admin: Admin, event: Event): Participant?

    fun findFirstByEventIdAndNameAndPinCode(eventId: EventId, name: String, pinCode: String): Participant?

    fun findByEventId(eventId: EventId): List<Participant>
}
