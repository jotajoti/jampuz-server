package info.jotajoti.jid.location

import info.jotajoti.jid.jidcode.JidCode
import info.jotajoti.jid.security.AdminAuthentication
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
    private val locationRepository: LocationRepository,
) {

    @SchemaMapping
    fun locations(viewer: Viewer): List<Location> = when {
        viewer.admin != null -> locationRepository.findAllByOwner(viewer.admin)
        viewer.participant != null -> locationRepository.findByParticipant(viewer.participant).toList()
        else -> emptyList()
    }

    @SchemaMapping
    fun locationById(viewer: Viewer, @Argument locationId: LocationId) = when {
        viewer.admin != null -> locationRepository.findByIdAndOwner(locationId, viewer.admin)
        viewer.participant != null -> locationRepository.findByIdAndParticipant(locationId, viewer.participant)
        else -> null
    }

    @SchemaMapping
    fun locationByCode(viewer: Viewer, @Argument code: JidCode) = when {
        viewer.admin != null -> locationRepository.findByCodeAndOwner(code, viewer.admin)
        viewer.participant != null -> locationRepository.findByCodeAndParticipant(code, viewer.participant)
        else -> null
    }

    @RequireAdmin
    @MutationMapping
    fun createLocation(
        @Valid @Argument input: CreateLocationInput,
        adminAuthentication: AdminAuthentication
    ): Location {

        val location = Location(
            name = input.name,
            code = JidCode(input.code),
            year = input.year,
            owners = listOf(adminAuthentication.admin),
        ).apply {
            createdBy = adminAuthentication.admin
            lastModifiedBy = adminAuthentication.admin
        }

        return locationRepository.save(location)
    }

}