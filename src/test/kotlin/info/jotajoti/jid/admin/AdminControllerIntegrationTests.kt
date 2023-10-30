package info.jotajoti.jid.admin

import info.jotajoti.jid.test.GraphQLIntegrationTests
import org.junit.jupiter.api.Test

class AdminControllerIntegrationTests : GraphQLIntegrationTests() {

    @Test
    fun `should create admin user`() {

        executeAnonymousQuery(
            """
            mutation CreateAdmin {
                createAdmin(
                    input: { name: "Admin 1", email: "admin1@example.com", password: "admin123" }
                ) {
                    id
                }
            }
        """.trimIndent()
        )
            .path("createAdmin.id").hasValue()
    }
}
