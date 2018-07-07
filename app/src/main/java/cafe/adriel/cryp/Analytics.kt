package cafe.adriel.cryp

import android.os.Bundle
import cafe.adriel.cryp.model.entity.Crypto
import com.google.firebase.analytics.FirebaseAnalytics

object Analytics {
    private const val EVENT_SCAN_WALLET = "scan_wallet"
    private const val EVENT_ADD_WALLET = "add_wallet"
    private const val EVENT_COPY_WALLET = "copy_wallet"
    private const val EVENT_SHARE_WALLET = "share_wallet"
    private const val EVENT_CHANGE_LANGUAGE = "change_language"
    private const val EVENT_CHANGE_CURRENCY = "change_currency"
    private const val EVENT_CHANGE_CRYPTO_UNIT = "change_crypto_unit"

    private const val PARAM_CRYPTO = "crypto"
    private const val PARAM_LANGUAGE = "language"
    private const val PARAM_FIAT = "fiat"
    private const val PARAM_UNIT = "unit"

    private val fbAnalytics = FirebaseAnalytics.getInstance(App.context)

    fun logScanWallet(){
        if(BuildConfig.RELEASE) fbAnalytics.logEvent(EVENT_SCAN_WALLET, null)
    }

    fun logAddWallet(crypto: Crypto){
        val params = Bundle().apply {
            putString(PARAM_CRYPTO, crypto.symbol.toLowerCase())
        }
        if(BuildConfig.RELEASE) fbAnalytics.logEvent(EVENT_ADD_WALLET, params)
    }

    fun logCopyWallet(crypto: Crypto){
        val params = Bundle().apply {
            putString(PARAM_CRYPTO, crypto.symbol.toLowerCase())
        }
        if(BuildConfig.RELEASE) fbAnalytics.logEvent(EVENT_COPY_WALLET, params)
    }

    fun logShareWallet(crypto: Crypto){
        val params = Bundle().apply {
            putString(PARAM_CRYPTO, crypto.symbol.toLowerCase())
        }
        if(BuildConfig.RELEASE) fbAnalytics.logEvent(EVENT_SHARE_WALLET, params)
    }

    fun logChangeLanguage(language: String){
        val params = Bundle().apply {
            putString(PARAM_LANGUAGE, language.toLowerCase())
        }
        if(BuildConfig.RELEASE) fbAnalytics.logEvent(EVENT_CHANGE_LANGUAGE, params)
    }

    fun logChangeCurrency(fiat: String){
        val params = Bundle().apply {
            putString(PARAM_FIAT, fiat.toLowerCase())
        }
        if(BuildConfig.RELEASE) fbAnalytics.logEvent(EVENT_CHANGE_CURRENCY, params)
    }

    fun logChangeCryptoUnit(unit: String){
        val params = Bundle().apply {
            putString(PARAM_UNIT, unit.toLowerCase())
        }
        if(BuildConfig.RELEASE) fbAnalytics.logEvent(EVENT_CHANGE_CRYPTO_UNIT, params)
    }
}