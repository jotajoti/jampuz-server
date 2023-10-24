package info.jotajoti.jid.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

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
