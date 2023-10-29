package info.jotajoti.jid.security

import kotlin.random.Random.Default.nextInt

@JvmInline
value class PinCode(val value: String) {

    init {
        assert(value.matches("[0-9]{4}".toRegex()))
    }

    companion object {

        fun random() = PinCode(
            listOf(
                nextInt(0, 10),
                nextInt(0, 10),
                nextInt(0, 10),
                nextInt(0, 10),
            ).joinToString(separator = "")
        )
    }
}