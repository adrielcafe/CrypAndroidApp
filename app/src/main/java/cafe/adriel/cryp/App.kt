package cafe.adriel.cryp

import android.app.Application
import com.esotericsoftware.minlog.Log
import io.paperdb.Paper
import timber.log.Timber

class App : Application() {

    companion object {
        lateinit var CONTEXT: App
    }

    override fun onCreate() {
        super.onCreate()
        CONTEXT = this

        if (!BuildConfig.RELEASE) {
            Timber.plant(Timber.DebugTree())
        }

        Paper.init(this)
        Paper.setLogLevel(if(BuildConfig.RELEASE) Log.LEVEL_NONE else Log.LEVEL_ERROR)
    }

}