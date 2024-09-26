package info.jotajoti.jid.jidcode

import info.jotajoti.jid.event.*
import info.jotajoti.jid.participant.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.repository.query.*
import java.util.*

interface FoundJidCodeRepository : JpaRepository<FoundJidCode, FoundJidCodeId> {

    fun findFoundJidCodesByParticipantEventId(eventId: EventId): List<FoundJidCode>

    @Query("SELECT new info.jotajoti.jid.jidcode.AssociatedCode(fjc.participant.event.id, fjc.code) FROM FoundJidCode fjc WHERE fjc.participant.event in :events")
    fun getJidCodesForEvents(@Param("events") events: Set<Event>): List<AssociatedCode>

    @Query("SELECT new info.jotajoti.jid.jidcode.AssociatedCode(fjc.participant.id, fjc.code) FROM FoundJidCode fjc WHERE fjc.participant in :participants")
    fun getJidCodesForParticipants(@Param("participants") participants: Set<Participant>): List<AssociatedCode>
}

data class AssociatedCode(
    val id: UUID,
    val code: JidCode,
)
