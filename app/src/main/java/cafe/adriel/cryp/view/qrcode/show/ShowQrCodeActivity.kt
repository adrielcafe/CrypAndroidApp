package cafe.adriel.cryp.view.qrcode.show

import android.os.Bundle
import cafe.adriel.cryp.Const
import cafe.adriel.cryp.R
import cafe.adriel.cryp.copyToClipboard
import cafe.adriel.cryp.getQrCode
import cafe.adriel.cryp.model.entity.MessageType
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.view.BaseActivity
import com.evernote.android.state.State
import kotlinx.android.synthetic.main.activity_show_qrcode.*

class ShowQrCodeActivity : BaseActivity() {
    @State
    lateinit var wallet: Wallet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_qrcode)

        if(intent.hasExtra(Const.EXTRA_WALLET)) {
            wallet = intent.getParcelableExtra(Const.EXTRA_WALLET)
        } else {
            finish()
        }

        vCoin.text = wallet.coin.toString()
        vPublicKey.text = wallet.address
        vQrCode.setImageBitmap(wallet.address.getQrCode(R.color.colorPrimaryDark))

        vClose.setOnClickListener { finish() }
        vQrCode.setOnClickListener { copyPublicKeyToClipboard() }
        vPublicKey.setOnClickListener { copyPublicKeyToClipboard() }
        vTapToCopy.setOnClickListener { copyPublicKeyToClipboard() }
    }

    private fun copyPublicKeyToClipboard(){
        val label = getString(R.string.coin_public_key, wallet.coin.fullName)
        wallet.address.copyToClipboard(label)
        showMessage(R.string.copied, MessageType.SUCCESS)
    }

}