package cafe.adriel.bitchecker.view

import android.annotation.SuppressLint
import android.os.Bundle
import cafe.adriel.bitchecker.model.entity.MessageType
import com.arellomobile.mvp.MvpAppCompatActivity
import com.evernote.android.state.StateSaver
import es.dmoral.toasty.Toasty

@SuppressLint("Registered")
open class BaseActivity : MvpAppCompatActivity(), BaseView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateSaver.restoreInstanceState(this, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState)
    }

    override fun showMessage(message: String, type: MessageType) =
            when(type){
                MessageType.DEFAULT -> Toasty.normal(this, message).show()
                MessageType.SUCCESS -> Toasty.success(this, message).show()
                MessageType.INFO -> Toasty.info(this, message).show()
                MessageType.WARN -> Toasty.warning(this, message).show()
                MessageType.ERROR -> Toasty.error(this, message).show()
            }

}