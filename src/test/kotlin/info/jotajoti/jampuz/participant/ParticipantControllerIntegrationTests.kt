package info.jotajoti.jampuz.participant

import info.jotajoti.jampuz.test.*
import org.junit.jupiter.api.*

class ParticipantControllerIntegrationTests : GraphQLIntegrationTests() {

    @Nested
    inner class CreateParticipantTests {

        @Test
        fun `should create participant`() {

            val eventId = testEvent.id!!

            executeAnonymousQuery(
                """
            mutation CreateParticipant {
                createParticipant(
                    input: { name: "Participant XYZ", eventId: "$eventId" }
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

            val eventId = testEvent.id!!

            executeAnonymousQuery(
                """
            mutation CreateParticipant {
                createParticipant(
                    input: { name: "Participant 1", eventId: "$eventId" }
                ) {
                    id
                }
            }
        """.trimIndent()
            )
                .errors()
                .expect {
                    it.message == "A participant with the same name already exists in the event"
                }
        }
    }
}