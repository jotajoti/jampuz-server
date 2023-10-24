package info.jotajoti.jid.security

import org.springframework.security.access.annotation.Secured
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Secured("ADMIN")
@Target(CLASS, FUNCTION)
annotation class RequireAdmin

@Secured("PARTICIPANT")
@Target(CLASS, FUNCTION)
annotation class RequireParticipant
