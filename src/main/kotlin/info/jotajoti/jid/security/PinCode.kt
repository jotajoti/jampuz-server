package info.jotajoti.jid.security

@JvmInline
value class PinCode(val value: String) {

    init {
        assert(value.matches("[0-9]{4}".toRegex()))
    }

    companion object
}