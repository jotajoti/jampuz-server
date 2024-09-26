package info.jotajoti.jid.location

import info.jotajoti.jid.admin.*
import info.jotajoti.jid.event.*
import jakarta.persistence.*
import jakarta.persistence.GenerationType.*
import org.hibernate.annotations.*
import org.hibernate.type.*

@Entity
data class Location(

    @Id
    @GeneratedValue(strategy = UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val id: LocationId? = null,

    var name: String,

    @ManyToMany
    @JoinTable(
        name = "location_owner",
        joinColumns = [JoinColumn(name = "location_id")],
        inverseJoinColumns = [JoinColumn(name = "admin_id")],
    )
    var owners: List<Admin>,

    @OneToMany(mappedBy = "location")
    var events: List<Event> = emptyList()

)
