package info.jotajoti.jid.security

import info.jotajoti.jid.security.SubjectType.ADMIN
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class SecurityController(
    private val securityService: SecurityService,
    private val jwtService: JwtService,
) {

    @MutationMapping
    fun authenticateAdmin(@Argument email: String, @Argument password: String) =
        securityService
            .authenticateAdmin(email, password)
            ?.let {
                jwtService.createToken(Subject(ADMIN, it.id!!))
            }
}