package info.jotajoti.jid

import info.jotajoti.jid.jidcode.*
import java.util.*

fun JidCode.Companion.random() =
    JidCode(
        "${(1..7).random()}${randomCountryCode()}${(0..9).random()}${(0..9).random()}${nextChar()}"
    )

private val charPool: List<Char> = ('A'..'Z').toList()

private fun nextChar() = charPool.random()

private fun randomCountryCode() = Locale
    .getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2)
    .random()
    .lowercase()
