package cafe.adriel.cryp.view.wallet.add

import android.Manifest
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import cafe.adriel.cryp.*
import cafe.adriel.cryp.model.entity.Coin
import cafe.adriel.cryp.model.entity.MessageType
import cafe.adriel.cryp.view.BaseActivity
import cafe.adriel.cryp.view.qrcode.scan.ScanQrCodeActivity
import cafe.adriel.kbus.KBus
import com.arellomobile.mvp.presenter.InjectPresenter
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_wallet_add.*
import java.util.concurrent.TimeUnit

class AddWalletActivity : BaseActivity(), AddWalletView {
    @InjectPresenter
    lateinit var presenter: AddWalletPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_add)

        setSupportActionBar(vToolbar)
        vToolbar.setNavigationIcon(R.drawable.ic_close)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        window.statusBarColor = colorFrom(R.color.colorAccentDark)
        window.navigationBarColor = colorFrom(R.color.colorAccentDark).darken

        if (savedInstanceState == null) {
            vRoot.apply {
                visibility = View.INVISIBLE
                if (viewTreeObserver.isAlive) {
                    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                            startRevealTransition()
                        }
                    })
                }
            }
        }

        vQrCodeLayout.setOnClickListener { scanQrCode() }
        vAddWallet.setOnClickListener { addWallet() }

        val coins = mutableListOf<String>().apply {
            Coin.values().forEach {
                add(it.toString())
            }
        }
        vCoins.adapter = ArrayAdapter(this, R.layout.spinner_item_coin, coins).apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item_coin)
        }

        RxTextView.textChanges(vPublicKey)
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { setQrCode(it.toString()) }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        KBus.subscribe<QrCodeScannedEvent>(this, {
            setPublicKey(it.text)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        KBus.unsubscribe(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
            when (item?.itemId) {
                android.R.id.home -> {
                    finish()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun close() {
        finish()
    }

    private fun scanQrCode() {
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe { granted ->
                    if (granted) {
                        startActivity<ScanQrCodeActivity>()
                    } else {
                        setQrCode("")
                    }
                }
    }

    private fun setPublicKey(qrCodeValue: String){
        if(qrCodeValue.isNotEmpty()) {
            val publicKey = if (qrCodeValue.contains(':')) qrCodeValue.split(":")[1]
                            else qrCodeValue
            vPublicKey.setText(publicKey)
        }
    }

    private fun setQrCode(text: String){
        if(text.isNotEmpty()) {
            vQrCode.setImageBitmap(text.getQrCode(R.color.colorAccentDark))
            vQrCode.setPadding(0, 0, 0, 0)
        } else {
            vQrCode.setImageResource(R.drawable.ic_scan_qrcode)
            vQrCode.setPadding(20.px, 20.px, 20.px, 20.px)
        }
    }

    private fun addWallet(){
        val publicKey = vPublicKey.text.toString()
        val coin = Coin.values()[vCoins.selectedItemPosition]
        if(publicKey.isNotEmpty()){
            presenter.saveWallet(coin, publicKey)
        } else {
            showMessage(R.string.type_or_scan_public_key, MessageType.WARN)
        }
    }

    private fun startRevealTransition(){
        val x = 9 * vRoot.width / 10
        val y = 9 * vRoot.height / 10
        val startRadius = 0F
        val endRadius = Math.max(vRoot.width, vRoot.height).toFloat()
        val duration = intFrom(android.R.integer.config_mediumAnimTime).toLong()

        vRoot.visibility = View.VISIBLE
        ViewAnimationUtils
                .createCircularReveal(vRoot, x, y, startRadius, endRadius)
                .setDuration(duration)
                .start()
    }
}