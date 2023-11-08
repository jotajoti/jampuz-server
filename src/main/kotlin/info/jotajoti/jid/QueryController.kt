package info.jotajoti.jid

import org.springframework.beans.factory.annotation.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.stereotype.*

@Controller
class QueryController(
    @Value("\${serverVersion}")
    private val serverVersion: String
) {

    @QueryMapping
    fun serverVersion() = serverVersion
}
