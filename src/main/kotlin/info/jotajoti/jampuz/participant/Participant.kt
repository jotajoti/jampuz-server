package info.jotajoti.jampuz.participant

import info.jotajoti.jampuz.admin.*
import info.jotajoti.jampuz.event.*
import info.jotajoti.jampuz.jidcode.*
import info.jotajoti.jampuz.security.*
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
