package info.jotajoti.jid.status

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class StatusController {

    @QueryMapping
    fun status() = Status(ok = true)
}