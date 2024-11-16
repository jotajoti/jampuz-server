package info.jotajoti.jampuz.security

import info.jotajoti.jampuz.test.*
import org.junit.jupiter.api.*

class SecurityControllerIntegrationTests : GraphQLIntegrationTests() {

    @Nested
    inner class AuthenticatedParticipantTests {

        @Test
        fun `should return null if not authenticated`() {

            executeAnonymousQuery(
                """
                    query GetAuthenticatedParticipant {
                        authenticatedParticipant {
                            id
                        }
                    }
                """.trimIndent()
            )
                .path("authenticatedParticipant").valueIsNull()
        }

        @Test
        fun `should return value if authenticated as participant`() {

            executeParticipantQuery(
                """
                    query GetAuthenticatedParticipant {
                        authenticatedParticipant {
                            id
                        }
                    }
                """.trimIndent()
            )
                .path("authenticatedParticipant").hasValue()
                .path("authenticatedParticipant.id").isEqualTo(testParticipants[0].id)
        }

        @Test
        fun `should return null if authenticated as admin not participating`() {

            executeAdminQueryName(
                "getAuthenticatedParticipant",
                testAdmins[0].id!!,
                "eventJidCode" to testEvent.code.code,
                "year" to testEvent.year,
            )
                .path("authenticatedParticipant").valueIsNull()
        }

        @Test
        fun `should return value if authenticated as admin participating`() {

            // Admin joins event by registered JID code
            executeAdminQueryName(
                "registerJidCode",
                testAdmins[0].id!!,
                "code" to "5DK90A",
                "eventId" to testEvent.id!!,
            )

            executeAdminQueryName(
                "getAuthenticatedParticipant",
                testAdmins[0].id!!,
                "eventJidCode" to testEvent.code.code,
                "year" to testEvent.year,
            )
                .path("authenticatedParticipant").hasValue()
                .path("authenticatedParticipant.admin.id").isEqualTo(testAdmins[0].id)
        }
    }
}