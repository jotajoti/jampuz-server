package info.jotajoti.jid.admin

import org.springframework.data.jpa.repository.*

interface AdminRepository : JpaRepository<Admin, AdminId> {

    fun findFirstByEmailAndPasswordHash(email: String, passwordHash: String): Admin?
}
