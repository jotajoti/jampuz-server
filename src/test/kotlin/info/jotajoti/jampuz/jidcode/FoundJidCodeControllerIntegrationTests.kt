package info.jotajoti.jampuz.jidcode

import info.jotajoti.jampuz.*
import info.jotajoti.jampuz.test.*
import org.junit.jupiter.api.*

class FoundJidCodeControllerIntegrationTests : GraphQLIntegrationTests() {

    @Nested
    inner class RegisterFoundJidCodeTests {

        @Test
        fun `should register jid code for participant`() {

            val participant = testParticipants[0]

            val jidCode = JidCode.random().code

            executeParticipantQuery(
                """
                    mutation RegisterFoundJidCode {
                        registerFoundJidCode(input: {code: "$jidCode", eventId: "${testEvent.id!!}"}) {
                            id
                            participant {
                                id                            
                            }
                        }
                    }
                """.trimIndent(),
                participant.id!!
            )
                .path("registerFoundJidCode.id").hasValue()
                .path("registerFoundJidCode.participant.id").isEqualTo(participant.id)
        }

        @Test
        fun `should register jid code for admin`() {

            val admin = testAdmins[0]

            val jidCode = JidCode.random().code

            executeAdminQuery(
                """
                    mutation RegisterFoundJidCode {
                        registerFoundJidCode(input: {code: "$jidCode", eventId: "${testEvent.id!!}"}) {
                            id
                            participant {
                                admin {
                                    id                                
                                }                            
                            }
                        }
                    }
                """.trimIndent(),
                admin.id!!
            )
                .path("registerFoundJidCode.id").hasValue()
                .path("registerFoundJidCode.participant.admin.id").isEqualTo(admin.id)
        }

        @Test
        fun `should not be allowed to register jid code for anonymous`() {

            val jidCode = JidCode.random().code

            executeAnonymousQuery(
                """
                    mutation RegisterFoundJidCode {
                        registerFoundJidCode(input: {code: "$jidCode", eventId: "${testEvent.id!!}"}) {
                            id
                            participant {
                                id                            
                            }
                        }
                    }
                """.trimIndent()
            )
                .errors()
                .expect {
                    it.message == "Unauthorized"
                }
        }

        @Test
        fun `should not be allowed to register jid code for admin on non-owned location`() {

            val locationNonOwner = testAdmins[4]

            val jidCode = JidCode.random().code

            executeAdminQuery(
                """
                    mutation RegisterFoundJidCode {
                        registerFoundJidCode(input: {code: "$jidCode", eventId: "${testEvent.id!!}"}) {
                            id
                            participant {
                                admin {
                                    id                                
                                }                            
                            }
                        }
                    }
                """.trimIndent(),
                locationNonOwner.id!!
            )
                .errors()
                .expect {
                    it.message == "Forbidden"
                }
        }
    }
}
