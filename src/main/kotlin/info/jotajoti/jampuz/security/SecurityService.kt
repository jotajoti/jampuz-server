package info.jotajoti.jampuz.security

import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.event.*
import info.jotajoti.jampuz.participant.*
import info.jotajoti.jampuz.security.SubjectType.*
import org.springframework.beans.factory.annotation.*
import org.springframework.data.repository.*
import org.springframework.security.crypto.bcrypt.BCrypt.*
import org.springframework.stereotype.*

@Service
class SecurityService(
    private val adminRepository: AdminRepository,
    private val participantRepository: ParticipantRepository,
    @Value("\${password.salt}")
    private val salt: String,
) {

    fun authenticateAdmin(email: String, password: String) =
        adminRepository
            .findFirstByEmailAndPasswordHash(email, hashPassword(password))

    fun authenticateParticipant(eventId: EventId, name: String, pinCode: PinCode) =
        participantRepository
            .findFirstByEventIdAndNameAndPinCode(eventId, name, pinCode.value)

    fun getAuthentication(subject: Subject) = when (subject.type) {
        ADMIN -> subject.getAdminAuthentication()
        PARTICIPANT -> subject.getParticipantAuthentication()
    }

    fun hashPassword(password: String): String = hashpw(password, salt)

    private fun Subject.getAdminAuthentication() = adminRepository
        .findByIdOrNull(id)
        ?.let {
            AdminAuthentication(it)
        }

    private fun Subject.getParticipantAuthentication() = participantRepository
        .findByIdOrNull(id)
        ?.let {
            ParticipantAuthentication(it)
        }
}