package info.jotajoti.jid.participant

import info.jotajoti.jid.admin.*
import info.jotajoti.jid.jidcode.*
import info.jotajoti.jid.location.*
import info.jotajoti.jid.security.*
import org.springframework.stereotype.*

@Service
class ParticipantService(
    private val participantRepository: ParticipantRepository,
    private val locationService: LocationService,
) {

    fun createParticipant(input: CreateParticipantInput): Participant {

        val location = locationService.getById(input.locationId)

        val participant = Participant(
            name = input.name,
            pinCode = PinCode.random(),
            location = location,
        )

        return participantRepository.save(participant)
    }

    fun findParticipantsForLocation(locationId: LocationId) = participantRepository.findByLocationId(locationId)

    fun findParticipantForAdmin(admin: Admin, locationCode: JidCode, year: Int?) =
        locationService
            .findByOwnerAndCode(admin, locationCode, year)
            ?.let { location ->
                participantRepository
                    .findFirstByAdminAndLocation(admin, location)
            }

    fun findParticipantForAdmin(locationId: LocationId, admin: Admin) =
        locationService
            .getByIdAndOwner(locationId, admin)
            .let { location ->
                participantRepository
                    .findFirstByAdminAndLocation(admin, location)
            }

    fun getParticipantForAdmin(locationId: LocationId, admin: Admin) =
        findParticipantForAdmin(locationId, admin)
            ?: participantRepository.save(admin.toParticipant(locationService.getById(locationId)))

    private fun Admin.toParticipant(location: Location) =
        Participant(
            name = name,
            pinCode = PinCode.random(),
            admin = this,
            location = location,
        )
}
