package info.jotajoti.jid.location

import info.jotajoti.jid.security.AdminAuthentication
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class LocationSecurityService(
    private val locationRepository: LocationRepository,
) {

    fun isAuthenticationOwnerOfLocation(locationId: LocationId?, authentication: Authentication) = when(authentication) {
        is AdminAuthentication -> locationRepository.findByIdAndOwner(locationId!!, authentication.admin) != null
        else -> false
    }
}
