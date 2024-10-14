package info.jotajoti.jampuz.security

import info.jotajoti.jampuz.admin.*
import org.springframework.security.authentication.*
import org.springframework.security.core.authority.*

val ADMIN_AUTHORITY = SimpleGrantedAuthority("ADMIN")

class AdminAuthentication(val admin: Admin) :
    AbstractAuthenticationToken(listOf(ADMIN_AUTHORITY, PARTICIPANT_AUTHORITY)) {

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
