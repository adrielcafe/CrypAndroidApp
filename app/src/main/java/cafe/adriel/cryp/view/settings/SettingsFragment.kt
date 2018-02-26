package cafe.adriel.cryp.view.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import cafe.adriel.cryp.*
import cafe.adriel.cryp.model.entity.CryptoUnit
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.view.wallet.show.ShowWalletActivity
import com.franmontiel.localechanger.LocaleChanger
import java.util.*

class SettingsFragment : PreferenceFragment(),
        Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private val prefLanguage by lazy { findPreference(Const.PREF_LANGUAGE) as ListPreference}
    private val prefCurrency by lazy { findPreference(Const.PREF_CURRENCY) as ListPreference }
    private val prefCryptoUnit by lazy { findPreference(Const.PREF_CRYPTO_UNIT) as ListPreference}
    private val prefDonateBtc by lazy { findPreference(Const.PREF_DONATE_BTC) }
    private val prefDonateLtc by lazy { findPreference(Const.PREF_DONATE_LTC) }
    private val prefDonateEth by lazy { findPreference(Const.PREF_DONATE_ETH) }
    private val prefShare by lazy { findPreference(Const.PREF_SHARE) }
    private val prefReview by lazy { findPreference(Const.PREF_REVIEW) }
    private val prefContact by lazy { findPreference(Const.PREF_CONTACT) }
    private val prefAppVersion by lazy { findPreference(Const.PREF_APP_VERSION) }

    private var hasInit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)

        prefLanguage.onPreferenceChangeListener = this
        prefCurrency.onPreferenceChangeListener = this
        prefCryptoUnit.onPreferenceChangeListener = this

        prefDonateBtc.onPreferenceClickListener = this
        prefDonateLtc.onPreferenceClickListener = this
        prefDonateEth.onPreferenceClickListener = this
        prefShare.onPreferenceClickListener = this
        prefReview.onPreferenceClickListener = this
        prefContact.onPreferenceClickListener = this

        addCurrencies()
        addCryptoUnits()

        updateSummary(prefLanguage)
        updateSummary(prefCurrency)
        updateSummary(prefCryptoUnit)

        setAppVersion()

        hasInit = true
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        val value = newValue.toString()
        preference?.let {
            if (it is ListPreference) {
                val index = it.findIndexOfValue(value)
                it.summary = if (index >= 0) it.entries[index] else null
            } else {
                it.summary = value
            }

            if(hasInit) {
                when (it.key) {
                    Const.PREF_LANGUAGE -> {
                        LocaleChanger.setLocale(Locale.forLanguageTag(value))
                        activity.recreate()
                    }
                }
            }
        }
        return true
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        when(preference?.key){
            Const.PREF_DONATE_BTC -> {
                val wallet = Wallet(Const.CRYPTO_BTC, Const.DONATE_BTC_ADDRESS)
                start<ShowWalletActivity>(Const.EXTRA_WALLET to wallet)
            }
            Const.PREF_DONATE_LTC -> {
                val wallet = Wallet(Const.CRYPTO_LTC, Const.DONATE_LTC_ADDRESS)
                start<ShowWalletActivity>(Const.EXTRA_WALLET to wallet)
            }
            Const.PREF_DONATE_ETH -> {
                val wallet = Wallet(Const.CRYPTO_ETH, Const.DONATE_ETH_ADDRESS)
                start<ShowWalletActivity>(Const.EXTRA_WALLET to wallet)
            }
            Const.PREF_SHARE -> shareApp()
            Const.PREF_REVIEW -> reviewApp()
            Const.PREF_CONTACT -> sendEmail()
        }
        return true
    }

    // Set currencies in current language
    private fun addCurrencies(){
        val allCurrencies = Currency.getAvailableCurrencies()
        val supportedCurrencies = Const.SUPPORTED_CURRENCIES
                .map { currencyCode ->
                    allCurrencies.first { it.currencyCode == currencyCode.toUpperCase() }
                }.sortedWith(kotlin.Comparator { o1, o2 ->
                    o1.displayName.compareTo(o2.displayName)
                })
        prefCurrency.entries = supportedCurrencies
                .map { "${it.displayName} (${it.symbol})" }
                .toTypedArray()
        prefCurrency.entryValues = supportedCurrencies
                .map { it.currencyCode }
                .toTypedArray()
    }

    private fun addCryptoUnits(){
        val supportedUnits = CryptoUnit.values()
        prefCryptoUnit.entries = supportedUnits
                .map { it.fullName }
                .toTypedArray()
        prefCryptoUnit.entryValues = supportedUnits
                .map { it.name }
                .toTypedArray()
    }

    private fun shareApp(){
        "${getString(R.string.you_should_try_crp)}\n${Const.GOOGLE_PLAY_URL}"
                .share(activity)
    }

    private fun reviewApp(){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(Const.MARKET_URI)
        startActivity(intent)
    }

    private fun sendEmail(){
        val subject = "${getString(R.string.cryp)} for Android | v${BuildConfig.VERSION_NAME}, SDK ${Build.VERSION.SDK_INT}"
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${Const.CONTACT_EMAIL}"))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        startActivity(intent)
    }

    private fun setAppVersion(){
        prefAppVersion.summary = "v${BuildConfig.VERSION_NAME}"
    }

    private fun updateSummary(preference: Preference){
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, ""))
    }

}