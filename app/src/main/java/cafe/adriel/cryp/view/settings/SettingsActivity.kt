package cafe.adriel.cryp.view.settings

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import cafe.adriel.cryp.R
import cafe.adriel.cryp.view.custom.AppCompatPreferenceActivity
import com.franmontiel.localechanger.LocaleChanger
import com.franmontiel.localechanger.utils.ActivityRecreationHelper

class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.let {
            it.title = getString(R.string.settings)
            it.setDisplayHomeAsUpEnabled(true)
        }
        if(savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, SettingsFragment())
                    .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        ActivityRecreationHelper.onResume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityRecreationHelper.onDestroy(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleChanger.configureBaseContext(newBase))
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem) =
        when(item.itemId){
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onMenuItemSelected(featureId, item)
        }

    override fun isValidFragment(fragmentName: String) =
        SettingsFragment::class.java.name == fragmentName

}