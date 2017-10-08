package cafe.adriel.bitchecker.view.wallet.list

import cafe.adriel.bitchecker.model.entity.Wallet
import cafe.adriel.bitchecker.model.repository.WalletRepository
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class WalletListPresenter: MvpPresenter<WalletListView>() {

    fun delete(wallet: Wallet) {
        if(WalletRepository.remove(wallet)){
            viewState.remove(wallet)
        }
    }

    fun loadAll() =
            WalletRepository.getAll()
                // TODO don't need to update the wallets on pre-alpha phase
//                .map { WalletRepository.updateAll(it) }
//                .flatMap { it.toObservable() }

}