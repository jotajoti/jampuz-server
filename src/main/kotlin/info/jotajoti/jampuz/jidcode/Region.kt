package info.jotajoti.jampuz.jidcode

enum class Region(val code: Int, val countryCodes: Set<String>) {
    AFRICA(
        1,
        setOf(
            "ao",
            "bj",
            "bw",
            "bf",
            "bi",
            "cv",
            "cm",
            "td",
            "km",
            "ci",
            "cd",
            "sz",
            "et",
            "ga",
            "gm",
            "gh",
            "gn",
            "gw",
            "ke",
            "ls",
            "lr",
            "mg",
            "mw",
            "mu",
            "mz",
            "na",
            "ne",
            "ng",
            "rw",
            "st",
            "sn",
            "sc",
            "sl",
            "za",
            "ss",
            "tg",
            "ug",
            "tz",
            "zm",
            "zw"
        )
    ),
    ARAB(
        2,
        setOf(
            "dz",
            "bh",
            "eg",
            "iq",
            "jo",
            "kw",
            "lb",
            "ly",
            "mr",
            "ma",
            "om",
            "qa",
            "sa",
            "ps",
            "sd",
            "sy",
            "tn",
            "ae",
            "ye"
        )
    ),
    ASIA_PACIFIC(
        3,
        setOf(
            "af",
            "au",
            "bd",
            "bt",
            "bn",
            "kh",
            "fj",
            "hk",
            "in",
            "id",
            "jp",
            "ki",
            "mo",
            "my",
            "mv",
            "mn",
            "mm",
            "np",
            "nz",
            "pk",
            "pg",
            "ph",
            "kr",
            "tw",
            "sg",
            "sb",
            "lk",
            "th",
            "tl",
            "vn"
        )
    ),
    EUROASIA(
        4, setOf(
            "ua",
        )
    ),
    EUROPE(
        5,
        setOf(
            "am",
            "at",
            "az",
            "by",
            "be",
            "ba",
            "bg",
            "hr",
            "cy",
            "cz",
            "dk",
            "ee",
            "fi",
            "fr",
            "ge",
            "de",
            "gr",
            "hu",
            "is",
            "ie",
            "il",
            "it",
            "lv",
            "li",
            "lt",
            "lu",
            "mt",
            "mc",
            "me",
            "nl",
            "mk",
            "no",
            "pl",
            "pt",
            "md",
            "ro",
            "sm",
            "rs",
            "sk",
            "si",
            "es",
            "se",
            "ch",
            "tr",
            "gb"
        )
    ),
    INTERAMERICA(
        6,
        setOf(
            "ag",
            "ar",
            "aw",
            "bs",
            "bb",
            "bz",
            "ve",
            "br",
            "ca",
            "cl",
            "co",
            "cr",
            "cw",
            "dm",
            "do",
            "ec",
            "sv",
            "gd",
            "gt",
            "gy",
            "ht",
            "hn",
            "jm",
            "mx",
            "ni",
            "pa",
            "py",
            "pe",
            "bo",
            "lc",
            "vc",
            "sr",
            "tt",
            "us",
            "uy"
        )
    ),
    SPECIAL(7, emptySet())
}