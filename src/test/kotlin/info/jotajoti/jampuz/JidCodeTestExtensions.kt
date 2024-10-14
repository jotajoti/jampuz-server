package info.jotajoti.jampuz

import info.jotajoti.jampuz.jidcode.*

fun JidCode.Companion.random() =
    randomRegionByCountry()
        .let { region ->
            JidCode(
                "${region.code}${randomCountryCode(region)}${(0..9).random()}${(0..9).random()}${nextChar()}"
            )
        }

// Select a region based on an average of number of countries in total among all the regions
private fun randomRegionByCountry(): Region {
    val countryCodes = Region
        .values()
        .flatMap { region -> region.countryCodes }

    val selectedCountryCode = countryCodes.random()

    return Region
        .values()
        .first { region ->
            region.countryCodes.contains(selectedCountryCode)
        }
}

private val charPool: List<Char> = ('A'..'Z').toList()

private fun nextChar() = charPool.random()

private fun randomCountryCode(region: Region) = region.countryCodes.random().uppercase()
