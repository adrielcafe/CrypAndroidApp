package cafe.adriel.cryp.view.wallet.list

import cafe.adriel.cryp.R
import cafe.adriel.cryp.model.entity.MessageType
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.repository.PriceRepository
import cafe.adriel.cryp.model.repository.WalletRepository
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class WalletListPresenter: MvpPresenter<WalletListView>() {

    fun loadAll() =
            WalletRepository.getAll()
                .sortedWith(Comparator { w1, w2 ->
                    val result = w1.position.compareTo(w2.position)
                    if(result == 0) w1.cryptocurrency.compareTo(w2.cryptocurrency)
                    else result
                })

    fun updateAll() =
        PriceRepository.updatePrices()
            .defaultIfEmpty(emptyList())
            .map { WalletRepository.updateBalances() }
            .flatMapSingle { it }

    fun exists(wallet: Wallet) = WalletRepository.contains(wallet)

    fun delete(wallet: Wallet) {
        if(WalletRepository.remove(wallet)){
            viewState.remove(wallet)
            viewState.showMessage(R.string.wallet_removed, MessageType.SUCCESS)
        }
    }

    fun saveOrder(walletIds: List<String>) =
        WalletRepository.getAll().forEach {
            it.position = walletIds.indexOf(it.id)
            WalletRepository.addOrUpdate(it)
        }

}