package info.jotajoti.jid.build

import java.nio.file.*
import java.util.*

private fun createCountriesJson() {

    Locale
        .getAvailableLocales()
        .filter { it.country.isNotEmpty() && it.language.isNotEmpty() }
        .filter { it.country.matches("[a-zA-Z]{2}".toRegex()) }
        .filter { it.language.length == 2 }
        .distinctBy { it.language }
        .forEach { languageLocale ->
            Files.createDirectories(Path.of("target/i18n/${languageLocale.language}"))

            val countries = mutableSetOf<String>()

            Locale
                .getAvailableLocales()
                .filter { it.country.isNotEmpty() }
                .filter { it.country.matches("[a-zA-Z]{2}".toRegex()) }
                .distinctBy { it.country }
                .sortedBy { it.country }
                .forEach { locale ->
                    countries += "\"${locale.country}\": \"${locale.getDisplayCountry(languageLocale)}\""
                }

            val countriesString = countries
                .sorted()
                .joinToString(
                    separator = ",\n\t",
                    prefix = "{\n\t",
                    postfix = "\n}",
                )

            Files.writeString(Path.of("target/i18n/${languageLocale.language}/countries.json"), countriesString)
        }
}

private fun createCountryCodeList() {

    val countryCodes = Locale
        .getISOCountries()
        .sorted()
        .joinToString(
            separator = ",\n  ",
            prefix = "[\n  ",
            postfix = "\n]",
            transform = {
                "\"$it\""
            }
        )

    println(countryCodes)
}

private fun printSimpleCountryList() {

    val enUs = Locale.of("en_US")

    val countryCodes = Locale
        .getAvailableLocales()
        .filter { it.country.isNotEmpty() }
        .filter { it.country.matches("[a-zA-Z]{2}".toRegex()) }
        .distinctBy { it.country }
        .map {
            "${it.getDisplayCountry(enUs)} = ${it.country.lowercase()}"
        }
        .sorted()
        .joinToString(
            separator = "\n"
        )

    println(countryCodes)
}

fun main() {
    printSimpleCountryList()
}
