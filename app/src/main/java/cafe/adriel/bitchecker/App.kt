package cafe.adriel.bitchecker

import android.app.Application
import com.esotericsoftware.minlog.Log
import io.paperdb.Paper

class App : Application() {

    companion object {
        lateinit var CONTEXT: App
    }

    override fun onCreate() {
        super.onCreate()
        CONTEXT = this
        Paper.init(this)
        Paper.setLogLevel(if(BuildConfig.RELEASE) Log.LEVEL_NONE else Log.LEVEL_ERROR)
    }

}