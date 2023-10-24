package info.jotajoti.jid.participant

import org.springframework.data.jpa.repository.JpaRepository

interface ParticipantRepository : JpaRepository<Participant, ParticipantId>
