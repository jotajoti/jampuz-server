package info.jotajoti.jid.security

import info.jotajoti.jid.admin.AdminRepository
import info.jotajoti.jid.participant.ParticipantRepository
import info.jotajoti.jid.security.SubjectType.ADMIN
import info.jotajoti.jid.security.SubjectType.PARTICIPANT
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCrypt.hashpw
import org.springframework.stereotype.Service

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