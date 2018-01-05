package cafe.adriel.cryp

import android.app.Application
import android.content.res.Configuration
import android.preference.PreferenceManager
import cafe.adriel.cryp.model.repository.PreferenceRepository
import com.esotericsoftware.minlog.Log
import com.franmontiel.localechanger.LocaleChanger
import io.paperdb.Paper
import timber.log.Timber
import java.util.*

class App : Application() {

    companion object {
        lateinit var context: App
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        initLogging()
        initDatabase()
        initPreferences()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        LocaleChanger.onConfigurationChanged()
    }

    private fun initLogging(){
        if (!BuildConfig.RELEASE) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initDatabase(){
        Paper.init(this)
        Paper.setLogLevel(if(BuildConfig.RELEASE) Log.LEVEL_NONE else Log.LEVEL_ERROR)
    }

    private fun initPreferences(){
        val supportedLanguages = PreferenceRepository.getSupportedLanguages()
        val defaultLanguage = Locale.getDefault().language
        LocaleChanger.initialize(this, supportedLanguages.map { Locale.forLanguageTag(it) })
        PreferenceManager.setDefaultValues(this, R.xml.settings, false)
        if(PreferenceRepository.isFirstOpen()){
            PreferenceRepository.setFirstOpen(false)
            if(supportedLanguages.contains(defaultLanguage)) {
                PreferenceRepository.setLanguage(defaultLanguage)
            }
        }
    }

}