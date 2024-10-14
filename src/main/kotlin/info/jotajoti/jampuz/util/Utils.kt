package info.jotajoti.jampuz.util

fun <T> T?.toList() = when (this) {
    null -> emptyList()
    else -> listOf(this)
}
