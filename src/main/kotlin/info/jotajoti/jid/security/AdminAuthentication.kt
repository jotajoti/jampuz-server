package info.jotajoti.jid.security

import info.jotajoti.jid.admin.Admin
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

val ADMIN_AUTHORITY = SimpleGrantedAuthority("ADMIN")

class AdminAuthentication(val admin: Admin) : AbstractAuthenticationToken(listOf(ADMIN_AUTHORITY, PARTICIPANT_AUTHORITY)) {

    private val principal = AdminPrincipal(admin)

    override fun getCredentials(): Any {
        TODO("Not yet implemented")
    }

    override fun getPrincipal(): Any = principal

    override fun getName() = admin.name
}

private class AdminPrincipal(private val admin: Admin) {
    override fun toString(): String {
        return admin.name
    }
}
