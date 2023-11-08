package info.jotajoti.jid.location

import info.jotajoti.jid.admin.*
import info.jotajoti.jid.participant.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.repository.query.*

interface LocationRepository : JpaRepository<Location, LocationId> {

    fun findFirstByCodeCodeIgnoreCaseOrderByYearDesc(code: String): Location?

    fun findFirstByCodeCodeIgnoreCaseAndYear(code: String, year: Int): Location?

    fun findFirstByOwnersContainsAndCodeCodeIgnoreCaseOrderByYearDesc(admin: Admin, code: String): Location?

    fun findFirstByOwnersContainsAndCodeCodeIgnoreCaseAndYear(admin: Admin, code: String, year: Int): Location?


    fun findAllByOwnersContains(owner: Admin): List<Location>

    @Query("SELECT p.location FROM Participant p WHERE p = :participant")
    fun findByParticipant(@Param("participant") participant: Participant): Location?

    @Query("SELECT l FROM Admin a JOIN a.locations l WHERE a = :owner AND l.id = :id")
    fun findByIdAndOwner(@Param("id") id: LocationId, @Param("owner") owner: Admin): Location?
}
