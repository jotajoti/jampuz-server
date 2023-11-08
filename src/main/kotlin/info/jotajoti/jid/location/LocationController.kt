package info.jotajoti.jid.location

import info.jotajoti.jid.admin.*
import info.jotajoti.jid.jidcode.*
import info.jotajoti.jid.security.*
import info.jotajoti.jid.util.*
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

    @SchemaMapping
    fun code(location: Location) = location.code

    @QueryMapping
    fun locations(authentication: Authentication) = when (authentication) {
        is AdminAuthentication -> locationService.findAllByOwner(authentication.admin)
        is ParticipantAuthentication -> locationService.findByParticipant(authentication.participant).toList()
        else -> emptyList()
    }

    @QueryMapping
    fun locationByCode(@Argument code: JidCode, @Argument year: Int?) =
        locationService.findByCode(code, year)

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
        @Argument adminId: AdminId,
        adminAuthentication: AdminAuthentication,
    ) = locationService.addOwner(locationId, adminId, adminAuthentication)

    @IsOwnerOfLocation
    @MutationMapping
    fun removeOwner(
        @Argument locationId: LocationId,
        @Argument adminId: AdminId,
        adminAuthentication: AdminAuthentication,
    ) = locationService.removeOwner(locationId, adminId, adminAuthentication)

}
