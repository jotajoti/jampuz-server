package info.jotajoti.jampuz.location

import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.exceptions.*
import info.jotajoti.jampuz.exceptions.ErrorCode.*
import info.jotajoti.jampuz.security.*
import org.springframework.data.repository.*
import org.springframework.graphql.execution.ErrorType.*
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

    fun addOwner(locationId: LocationId, adminEmail: String, authentication: AdminAuthentication): Location? {
        val admin = adminRepository.findOneByEmail(adminEmail) ?: throw AdminNotFoundException()

        if (admin.id == authentication.admin.id) {
            throw CannotAddSelfToLocationException()
        }

        return locationRepository
            .findByIdOrNull(locationId)
            ?.let { location ->
                location.owners += admin

                locationRepository.save(location)
            }
            ?: throw LocationNotFoundException(locationId)
    }

    fun removeOwner(locationId: LocationId, adminId: AdminId, authentication: AdminAuthentication): Location? {
        if (adminId == authentication.admin.id) {
            throw CannotRemoveSelfFromLocationException()
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

class AdminNotInLocationException :
    ErrorCodeException("Admin not part of location", ADMIN_NOT_IN_LOCATION, FORBIDDEN)

class AdminNotFoundException :
    ErrorCodeException("Admin not found", ADMIN_NOT_FOUND, NOT_FOUND)

class CannotAddSelfToLocationException :
    ErrorCodeException("Cannot add yourself to a location", CANNOT_ADD_SELF_TO_LOCATION, FORBIDDEN)

class CannotRemoveSelfFromLocationException :
    ErrorCodeException("Cannot remove yourself from a location", CANNOT_REMOVE_SELF_FROM_LOCATION, FORBIDDEN)

class LocationNotFoundException(locationId: LocationId) :
    ErrorCodeException("No location found with id $locationId", LOCATION_NOT_FOUND, NOT_FOUND)
