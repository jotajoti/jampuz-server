package info.jotajoti.jid.event

import info.jotajoti.jid.jidcode.*
import info.jotajoti.jid.location.*
import info.jotajoti.jid.participant.*
import jakarta.persistence.*
import jakarta.persistence.GenerationType.*
import org.hibernate.annotations.*
import org.hibernate.type.*

@Entity
data class Event(

    @Id
    @GeneratedValue(strategy = UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val id: EventId? = null,

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    var location: Location,

    @Embedded
    var code: JidCode,

    var year: Int,

    @OneToMany(mappedBy = "event")
    var participants: List<Participant> = emptyList(),
)
