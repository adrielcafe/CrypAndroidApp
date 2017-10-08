package cafe.adriel.bitchecker.view.wallet.list

import cafe.adriel.bitchecker.model.entity.Wallet
import cafe.adriel.bitchecker.view.BaseView

interface WalletListView: BaseView {
    fun showAddActivity()
    fun showSettingsActivity()
    fun showAddressDialog(wallet: Wallet)
    fun showEditDialog(wallet: Wallet)
    fun showDeleteDialog(wallet: Wallet)
    fun addOrUpdate(wallet: Wallet)
    fun remove(wallet: Wallet)
    fun refresh()
    fun setRefreshing(refreshing: Boolean)
}