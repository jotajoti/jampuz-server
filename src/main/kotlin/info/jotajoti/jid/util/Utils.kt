package info.jotajoti.jid.util

fun <T> T?.toList() = when (this) {
    null -> emptyList()
    else -> listOf(this)
}
