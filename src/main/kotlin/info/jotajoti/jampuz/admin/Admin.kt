package info.jotajoti.jampuz.admin

import info.jotajoti.jampuz.location.*
import jakarta.persistence.*
import jakarta.persistence.FetchType.*
import jakarta.persistence.GenerationType.*
import org.hibernate.annotations.*
import org.hibernate.type.*

@Entity
data class Admin(
    @Id
    @GeneratedValue(strategy = UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val id: AdminId? = null,

    var name: String,

    var email: String,

    @Basic(fetch = LAZY)
    var passwordHash: String,

    @ManyToMany(mappedBy = "owners")
    var locations: List<Location> = emptyList(),
)
