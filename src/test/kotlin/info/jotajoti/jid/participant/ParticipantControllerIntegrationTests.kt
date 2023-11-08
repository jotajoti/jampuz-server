package info.jotajoti.jid.participant

import info.jotajoti.jid.test.*
import org.junit.jupiter.api.*

class ParticipantControllerIntegrationTests : GraphQLIntegrationTests() {

    @Nested
    inner class CreateParticipantTests {

        @Test
        fun `should create participant`() {

            val locationId = testLocation.id!!

            executeAnonymousQuery(
                """
            mutation CreateParticipant {
                createParticipant(
                    input: { name: "Participant XYZ", locationId: "$locationId" }
                ) {
                    id
                    name
                }
            }
        """.trimIndent()
            )
                .path("createParticipant.id").hasValue()
                .path("createParticipant.name").isEqualTo("Participant XYZ")
        }

        @Test
        fun `should not be allowed to create participant with duplicate name in location`() {

            val locationId = testLocation.id!!

            executeAnonymousQuery(
                """
            mutation CreateParticipant {
                createParticipant(
                    input: { name: "Participant 1", locationId: "$locationId" }
                ) {
                    id
                }
            }
        """.trimIndent()
            )
                .errors()
                .expect {
                    it.message == "A participant with the same name already exists in the location"
                }
        }
    }
}