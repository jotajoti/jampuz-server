package info.jotajoti.jid.persistence

import info.jotajoti.jid.admin.Admin
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType.TIMESTAMP
import java.util.*

@MappedSuperclass
abstract class AbstractAuditable(

    @ManyToOne
    var createdBy: Admin? = null,

    @Temporal(TIMESTAMP)
    val createdDate: Date = Date(),

    @ManyToOne
    var lastModifiedBy: Admin? = null,

    @Temporal(TIMESTAMP)
    val lastModifiedDate: Date = Date(),
)
