package info.jotajoti.jid.location

import info.jotajoti.jid.admin.AdminId
import info.jotajoti.jid.jidcode.JidCode
import info.jotajoti.jid.security.AdminAuthentication
import info.jotajoti.jid.security.IsOwnerOfLocation
import info.jotajoti.jid.security.ParticipantAuthentication
import info.jotajoti.jid.security.RequireAdmin
import info.jotajoti.jid.util.toList
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated

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
        if (year != null) {
            locationService.findByCodeAndYear(code, year)
        } else {
            locationService.findLatestByCode(code)
        }

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
