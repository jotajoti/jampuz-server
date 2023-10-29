package info.jotajoti.jid.location

import info.jotajoti.jid.test.GraphQLIntegrationTests
import org.junit.jupiter.api.Test

class LocationControllerIntegrationTests : GraphQLIntegrationTests() {

    @Test
    fun `should create location`() {

        executeAdminQuery(
            """
            mutation CreateLocation {
                createLocation(input: { name: "Location 1", code: "5DK37K", year: 2023 }) {
                    id
                }
            }
        """.trimIndent()
        )
            .path("createLocation.id")
            .entity(String::class.java)
    }
}