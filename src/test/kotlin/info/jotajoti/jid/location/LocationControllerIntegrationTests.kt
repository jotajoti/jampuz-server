package info.jotajoti.jid.location

import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.admin.AdminId
import info.jotajoti.jid.jidcode.JidCode
import info.jotajoti.jid.random
import info.jotajoti.jid.test.GraphQLIntegrationTests
import info.jotajoti.jid.test.hasSize
import info.jotajoti.jid.test.isEqualTo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LocationControllerIntegrationTests : GraphQLIntegrationTests() {

    @Nested
    inner class GetLocationByIdTests {

        @Test
        fun `should return location by id`() {

            val locationId = testLocation.id!!

            executeAdminQuery(
                """
                    query GetLocationById {
                        viewer {
                            locationById(locationId: "$locationId") {
                                id
                                name
                                code {
                                    value
                                    country
                                    region
                                }
                            }                        
                        }
                    }
                """.trimIndent()
            )
                .path("viewer.locationById.id").isEqualTo(locationId)
                .path("viewer.locationById.name").isEqualTo(testLocation.name)
                .path("viewer.locationById.code.value").isEqualTo(testLocation.code.code)
                .path("viewer.locationById.code.country").isEqualTo(testLocation.code.country)
                .path("viewer.locationById.code.region").isEqualTo(testLocation.code.region)
        }
    }

    @Nested
    inner class CreateLocationTests {

        @Test
        fun `should create location`() {

            val jidCode = JidCode.random().code

            executeAdminQuery(
                """
                    mutation CreateLocation {
                        createLocation(input: { name: "Location 2", code: "$jidCode", year: 2023 }) {
                            id
                        }
                    }
                """.trimIndent()
            )
                .path("createLocation.id").hasValue()
        }

        @Test
        fun `should not be allowed to create location with same code and year`() {

            val jidCode = testLocation.code.code
            val year = testLocation.year

            executeAdminQueryName(
                "createLocation",
                testAdmins[0].id!!,
                "name" to "Location 1",
                "code" to jidCode,
                "year" to year
            )
                .errors()
                .expect {
                    it.message == "A location with the same code and year is already created"
                }
        }
    }

    @Nested
    inner class AddOwnerTests {

        @Test
        fun `should add owner to location`() {
            val adminId = testAdmins[2].id
            val locationId = testLocation.id

            executeAdminQuery(
                """
                    mutation AddOwnerToLocation {
                        addOwner(adminId: "$adminId", locationId: "$locationId") {
                            owners {
                                id
                            }
                        }
                    }
                """.trimIndent()
            )
                .path("addOwner.owners[*].id").hasSize<AdminId>(3)
        }

        @Test
        fun `should not be allowed to add self to not-owned location`() {
            val locationNonOwner = testAdmins[2]

            val adminId = locationNonOwner.id!!
            val locationId = testLocation.id!!

            executeAdminQuery(
                """
                    mutation AddOwnerToLocation {
                        addOwner(adminId: "$adminId", locationId: "$locationId") {
                            owners {
                                id
                            }
                        }
                    }
                """.trimIndent(),
                adminId
            )
                .errors()
                .expect {
                    it.message == "Forbidden"
                }
        }
    }

    @Nested
    inner class RemoveOwnerTests {

        @Test
        fun `should remove owner from location`() {
            val adminId = testAdmins[1].id!!
            val locationId = testLocation.id!!
            executeAdminQuery(
                """
                    mutation RemoveOwnerFromLocation {
                        removeOwner(adminId: "$adminId", locationId: "$locationId") {
                            owners {
                                id
                            }
                        }
                    }
                """.trimIndent()
            )
                .path("removeOwner.owners[*].id").hasSize<AdminId>(1)
        }

        @Test
        fun `should not be allowed to remove self from location`() {
            val self = testAdmins[0]
            val adminId = self.id!!
            val locationId = testLocation.id!!

            executeAdminQuery(
                """
                    mutation RemoveOwnerFromLocation {
                        removeOwner(adminId: "$adminId", locationId: "$locationId") {
                            owners {
                                id
                            }
                        }
                    }
                """.trimIndent()
            )
                .errors()
                .expect {
                    it.message == "Cannot remove yourself from a location"
                }

            locationId.verifyHasOwner(self)
        }

        @Test
        fun `should not be allowed to remove owner from not-owned location`() {
            val locationOwner = testAdmins[0]
            val adminId = locationOwner.id!!
            val locationId = testLocation.id!!

            val locationNonOwner = testAdmins[4]

            executeAdminQuery(
                """
                    mutation RemoveOwnerFromLocation {
                        removeOwner(adminId: "$adminId", locationId: "$locationId") {
                            owners {
                                id
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

            locationId.verifyHasOwner(locationOwner)
        }
    }

    private fun LocationId.verifyHasOwner(admin: Admin) =
        assertThat(locationRepository.findByIdAndOwner(this, admin)).isNotNull
}