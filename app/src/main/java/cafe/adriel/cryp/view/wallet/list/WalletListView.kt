package cafe.adriel.cryp.view.wallet.list

import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.view.IView

interface WalletListView: IView {

    fun remove(wallet: Wallet)

}