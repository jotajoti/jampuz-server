package info.jotajoti.jid.jidcode

data class JidCodeStats(
    val count: Int,
    val uniqueCount: Int,
    val uniqueCountries: List<String>,
    val uniqueRegions: List<Region>,
) {

    val uniqueCountryCount: Int
        get() = uniqueCountries.size

    val uniqueRegionCount: Int
        get() = uniqueRegions.size
}
