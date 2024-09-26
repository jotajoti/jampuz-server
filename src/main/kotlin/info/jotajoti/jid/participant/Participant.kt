package info.jotajoti.jid.participant

import info.jotajoti.jid.admin.*
import info.jotajoti.jid.event.*
import info.jotajoti.jid.jidcode.*
import info.jotajoti.jid.security.*
import jakarta.persistence.*
import jakarta.persistence.GenerationType.*
import org.hibernate.annotations.*
import org.hibernate.type.*

@Entity
data class Participant(

    @Id
    @GeneratedValue(strategy = UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val id: ParticipantId? = null,

    var name: String,

    var pinCode: PinCode,

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = true)
    var admin: Admin? = null,

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    var event: Event,

    @OneToMany(mappedBy = "participant")
    var foundJidCodes: List<FoundJidCode> = emptyList(),
)
