package info.jotajoti.jid.security

import jakarta.servlet.*
import jakarta.servlet.http.*
import org.springframework.security.core.context.*
import org.springframework.stereotype.*
import org.springframework.web.filter.*

@Component
class JwtRequestFilter(
    private val jwtService: JwtService,
    private val securityService: SecurityService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getToken()
        val subject = jwtService.validateToken(token)
        if (subject != null) {
            SecurityContextHolder.getContext().authentication = securityService.getAuthentication(subject)
        }

        filterChain.doFilter(request, response)
    }

    private fun HttpServletRequest.getToken() =
        getHeader("Authorization")
            ?.takeIf { it != "" }
            ?.takeIf { it.startsWith("Bearer ") }
            ?.substring(7)

}
