package info.jotajoti.jampuz.admin

import org.springframework.data.jpa.repository.*

interface AdminRepository : JpaRepository<Admin, AdminId> {

    fun existsByEmail(email: String): Boolean

    fun findOneByEmail(email: String): Admin?

    fun findFirstByEmailAndPasswordHash(email: String, passwordHash: String): Admin?
}
