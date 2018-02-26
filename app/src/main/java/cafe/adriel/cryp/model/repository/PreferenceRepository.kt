package cafe.adriel.cryp.model.repository

import android.preference.PreferenceManager
import cafe.adriel.cryp.App
import cafe.adriel.cryp.Const
import cafe.adriel.cryp.R
import cafe.adriel.cryp.model.entity.CryptoUnit
import java.util.*

object PreferenceRepository {

    private val prefDb by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.context)
    }

    fun isFirstOpen() =
            prefDb.getBoolean(Const.PREF_FIRST_OPEN, true)

    fun setFirstOpen(firstOpen: Boolean) =
            prefDb.edit()
                    .putBoolean(Const.PREF_FIRST_OPEN, firstOpen)
                    .apply()

    fun getSupportedLanguages() =
            App.context.resources.getStringArray(R.array.language_values)

    fun getLanguage() =
            Locale.forLanguageTag(
                    prefDb.getString(Const.PREF_LANGUAGE, Locale.ENGLISH.language))

    fun setLanguage(language: String) =
            prefDb.edit()
                    .putString(Const.PREF_LANGUAGE, language)
                    .apply()

    fun getCurrency() =
            Currency.getInstance(prefDb.getString(Const.PREF_CURRENCY, Const.DEFAULT_CURRENCY))

    fun getCryptoUnit() =
            CryptoUnit.valueOf(
                    prefDb.getString(Const.PREF_CRYPTO_UNIT, CryptoUnit.BTC.name))

}