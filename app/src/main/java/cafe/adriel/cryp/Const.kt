package cafe.adriel.cryp

object Const {
    // API
    const val WALLET_API_BASE_URL = "https://multiexplorer.com/api/"
    const val PRICE_API_BASE_URL = "https://min-api.cryptocompare.com/data/"

    // Database
    const val DB_WALLETS = "wallets"
    const val DB_PREFERENCES = "preferences"

    // Preference
    const val PREF_WALLET_ORDER = "walletOrder"
    const val PREF_COIN_FORMAT = "coinFormat"

    // Extra
    const val EXTRA_WALLET = "wallet"

    // Currency
    const val BTC_TO_MBTC = 1_000
    const val BTC_TO_BITS = 1_000_000
    const val BTC_TO_SATOSHI = 100_000_000
}