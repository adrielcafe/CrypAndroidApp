package cafe.adriel.cryp.view.wallet.add

import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.view.BaseView

interface AddWalletView : BaseView {

    fun showValidatingDialog(wallet: Wallet)
    fun hideValidatingDialog()
    fun close()

}