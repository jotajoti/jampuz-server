package info.jotajoti.jid.jidcode

import info.jotajoti.jid.participant.Participant
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

typealias FoundJidCodeId = UUID

@Entity
data class FoundJidCode(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val id: FoundJidCodeId? = null,

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    var participant: Participant,

    @Embedded
    var code: JidCode,
)
