package cafe.adriel.cryp.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import cafe.adriel.cryp.R
import cafe.adriel.cryp.colorFrom
import cafe.adriel.cryp.darken
import cafe.adriel.cryp.model.entity.MessageType
import com.arellomobile.mvp.MvpAppCompatActivity
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog
import com.evernote.android.state.StateSaver
import com.franmontiel.localechanger.LocaleChanger
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.tbruyelle.rxpermissions2.RxPermissions
import es.dmoral.toasty.Toasty

abstract class BaseActivity : MvpAppCompatActivity(), IView {

    private var progressDialog : Dialog? = null

    protected val rxPermissions by lazy { RxPermissions(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = colorFrom(R.color.colorPrimaryDark)
        window.navigationBarColor = colorFrom(R.color.colorPrimaryDark).darken
        StateSaver.restoreInstanceState(this, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        ActivityRecreationHelper.onResume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityRecreationHelper.onDestroy(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleChanger.configureBaseContext(newBase))
    }

    override fun showMessage(messageRes: Int, type: MessageType) =
            showMessage(getString(messageRes), type)

    override fun showMessage(message: String, type: MessageType) =
            when(type){
                MessageType.DEFAULT -> Toasty.normal(this, message).show()
                MessageType.SUCCESS -> Toasty.success(this, message).show()
                MessageType.INFO -> Toasty.info(this, message).show()
                MessageType.WARN -> Toasty.warning(this, message).show()
                MessageType.ERROR -> Toasty.error(this, message).show()
            }

    override fun showProgressDialog(title: String, message: String){
        progressDialog = AwesomeProgressDialog(this)
                .setTitle(title)
                .setMessage(message)
                .setColoredCircle(R.color.colorAccent)
                .setCancelable(false)
                .show()
    }

    override fun hideProgressDialog(){
        progressDialog?.dismiss()
    }

}