package info.jotajoti.jid.location

import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.admin.AdminId
import info.jotajoti.jid.admin.AdminRepository
import info.jotajoti.jid.jidcode.JidCode
import info.jotajoti.jid.participant.Participant
import info.jotajoti.jid.security.AdminAuthentication
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class LocationService(
    private val locationRepository: LocationRepository,
    private val adminRepository: AdminRepository,
) {

    fun getById(locationId: LocationId) = locationRepository.getReferenceById(locationId)

    fun findAllByOwner(admin: Admin) = locationRepository.findAllByOwnersContains(admin)

    fun findByParticipant(participant: Participant) = locationRepository.findByParticipant(participant)

    fun findByIdAndOwner(locationId: LocationId, admin: Admin) = locationRepository.findByIdAndOwner(locationId, admin)

    fun findByIdAndParticipant(locationId: LocationId, participant: Participant) =
        locationRepository.findByIdAndParticipant(locationId, participant)

    fun findByCodeAndOwner(code: JidCode, admin: Admin) = locationRepository.findByCodeAndOwner(code, admin)

    fun findByCodeAndParticipant(code: JidCode, participant: Participant) =
        locationRepository.findByCodeAndParticipant(code, participant)

    fun createLocation(location: Location) =
        locationRepository.save(location)

    fun addOwner(locationId: LocationId, adminId: AdminId, authentication: AdminAuthentication): Location? {
        if (adminId == authentication.admin.id) {
            throw CannotAddSelfToLocation()
        }

        return locationRepository
            .findByIdOrNull(locationId)
            ?.let { location ->
                location to adminRepository.findByIdOrNull(adminId)
            }
            ?.takeIf { it.second != null }
            ?.let {
                val location = it.first
                val admin = it.second!!

                location.owners += admin

                locationRepository.save(location)
            }
            ?: throw LocationNotFoundException(locationId)
    }

    fun removeOwner(locationId: LocationId, adminId: AdminId, authentication: AdminAuthentication): Location? {
        if (adminId == authentication.admin.id) {
            throw CannotRemoveSelfFromLocation()
        }

        return locationRepository
            .findByIdOrNull(locationId)
            ?.let { location ->
                location.owners = location.owners.filter { it.id != adminId }

                locationRepository.save(location)
            }
            ?: throw LocationNotFoundException(locationId)
    }
}

class LocationNotFoundException(locationId: LocationId) : Exception("No location found with id $locationId")
class CannotAddSelfToLocation : Exception("Cannot add yourself to a location")
class CannotRemoveSelfFromLocation : Exception("Cannot remove yourself from a location")
