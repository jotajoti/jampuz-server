package info.jotajoti.jampuz.event

import info.jotajoti.jampuz.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*

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

    @Nested
    inner class UpdateEventTests {

        @ParameterizedTest
        @CsvSource(
            textBlock = """
            code,   year, active
            NULL,   NULL, NULL
            5DK10B, NULL, NULL
            NULL,   2010, NULL
            NULL,   NULL, false
            5DK10B, 2010, NULL
            5DK10B, NULL, false
            NULL,   2010, false
            5DK10B, 2010, false""",
            useHeadersInDisplayName = true, nullValues = ["NULL"]
        )
        fun `should update event`(code: String?, year: Int?, active: Boolean?) {

            val variables = mutableListOf<Pair<String, Any>>()
            variables.add("eventId" to testEvent.id!!)
            variables.addIfValueNotNull("code" to code)
            variables.addIfValueNotNull("year" to year)
            variables.addIfValueNotNull("active" to active)

            executeAdminQueryName(
                graphQlDocument = "updateEvent",
                adminId = testAdmins[0].id!!,
                *variables.toTypedArray(),
            )
                .errors().verify()
                .path("updateEvent.code.value").isEqualTo(code ?: testEvent.code.code)
                .path("updateEvent.year").isEqualTo(year ?: testEvent.year)
                .path("updateEvent.active").isEqualTo(active ?: testEvent.active)
        }

        @Test
        fun `should not be allowed to update event if changing to existing code or year`() {

        }

        private fun MutableList<Pair<String, Any>>.addIfValueNotNull(entry: Pair<String, Any?>) {
            val key = entry.first
            val value = entry.second
            if (value != null) {
                add(key to value)
            }
        }
    }
}