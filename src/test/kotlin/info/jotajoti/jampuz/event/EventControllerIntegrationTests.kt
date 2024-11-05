package info.jotajoti.jampuz.event

import info.jotajoti.jampuz.test.*
import org.junit.jupiter.api.*

class EventControllerIntegrationTests : GraphQLIntegrationTests() {

    @Nested
    inner class GetEventByCodeTests {

        @Test
        fun `should return newest event by code`() {

            val code = testEvent.code.code

            executeAdminQuery(
                """
                    query GetEventByCode {
                        event(code: "$code") {
                            id
                            code {
                                value
                                country
                                region
                            }
                        }                        
                    }
                """.trimIndent()
            )
                .path("event.id").isEqualTo(testEvent.id)
                .path("event.code.value").isEqualTo(testEvent.code.code)
                .path("event.code.country").isEqualTo(testEvent.code.country)
                .path("event.code.region").isEqualTo(testEvent.code.region)
        }
    }

    @Nested
    inner class CreateEventTests {

        @Test
        fun `should create event`() {

            val locationId = testEvent.location.id!!
            val jidCode = testEvent.code.code
            val year = testEvent.year + 1

            executeAdminQueryName(
                graphQlDocument = "createEvent",
                adminId = testAdmins[0].id!!,
                "locationId" to locationId,
                "code" to jidCode,
                "year" to year
            )
                .path("createEvent.id").hasValue()
        }

        @Test
        fun `should not be allowed to create event with same code and year`() {

            val locationId = testEvent.location.id!!
            val jidCode = testEvent.code.code
            val year = testEvent.year

            executeAdminQueryName(
                "createEvent",
                testAdmins[0].id!!,
                "locationId" to locationId,
                "code" to jidCode,
                "year" to year
            )
                .errors()
                .expect {
                    it.message == "An event with the same code and year is already created"
                }
        }
    }
}