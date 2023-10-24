package info.jotajoti.jid.viewer

import info.jotajoti.jid.security.AdminAuthentication
import info.jotajoti.jid.security.ParticipantAuthentication
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller

@Controller
class ViewerController {

    @QueryMapping
    fun viewer(authentication: Authentication) = when (authentication) {
        is AdminAuthentication -> Viewer(admin = authentication.admin, participant = null)
        is ParticipantAuthentication -> Viewer(admin = null, participant = authentication.participant)
        else -> null
    }
}