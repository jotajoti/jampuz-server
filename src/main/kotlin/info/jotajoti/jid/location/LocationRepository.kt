package info.jotajoti.jid.location

import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.jidcode.JidCode
import info.jotajoti.jid.participant.Participant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LocationRepository : JpaRepository<Location, LocationId> {

    fun findFirstByCodeAndYear(code: JidCode, year: Int): Location?

    fun findAllByOwnersContains(owner: Admin): List<Location>

    @Query("SELECT p.location FROM Participant p WHERE p = :participant")
    fun findByParticipant(@Param("participant") participant: Participant): Location?

    @Query("SELECT l FROM Admin a JOIN a.locations l WHERE a = :owner AND l.id = :id")
    fun findByIdAndOwner(@Param("id") id: LocationId, @Param("owner") owner: Admin): Location?

    @Query("SELECT p.location FROM Participant p WHERE p = :participant AND p.location.id = :id")
    fun findByIdAndParticipant(@Param("id") id: LocationId, @Param("participant") participant: Participant): Location?

    @Query("SELECT l FROM Admin a JOIN a.locations l WHERE a = :owner AND l.code = :code")
    fun findByCodeAndOwner(@Param("code") code: JidCode, @Param("owner") owner: Admin): Location?

    @Query("SELECT p.location FROM Participant p WHERE p = :participant AND p.location.code = :code")
    fun findByCodeAndParticipant(@Param("code") code: JidCode, @Param("participant") participant: Participant): Location?
}
