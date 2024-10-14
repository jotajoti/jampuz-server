package info.jotajoti.jampuz.event

import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.location.*
import info.jotajoti.jampuz.participant.*
import org.springframework.data.jpa.repository.*
import org.springframework.data.repository.query.*

interface EventRepository : JpaRepository<Event, EventId> {

    fun findFirstByLocationIdOrderByYearDesc(locationId: LocationId): Event?

    fun findFirstByCodeCodeIgnoreCaseOrderByYearDesc(code: String): Event?

    fun findFirstByCodeCodeIgnoreCaseAndYear(code: String, year: Int): Event?

    fun findFirstByLocationIdAndYearLessThanOrderByYearDesc(locationId: LocationId, year: Int): Event?

    fun findFirstByLocationIdAndYearGreaterThanOrderByYearAsc(locationId: LocationId, year: Int): Event?

    @Query("SELECT e FROM Admin a JOIN a.locations l JOIN l.events e WHERE a = :owner AND e.id = :id")
    fun findByIdAndOwner(@Param("id") id: EventId, @Param("owner") owner: Admin): Event?

    @Query("SELECT p.event FROM Participant p WHERE p = :participant")
    fun findByParticipant(@Param("participant") participant: Participant): Event?
}