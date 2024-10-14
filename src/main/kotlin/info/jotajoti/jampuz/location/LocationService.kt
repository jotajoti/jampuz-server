package info.jotajoti.jampuz.location

import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.security.*
import org.springframework.data.repository.*
import org.springframework.stereotype.*

@Service
class LocationService(
    private val locationRepository: LocationRepository,
    private val adminRepository: AdminRepository,
) {

    fun getById(locationId: LocationId) = locationRepository.getReferenceById(locationId)

    fun findAllByOwner(admin: Admin) = locationRepository.findAllByOwnersContains(admin)

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

class AdminNotInLocationException : Exception("Admin not part of location")
class CannotAddSelfToLocation : Exception("Cannot add yourself to a location")
class CannotRemoveSelfFromLocation : Exception("Cannot remove yourself from a location")
class LocationNotFoundException(locationId: LocationId) : Exception("No location found with id $locationId")
