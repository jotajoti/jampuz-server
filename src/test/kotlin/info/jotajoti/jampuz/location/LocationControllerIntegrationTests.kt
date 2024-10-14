package info.jotajoti.jampuz.location

import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.test.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*

class LocationControllerIntegrationTests : GraphQLIntegrationTests() {

    @Nested
    inner class CreateLocationTests {

        @Test
        fun `should create location`() {

            executeAdminQuery(
                """
                    mutation CreateLocation {
                        createLocation(input: { name: "Location 2" }) {
                            id
                            name
                        }
                    }
                """.trimIndent()
            )
                .path("createLocation.id").hasValue()
                .path("createLocation.name").isEqualTo("Location 2")
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