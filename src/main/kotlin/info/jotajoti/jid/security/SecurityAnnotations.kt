package info.jotajoti.jid.security

import org.intellij.lang.annotations.Language
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Secured("ADMIN")
@Target(CLASS, FUNCTION)
annotation class RequireAdmin

@Secured("PARTICIPANT", "ADMIN")
@Target(CLASS, FUNCTION)
annotation class RequireParticipant

@Language("SpEL")
@PreAuthorize("#locationId != null and @locationSecurityService.isAuthenticationOwnerOfLocation(#locationId, authentication)")
@RequireAdmin
@Target(CLASS, FUNCTION)
annotation class IsOwnerOfLocation
