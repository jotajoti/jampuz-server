package info.jotajoti.jampuz.location

import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.security.*
import jakarta.validation.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.security.core.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*

@Controller
@Validated
class LocationController(
    private val locationService: LocationService,
) {

    @QueryMapping
    fun locations(authentication: Authentication) = when (authentication) {
        is AdminAuthentication -> locationService.findAllByOwner(authentication.admin)
        else -> emptyList()
    }

    @QueryMapping
    @IsOwnerOfLocation
    fun location(@Argument locationId: LocationId) =
        locationService.getById(locationId)

    @SchemaMapping
    fun events(location: Location) =
        location.events.sortedBy { it.year }

    @SchemaMapping
    fun latestEvent(location: Location) =
        location
            .events
            .maxByOrNull { it.year }

    @RequireAdmin
    @MutationMapping
    fun createLocation(
        @Valid @Argument input: CreateLocationInput,
        adminAuthentication: AdminAuthentication,
    ) = locationService.createLocation(input.toEntity(adminAuthentication.admin))

    @IsOwnerOfLocation
    @MutationMapping
    fun addOwner(
        @Argument locationId: LocationId,
        @Argument adminEmail: String,
        adminAuthentication: AdminAuthentication,
    ) = locationService.addOwner(locationId, adminEmail, adminAuthentication)

    @IsOwnerOfLocation
    @MutationMapping
    fun removeOwner(
        @Argument locationId: LocationId,
        @Argument adminId: AdminId,
        adminAuthentication: AdminAuthentication,
    ) = locationService.removeOwner(locationId, adminId, adminAuthentication)

}
