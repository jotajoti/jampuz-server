package info.jotajoti.jid.jidcode

import info.jotajoti.jid.location.*
import info.jotajoti.jid.participant.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.repository.query.*
import java.util.*

interface FoundJidCodeRepository : JpaRepository<FoundJidCode, FoundJidCodeId> {

    fun findFoundJidCodesByParticipantLocationId(locationId: LocationId): List<FoundJidCode>

    @Query("SELECT new info.jotajoti.jid.jidcode.AssociatedCode(fjc.participant.location.id, fjc.code) FROM FoundJidCode fjc WHERE fjc.participant.location in :locations")
    fun getJidCodesForLocations(@Param("locations") locations: Set<Location>): List<AssociatedCode>

    @Query("SELECT new info.jotajoti.jid.jidcode.AssociatedCode(fjc.participant.id, fjc.code) FROM FoundJidCode fjc WHERE fjc.participant in :participants")
    fun getJidCodesForParticipants(@Param("participants") participants: Set<Participant>): List<AssociatedCode>
}

data class AssociatedCode(
    val id: UUID,
    val code: JidCode,
)
