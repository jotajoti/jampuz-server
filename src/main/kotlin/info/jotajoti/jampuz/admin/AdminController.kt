package info.jotajoti.jampuz.admin

import org.springframework.graphql.data.method.annotation.*
import org.springframework.stereotype.*

@Controller
class AdminController(
    private val adminService: AdminService,
) {

    @MutationMapping
    fun createAdmin(@Argument input: CreateAdminInput) =
        adminService.createAdmin(input.name, input.email, input.password)
}
