package cafe.adriel.cryp.view.wallet.add

import android.Manifest
import android.animation.Animator
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import cafe.adriel.cryp.*
import cafe.adriel.cryp.model.entity.Crypto
import cafe.adriel.cryp.model.entity.MessageType
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.view.BaseActivity
import cafe.adriel.cryp.view.wallet.scan.ScanWalletActivity
import cafe.adriel.cryp.view.wallet.select.SelectCryptoActivity
import cafe.adriel.kbus.KBus
import com.arellomobile.mvp.presenter.InjectPresenter
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_add_wallet.*
import java.util.concurrent.TimeUnit

class AddWalletActivity : BaseActivity(), AddWalletView {
    @InjectPresenter
    lateinit var presenter: AddWalletPresenter

    lateinit var selectedCrypto: Crypto
    var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_wallet)

        setSupportActionBar(vToolbar)
        vToolbar.setNavigationIcon(R.drawable.ic_close)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        window.statusBarColor = colorFrom(R.color.colorAccentDark)
        window.navigationBarColor = colorFrom(R.color.colorAccentDark).darken

        if(intent.hasExtra(Const.EXTRA_WALLET)){
            val wallet: Wallet = intent.getParcelableExtra(Const.EXTRA_WALLET)
            setEditMode(wallet)
        } else {
            setCrypto(Const.DEFAULT_CRYPTO)
        }

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

        vScanQrCode.enableMergePathsForKitKatAndAbove(true)

        vCryptosLayout.setOnClickListener { selectCrypto() }
        vScanQrCode.setOnClickListener { scanQrCode() }
        vQrCode.setOnClickListener { scanQrCode() }
        vTapToScan.setOnClickListener { scanQrCode() }
        vSaveWallet.setOnClickListener { addWallet() }

        vPublicKey.textChanges()
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { setQrCode(it.toString()) }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        KBus.subscribe<SelectedCryptoEvent>(this) {
            setCrypto(it.crypto)
        }
        KBus.subscribe<QrCodeScannedEvent>(this) {
            setPublicKey(it.text)
        }
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

    private fun setEditMode(wallet: Wallet){
        isEditMode = true

        setCrypto(wallet.crypto)
        setQrCode(wallet.address)
        setPublicKey(wallet.address)

        vName.setText(wallet.name)
//        if(wallet.crypto.autoRefresh) vBalance.setText(R.string.auto_updated)
//        else
        vBalance.setText(wallet.balance.toPlainString())

        vCryptosLayout.isEnabled = false
        vPublicKey.isEnabled = false
        vScanQrCode.isEnabled = false
        vQrCode.isEnabled = false

        vArrowRight.visibility = View.GONE
        vTapToScan.visibility = View.GONE
    }

    private fun selectCrypto(){
        start<SelectCryptoActivity>()
    }

    private fun scanQrCode() {
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe { granted ->
                    if (granted) {
                        start<ScanWalletActivity>()
                    } else {
                        setQrCode("")
                    }
                }
    }

    private fun setCrypto(crypto: Crypto){
        selectedCrypto = crypto
        vCryptoSymbol.text = crypto.symbol
        vCryptoName.text = crypto.name
        vCryptoLogo.setCrypto(crypto)
        toggleBalanceMessage()
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
            vQrCode.visibility = View.VISIBLE
            vScanQrCode.pauseAnimation()
            vScanQrCode.visibility = View.INVISIBLE
        } else {
            vQrCode.setImageBitmap(null)
            vQrCode.visibility = View.INVISIBLE
            vScanQrCode.resumeAnimation()
            vScanQrCode.visibility = View.VISIBLE
        }
    }

    private fun addWallet(){
        val publicKey = vPublicKey.text.toString().trim()
        val name = vName.text.toString().trim()
        val balance = vBalance.text.toString().toBigDecimalOrNull()

        // Validations
        if(!presenter.hasWalletSlotRemaining() && !isEditMode){
            showMessage(R.string.you_can_track_ten_wallets, MessageType.INFO)
            return
        } else if(publicKey.isEmpty()){
            showMessage(R.string.type_or_scan_public_key, MessageType.INFO)
            return
        } else if(balance == null /*&& !selectedCrypto.autoRefresh*/) {
            showMessage(R.string.invalid_balance, MessageType.WARN)
            return
        } else if(!isConnected()) {
            showMessage(R.string.connect_internet, MessageType.WARN)
            return
        }

        presenter.saveWallet(selectedCrypto, publicKey, name, balance)
    }

    private fun toggleBalanceMessage(){
//        if(selectedCrypto.autoRefresh){
//            vBalance.isEnabled = false
//            vBalance.setText(getString(R.string.auto_updated))
//            vBalance.setTextColor(colorFrom(R.color.grey_light))
//        } else {
            vBalance.isEnabled = true
            vBalance.setText("")
            vBalance.setTextColor(Color.WHITE)
//        }
    }

    private fun startRevealTransition(){
        val x = 9 * vRoot.width / 10
        val y = 9 * vRoot.height / 10
        val startRadius = 0F
        val endRadius = Math.max(vRoot.width, vRoot.height).toFloat()
        val duration = intFrom(android.R.integer.config_mediumAnimTime).toLong()

        vRoot.visibility = View.VISIBLE
        val animation = ViewAnimationUtils
                .createCircularReveal(vRoot, x, y, startRadius, endRadius)
                .setDuration(duration)
        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) { }
            override fun onAnimationEnd(animation: Animator?) {
                vScanQrCode.playAnimation()
            }
            override fun onAnimationCancel(animation: Animator?) { }
            override fun onAnimationRepeat(animation: Animator?) { }
        })
        animation.start()
    }
}