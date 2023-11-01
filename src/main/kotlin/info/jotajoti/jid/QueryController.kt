package info.jotajoti.jid

import org.springframework.beans.factory.annotation.Value
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class QueryController(
    @Value("\${serverVersion}")
    private val serverVersion: String
) {

    @QueryMapping
    fun serverVersion() = serverVersion
}
