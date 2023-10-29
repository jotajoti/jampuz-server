package info.jotajoti.jid.participant

import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.location.Location
import info.jotajoti.jid.location.LocationId
import org.springframework.data.jpa.repository.JpaRepository

interface ParticipantRepository : JpaRepository<Participant, ParticipantId> {

    fun findFirstByNameAndLocationId(name: String, locationId: LocationId): Participant?

    fun findFirstByAdminAndLocation(admin: Admin, location: Location): Participant?
}
