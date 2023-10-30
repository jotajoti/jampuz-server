package info.jotajoti.jid.jidcode

import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class JidCodeController {

    @SchemaMapping
    fun value(jidCode: JidCode) = jidCode.code
}