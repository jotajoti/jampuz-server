package info.jotajoti.jid.location

import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.jidcode.JidCode
import info.jotajoti.jid.participant.Participant
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

typealias LocationId = UUID

@Entity
data class Location(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val id: LocationId? = null,

    var name: String,

    var code: JidCode,

    var year: Int,

    @ManyToMany
    @JoinTable(
        name = "location_owner",
        joinColumns = [JoinColumn(name = "location_id")],
        inverseJoinColumns = [JoinColumn(name = "admin_id")]
    )
    var owners: List<Admin>,

    @OneToMany(mappedBy = "location")
    var participants: List<Participant> = emptyList(),
)
