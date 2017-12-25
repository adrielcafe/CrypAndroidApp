package cafe.adriel.cryp.view.wallet.list

import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.view.BaseView

interface WalletListView: BaseView {
    fun remove(wallet: Wallet)
}