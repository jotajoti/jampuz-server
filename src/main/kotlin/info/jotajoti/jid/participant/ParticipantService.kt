package info.jotajoti.jid.participant

import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.location.Location
import info.jotajoti.jid.location.LocationId
import info.jotajoti.jid.location.LocationService
import info.jotajoti.jid.security.PinCode
import org.springframework.stereotype.Service

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

    fun getParticipantForAdmin(locationId: LocationId, admin: Admin) =
        locationService
            .findByIdAndOwner(locationId, admin)
            ?.let { location ->
                participantRepository
                    .findFirstByAdminAndLocation(admin, location)
                    ?: participantRepository.save(admin.toParticipant(location))
            }
            ?: throw AdminNotInLocationException()

    private fun Admin.toParticipant(location: Location) =
        Participant(
            name = name,
            pinCode = PinCode.random(),
            admin = this,
            location = location,
        )
}

class AdminNotInLocationException : Exception("Admin not part of location")
