package info.jotajoti.jampuz.admin

import info.jotajoti.jampuz.test.*
import org.junit.jupiter.api.*

class AdminControllerIntegrationTests : GraphQLIntegrationTests() {

    @Test
    fun `should create admin user`() {

        executeAnonymousQuery(
            """
            mutation CreateAdmin {
                createAdmin(
                    input: { name: "Admin 10", email: "admin10@example.com", password: "admin123" }
                ) {
                    id
                }
            }
        """.trimIndent()
        )
            .path("createAdmin.id").hasValue()
    }
}
