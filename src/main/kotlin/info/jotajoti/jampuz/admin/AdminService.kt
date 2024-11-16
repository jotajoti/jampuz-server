package info.jotajoti.jampuz.admin

import info.jotajoti.jampuz.exceptions.*
import info.jotajoti.jampuz.exceptions.ErrorCode.*
import info.jotajoti.jampuz.security.*
import org.springframework.graphql.execution.ErrorType.*
import org.springframework.stereotype.*

@Service
class AdminService(
    private val adminRepository: AdminRepository,
    private val securityService: SecurityService,
) {

    fun createAdmin(name: String, email: String, password: String): Admin {

        if (adminRepository.existsByEmail(email)) {
            throw EmailNotAvailableException()
        }

        return adminRepository.save(
            Admin(
                name = name,
                email = email,
                passwordHash = securityService.hashPassword(password),
            )
        )
    }
}

class EmailNotAvailableException() : ErrorCodeException("Email not available", EMAIL_NOT_AVAILABLE, BAD_REQUEST)
