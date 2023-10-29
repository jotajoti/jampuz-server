package info.jotajoti.jid.jidcode

import info.jotajoti.jid.security.RequireParticipant
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated

@Controller
@Validated
class FoundJidCodeController(
    private val foundJidCodeService: FoundJidCodeService,
) {

    @RequireParticipant
    @MutationMapping
    fun registerFoundJidCode(
        @Valid @Argument input: RegisterFoundJidCodeInput,
        authentication: Authentication
    ) = foundJidCodeService.createFoundJidCode(input, authentication)

}