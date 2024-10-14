package info.jotajoti.jampuz.location

import info.jotajoti.jampuz.security.*
import org.springframework.security.core.*
import org.springframework.stereotype.*

@Service
class LocationSecurityService(
    private val locationRepository: LocationRepository,
) {

    fun isAuthenticationOwnerOfLocation(locationId: LocationId?, authentication: Authentication) =
        when (authentication) {
            is AdminAuthentication -> locationRepository.findByIdAndOwner(locationId!!, authentication.admin) != null
            else -> false
        }
}
