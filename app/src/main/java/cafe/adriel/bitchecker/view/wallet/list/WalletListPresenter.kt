package cafe.adriel.bitchecker.view.wallet.list

import cafe.adriel.bitchecker.model.entity.Wallet
import cafe.adriel.bitchecker.model.repository.WalletRepository
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import java.util.*

@InjectViewState
class WalletListPresenter: MvpPresenter<WalletListView>() {

    fun delete(wallet: Wallet) {
        if(WalletRepository.remove(wallet)){
            viewState.remove(wallet)
        }
    }

    fun saveOrder(walletIds: List<String>) {
        WalletRepository.setOrder(walletIds.toTypedArray())
    }

    fun loadAll() =
            WalletRepository.getAll()
                    .flatMapIterable { it }
                    .sorted(getSortComparator())
                    .toList()
                    .toObservable()
                // TODO don't need to update the wallets on pre-alpha phase
//                .map { WalletRepository.updateAll(it) }
//                .flatMap { it.toObservable() }

    private fun getSortComparator() = object : Comparator<Wallet> {
        private val order = WalletRepository.getOrder()
        override fun compare(w1: Wallet?, w2: Wallet?) =
                order.indexOf(w1?.id)
                    .compareTo(order.indexOf(w2?.id))
    }

}