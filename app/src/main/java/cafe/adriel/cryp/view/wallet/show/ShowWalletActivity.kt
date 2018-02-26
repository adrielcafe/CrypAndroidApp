package cafe.adriel.cryp.view.wallet.show

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import cafe.adriel.cryp.*
import cafe.adriel.cryp.model.entity.MessageType
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.view.BaseActivity
import com.evernote.android.state.State
import kotlinx.android.synthetic.main.activity_show_wallet.*

class ShowWalletActivity : BaseActivity() {
    @State
    lateinit var wallet: Wallet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_wallet)

        setSupportActionBar(vToolbar)
        vToolbar.setNavigationIcon(R.drawable.ic_close)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if(intent.hasExtra(Const.EXTRA_WALLET)) {
            wallet = intent.getParcelableExtra(Const.EXTRA_WALLET)
        } else {
            showMessage(R.string.wallet_not_found, MessageType.ERROR)
            finish()
        }

        vCryptoLogo.setImageResource(wallet.crypto.logoResId)
        vCryptoFullName.text = wallet.crypto.fullName
        vPublicKey.text = wallet.address
        vQrCode.setImageBitmap(wallet.address.getQrCode(R.color.colorPrimaryDark))

        vQrCodeLayout.setOnClickListener { copyPublicKeyToClipboard() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_share, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
            when (item?.itemId) {
                android.R.id.home -> {
                    finish()
                    true
                }
                R.id.action_share -> {
                    shareWallet()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    private fun copyPublicKeyToClipboard(){
        val label = getString(R.string.crypto_public_key, wallet.crypto.fullName)
        wallet.address.copyToClipboard(label)
        showMessage(R.string.copied, MessageType.SUCCESS)
    }

    private fun shareWallet(){
        wallet.share(this)
    }

}