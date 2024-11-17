package info.jotajoti.jampuz.location

import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.security.*
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.data.repository.*

class LocationServiceTests {

    private val mockLocationRepository = mockk<LocationRepository>()
    private val mockAdminRepository = mockk<AdminRepository>()
    private val locationService = LocationService(mockLocationRepository, mockAdminRepository)

    @AfterEach
    fun verifyAndClearMocks() {
        confirmVerified(mockAdminRepository, mockLocationRepository)
        checkUnnecessaryStub(mockAdminRepository, mockLocationRepository)
        clearMocks(mockAdminRepository, mockLocationRepository)
    }

    @Nested
    inner class AddOwnerTests {

        private val testAdmin = admin()
        private val testAuthentication = adminAuthentication()

        @Test
        fun `should add owner to location`() {

            val testLocation = location(owners = listOf(testAuthentication.admin))

            every { mockAdminRepository.findOneByEmail(testAdmin.email) } returns testAdmin
            every { mockLocationRepository.findByIdOrNull(testLocation.id!!) } returns testLocation
            every { mockLocationRepository.save(testLocation) } returns testLocation

            assertThat(testLocation.owners).containsOnly(testAuthentication.admin)

            locationService.addOwner(testLocation.id!!, testAdmin.email, testAuthentication)

            assertThat(testLocation.owners).containsOnly(testAuthentication.admin, testAdmin)

            verify { mockAdminRepository.findOneByEmail(testAdmin.email) }
            verify { mockLocationRepository.findByIdOrNull(testLocation.id!!) }
            verify { mockLocationRepository.save(testLocation) }
        }

        @Test
        fun `should fail if admin not found by email`() {

            every { mockAdminRepository.findOneByEmail(testAdmin.email) } returns null

            assertThrows<AdminNotFoundException> {
                locationService.addOwner(LocationId.randomUUID(), testAdmin.email, testAuthentication)
            }

            verify { mockAdminRepository.findOneByEmail(testAdmin.email) }
        }

        @Test
        fun `should fail if trying to add self to location`() {

            every { mockAdminRepository.findOneByEmail(testAuthentication.admin.email) } returns testAuthentication.admin

            assertThrows<CannotAddSelfToLocationException> {
                locationService.addOwner(LocationId.randomUUID(), testAuthentication.admin.email, testAuthentication)
            }

            verify { mockAdminRepository.findOneByEmail(testAuthentication.admin.email) }
        }

        @Test
        fun `should fail if location not found`() {

            val locationId = LocationId.randomUUID()

            every { mockAdminRepository.findOneByEmail(testAdmin.email) } returns testAdmin
            every { mockLocationRepository.findByIdOrNull(locationId) } returns null

            assertThrows<LocationNotFoundException> {
                locationService.addOwner(locationId, testAdmin.email, testAuthentication)
            }

            verify { mockAdminRepository.findOneByEmail(testAdmin.email) }
            verify { mockLocationRepository.findByIdOrNull(locationId) }
        }
    }
}