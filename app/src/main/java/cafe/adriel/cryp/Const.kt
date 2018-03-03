package cafe.adriel.cryp

import cafe.adriel.cryp.model.entity.Crypto

object Const {
    // URL
    const val GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
    const val MARKET_URI = "market://details?id=${BuildConfig.APPLICATION_ID}"
    const val CONTACT_EMAIL = "me@adriel.cafe"

    // API
    const val WALLET_API_BASE_URL = "https://scryp.herokuapp.com/"
    const val CRYPTO_PRICE_API_BASE_URL = "https://min-api.cryptocompare.com/data/"

    // Database
    const val DB_CRYPTOS = "cryptos"
    const val DB_PRICES = "prices"
    const val DB_WALLETS = "wallets"

    // Features
    const val WALLET_SLOTS_FREE = 10
    const val WALLET_SLOTS_PRO = 100

    // Preference
    const val PREF_FIRST_OPEN = "firstOpen"
    const val PREF_LANGUAGE = "language"
    const val PREF_CURRENCY = "currency"
    const val PREF_CRYPTO_UNIT = "cryptoUnit"
    const val PREF_SHARE = "share"
    const val PREF_REVIEW = "review"
    const val PREF_CONTACT = "contact"
    const val PREF_APP_VERSION = "appVersion"

    // Extra
    const val EXTRA_WALLET = "wallet"

    // Currency
    val SUPPORTED_CURRENCIES = arrayOf(
        "afn", "all", "dzd", "aoa", "ars", "amd", "aud", "azn", "bsd", "bhd", "bdt", "bbd", "byr",
        "btn", "bob", "bam", "bwp", "brl", "gbp", "bnd", "bgn", "bif", "khr", "cad", "xaf", "clp",
        "cny", "cop", "crc", "hrk", "cuc", "czk", "dkk", "dop", "egp", "etb", "eur", "gel", "ghs",
        "gip", "gtq", "hnl", "hkd", "huf", "isk", "inr", "idr", "irr", "iqd", "ils", "jmd", "jpy",
        "jod", "kzt", "kes", "kwd", "kgs", "lbp", "lsl", "mop", "myr", "mvr", "mur", "mxn", "mdl",
        "mad", "mzn", "mmk", "nad", "npr", "twd", "nzd", "nio", "ngn", "nok", "omr", "pkr", "pab",
        "pyg", "pen", "php", "pln", "qar", "ron", "rub", "rwf", "svc", "sar", "rsd", "sgd", "sbd",
        "zar", "krw", "lkr", "szl", "sek", "chf", "tzs", "thb", "top", "ttd", "tnd", "try", "usd",
        "ugx", "uah", "aed", "uyu", "uzs", "vuv", "vef", "vnd", "xof")
    val CRYPTO_BTC = Crypto("BTC", "Bitcoin")
    val CRYPTO_LTC = Crypto("LTC", "Litecoin")
    val CRYPTO_ETH = Crypto("ETH", "Ethereum")
    val DEFAULT_CRYPTO = CRYPTO_BTC
    const val DEFAULT_CURRENCY = "USD"
    const val SYMBOL_BTC = "Ƀ"
    const val SYMBOL_ETH = "Ξ"
    const val BTC_TO_MBTC = 1_000
    const val BTC_TO_BITS = 1_000_000
    const val BTC_TO_SATOSHI = 100_000_000
}