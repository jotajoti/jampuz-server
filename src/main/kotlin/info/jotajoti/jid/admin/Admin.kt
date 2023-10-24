package info.jotajoti.jid.admin

import info.jotajoti.jid.location.Location
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes.VARCHAR
import java.util.*

typealias AdminId = UUID

@Entity
data class Admin(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(VARCHAR)
    val id: AdminId? = null,

    var name: String,

    var email: String,

    @Basic(fetch = LAZY)
    var passwordHash: String,

    @ManyToMany(mappedBy = "owners")
    var locations: List<Location> = emptyList(),
)
