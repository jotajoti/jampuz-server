package info.jotajoti.jid.participant

import info.jotajoti.jid.admin.*
import info.jotajoti.jid.location.*
import org.springframework.data.jpa.repository.*

interface ParticipantRepository : JpaRepository<Participant, ParticipantId> {

    fun findFirstByNameAndLocationId(name: String, locationId: LocationId): Participant?

    fun findFirstByAdminAndLocation(admin: Admin, location: Location): Participant?

    fun findFirstByLocationIdAndNameAndPinCode(locationId: LocationId, name: String, pinCode: String): Participant?

    fun findByLocationId(locationId: LocationId): List<Participant>
}
