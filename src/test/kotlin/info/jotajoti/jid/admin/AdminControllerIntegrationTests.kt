package info.jotajoti.jid.admin

import info.jotajoti.jid.test.GraphQLIntegrationTests
import org.junit.jupiter.api.Test

class AdminControllerIntegrationTests : GraphQLIntegrationTests() {

    @Test
    fun `should create admin user`() {

        executeQuery(
            """
            mutation CreateAdmin {
                createAdmin(
                    input: { name: "Test 1", email: "test1@example.com", password: "test123" }
                ) {
                    id
                }
            }
        """.trimIndent()
        )
            .path("createAdmin.id")
            .entity(String::class.java)
    }
}
