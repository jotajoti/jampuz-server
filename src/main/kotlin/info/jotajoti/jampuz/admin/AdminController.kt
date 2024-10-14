package info.jotajoti.jampuz.admin

import info.jotajoti.jampuz.security.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.stereotype.*

@Controller
class AdminController(
    private val adminRepository: AdminRepository,
    private val securityService: SecurityService,
) {

    @MutationMapping
    fun createAdmin(@Argument input: CreateAdminInput): Admin {

        val admin = Admin(
            name = input.name,
            email = input.email,
            passwordHash = securityService.hashPassword(input.password),
        )

        return adminRepository.save(admin)
    }
}
