package cafe.adriel.cryp

object Const {
    // URL
    const val GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
    const val MARKET_URI = "market://details?id=${BuildConfig.APPLICATION_ID}"
    const val CONTACT_EMAIL = "me@adriel.cafe"

    // API
    const val WALLET_API_BASE_URL = "https://multiexplorer.com/api/"
    const val PRICE_API_BASE_URL = "https://min-api.cryptocompare.com/data/"

    // Database
    const val DB_WALLETS = "wallets"

    // Preference
    const val PREF_FIRST_OPEN = "firstOpen"
    const val PREF_LANGUAGE = "language"
    const val PREF_CURRENCY = "currency"
    const val PREF_CRYPTOCURRENCY_UNIT = "cryptocurrencyUnit"
    const val PREF_SHARE = "share"
    const val PREF_REVIEW = "review"
    const val PREF_CONTACT = "contact"
    const val PREF_APP_VERSION = "appVersion"

    // Extra
    const val EXTRA_WALLET = "wallet"

    // Currency
    const val DEFAULT_CURRENCY = "USD"
    const val BTC_TO_MBTC = 1_000
    const val BTC_TO_BITS = 1_000_000
    const val BTC_TO_SATOSHI = 100_000_000
}