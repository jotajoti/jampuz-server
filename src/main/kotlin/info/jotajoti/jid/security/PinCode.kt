package info.jotajoti.jid.security

import kotlin.random.Random

@JvmInline
value class PinCode(val value: String) {

    init {
        assert(value.matches("[0-9]{4}".toRegex()))
    }

    companion object {

        fun random() =
            PinCode("1234")
    }
}