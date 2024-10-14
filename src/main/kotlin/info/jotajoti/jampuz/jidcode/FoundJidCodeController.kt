package info.jotajoti.jampuz.jidcode

import info.jotajoti.jampuz.security.*
import jakarta.validation.*
import org.springframework.graphql.data.method.annotation.*
import org.springframework.security.core.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*

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