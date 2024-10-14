package info.jotajoti.jampuz.location

import info.jotajoti.jampuz.admin.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.repository.query.*

interface LocationRepository : JpaRepository<Location, LocationId> {

    fun findAllByOwnersContains(owner: Admin): List<Location>

    @Query("SELECT l FROM Admin a JOIN a.locations l WHERE a = :owner AND l.id = :id")
    fun findByIdAndOwner(@Param("id") id: LocationId, @Param("owner") owner: Admin): Location?
}
