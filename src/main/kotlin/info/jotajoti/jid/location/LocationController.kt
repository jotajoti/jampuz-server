package info.jotajoti.jid.location

import info.jotajoti.jid.admin.AdminId
import info.jotajoti.jid.jidcode.JidCode
import info.jotajoti.jid.security.AdminAuthentication
import info.jotajoti.jid.security.IsOwnerOfLocation
import info.jotajoti.jid.security.RequireAdmin
import info.jotajoti.jid.util.toList
import info.jotajoti.jid.viewer.Viewer
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated

@Controller
@Validated
class LocationController(
    private val locationService: LocationService,
) {

    @SchemaMapping
    fun locations(viewer: Viewer): List<Location> = when {
        viewer.admin != null -> locationService.findAllByOwner(viewer.admin)
        viewer.participant != null -> locationService.findByParticipant(viewer.participant).toList()
        else -> emptyList()
    }

    @SchemaMapping
    fun locationById(viewer: Viewer, @Argument locationId: LocationId) = when {
        viewer.admin != null -> locationService.findByIdAndOwner(locationId, viewer.admin)
        viewer.participant != null -> locationService.findByIdAndParticipant(locationId, viewer.participant)
        else -> null
    }

    @SchemaMapping
    fun locationByCode(viewer: Viewer, @Argument code: JidCode) = when {
        viewer.admin != null -> locationService.findByCodeAndOwner(code, viewer.admin)
        viewer.participant != null -> locationService.findByCodeAndParticipant(code, viewer.participant)
        else -> null
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
