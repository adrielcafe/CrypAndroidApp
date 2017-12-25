package cafe.adriel.cryp.view.wallet.list

import cafe.adriel.cryp.R
import cafe.adriel.cryp.model.entity.CoinFormat
import cafe.adriel.cryp.model.entity.MessageType
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.repository.PreferenceRepository
import cafe.adriel.cryp.model.repository.WalletRepository
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import java.util.*

@InjectViewState
class WalletListPresenter: MvpPresenter<WalletListView>() {

    fun delete(wallet: Wallet) {
        if(WalletRepository.remove(wallet)){
            viewState.remove(wallet)
            viewState.showMessage(R.string.wallet_removed, MessageType.SUCCESS)
        }
    }

    fun saveOrder(walletIds: List<String>) =
        PreferenceRepository.setWalletOrder(walletIds.toTypedArray())

    fun saveCoinFormat(coinFormat: CoinFormat) =
        PreferenceRepository.setCoinFormat(coinFormat)

    fun getCoinFormat() =
        PreferenceRepository.getCoinFormat()

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
        private val order = PreferenceRepository.getWalletOrder()
        override fun compare(w1: Wallet?, w2: Wallet?) =
                order.indexOf(w1?.id)
                    .compareTo(order.indexOf(w2?.id))
    }

}