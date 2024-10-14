package info.jotajoti.jampuz.jidcode

data class JidCodeStats(
    val count: Int,
    val uniqueCount: Int,
    val uniqueCountries: List<String>,
    val countryStats: List<CountryStat>,
    val uniqueRegions: List<Region>,
) {

    val id: String
        get() = hashCode().toString()

    val uniqueCountryCount: Int
        get() = uniqueCountries.size

    val uniqueRegionCount: Int
        get() = uniqueRegions.size
}

data class CountryStat(
    val country: String,
    val uniqueCount: Int,
) {

    val id: String
        get() = hashCode().toString()
}
