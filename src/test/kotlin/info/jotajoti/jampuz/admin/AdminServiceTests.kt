package info.jotajoti.jampuz.admin

import info.jotajoti.jampuz.security.*
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*

class AdminServiceTests {

    private val mockAdminRepository = mockk<AdminRepository>()
    private val mockSecurityService = mockk<SecurityService>()
    private val adminService = AdminService(mockAdminRepository, mockSecurityService)

    @AfterEach
    fun verifyAndClearMocks() {
        confirmVerified(mockAdminRepository, mockSecurityService)
        checkUnnecessaryStub(mockAdminRepository, mockSecurityService)
        clearMocks(mockAdminRepository, mockSecurityService)
    }

    @Nested
    inner class CreateAdminTests {

        private val testName = "Admin Name"
        private val testEmail = "admin@example.com"
        private val testPassword = "admin123"
        private val testPasswordHash = "hash-$testPassword"

        @Test
        fun `should create a new admin`() {
            every { mockAdminRepository.existsByEmail(testEmail) } returns false
            every { mockAdminRepository.save(any()) } answers { firstArg() }
            every { mockSecurityService.hashPassword(testPassword) } returns testPasswordHash

            assertDoesNotThrow {
                val result = adminService.createAdmin(testName, testEmail, testPassword)
                with(result) {
                    assertThat(name).isEqualTo(testName)
                    assertThat(email).isEqualTo(testEmail)
                    assertThat(passwordHash).isEqualTo(testPasswordHash)
                }
            }

            verify { mockAdminRepository.existsByEmail(testEmail) }
            verify { mockAdminRepository.save(any()) }
            verify { mockSecurityService.hashPassword(testPassword) }
        }

        @Test
        fun `should fail if email already exists`() {
            every { mockAdminRepository.existsByEmail(testEmail) } returns true

            assertThrows<EmailNotAvailableException> {
                adminService.createAdmin(testName, testEmail, testPassword)
            }

            verify { mockAdminRepository.existsByEmail(testEmail) }
        }
    }
}