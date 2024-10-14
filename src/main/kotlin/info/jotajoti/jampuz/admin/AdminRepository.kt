package info.jotajoti.jampuz.admin

import org.springframework.data.jpa.repository.*

interface AdminRepository : JpaRepository<Admin, AdminId> {

    fun findFirstByEmailAndPasswordHash(email: String, passwordHash: String): Admin?
}
