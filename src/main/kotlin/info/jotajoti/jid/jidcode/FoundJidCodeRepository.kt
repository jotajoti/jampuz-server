package info.jotajoti.jid.jidcode

import info.jotajoti.jid.location.Location
import info.jotajoti.jid.participant.Participant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface FoundJidCodeRepository : JpaRepository<FoundJidCode, FoundJidCodeId> {

    @Query("SELECT new info.jotajoti.jid.jidcode.AssociatedCode(fjc.participant.location.id, fjc.code) FROM FoundJidCode fjc WHERE fjc.participant.location in :locations")
    fun getJidCodesForLocations(@Param("locations") events: Set<Location>): List<AssociatedCode>

    @Query("SELECT new info.jotajoti.jid.jidcode.AssociatedCode(fjc.participant.id, fjc.code) FROM FoundJidCode fjc WHERE fjc.participant in :participants")
    fun getJidCodesForParticipants(@Param("participants") participants: Set<Participant>): List<AssociatedCode>
}

data class AssociatedCode(
    val id: UUID,
    val code: String,
)
