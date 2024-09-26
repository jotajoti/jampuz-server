package info.jotajoti.jid.location

import info.jotajoti.jid.admin.*
import info.jotajoti.jid.participant.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.repository.query.*

interface LocationRepository : JpaRepository<Location, LocationId> {

    fun findAllByOwnersContains(owner: Admin): List<Location>

    @Query("SELECT l FROM Admin a JOIN a.locations l WHERE a = :owner AND l.id = :id")
    fun findByIdAndOwner(@Param("id") id: LocationId, @Param("owner") owner: Admin): Location?
}
