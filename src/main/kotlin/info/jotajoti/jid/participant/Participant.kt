package info.jotajoti.jid.participant

import info.jotajoti.jid.admin.Admin
import info.jotajoti.jid.jidcode.FoundJidCode
import info.jotajoti.jid.location.Location
import info.jotajoti.jid.security.PinCode
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

typealias ParticipantId = UUID

@Entity
data class Participant(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val id: ParticipantId? = null,

    var name: String,

    var pinCode: PinCode,

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = true)
    var admin: Admin? = null,

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    var location: Location,

    @OneToMany(mappedBy = "participant")
    var foundJidCodes: List<FoundJidCode> = emptyList(),
)
