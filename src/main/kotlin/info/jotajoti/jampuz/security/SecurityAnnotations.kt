package info.jotajoti.jampuz.security

import org.intellij.lang.annotations.*
import org.springframework.security.access.annotation.*
import org.springframework.security.access.prepost.*
import kotlin.annotation.AnnotationTarget.*

@Secured("ADMIN")
@Target(CLASS, FUNCTION)
annotation class RequireAdmin

@Secured("PARTICIPANT", "ADMIN")
@Target(CLASS, FUNCTION)
annotation class RequireParticipant

@Language("SpEL")
@PreAuthorize("#input.eventId != null and @eventSecurityService.isParticipatingInEvent(#input.eventId, authentication)")
@RequireParticipant
@Target(CLASS, FUNCTION)
annotation class IsParticipatingInEvent

@Language("SpEL")
@PreAuthorize("""
    (#locationId != null and @locationSecurityService.isAuthenticationOwnerOfLocation(#locationId, authentication))
    or
    (#input != null and #input.locationId != null and @locationSecurityService.isAuthenticationOwnerOfLocation(#input.locationId, authentication))
""")
@RequireAdmin
@Target(CLASS, FUNCTION)
annotation class IsOwnerOfLocation
