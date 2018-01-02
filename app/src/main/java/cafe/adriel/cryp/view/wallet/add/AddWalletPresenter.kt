package cafe.adriel.cryp.view.wallet.add

import cafe.adriel.cryp.R
import cafe.adriel.cryp.RefreshWalletListEvent
import cafe.adriel.cryp.model.entity.Coin
import cafe.adriel.cryp.model.entity.MessageType
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.repository.WalletRepository
import cafe.adriel.cryp.stringFrom
import cafe.adriel.kbus.KBus
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException

@InjectViewState
class AddWalletPresenter : MvpPresenter<AddWalletView>() {

    fun saveWallet(coin: Coin, publicKey: String){
        val wallet = Wallet(coin, publicKey)
        viewState.showValidatingDialog(wallet)
        WalletRepository.update(wallet)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.hideValidatingDialog()
                    when {
                        WalletRepository.contains(wallet) ->
                            viewState.showMessage(R.string.wallet_already_saved, MessageType.ERROR)
                        WalletRepository.addOrUpdate(wallet) -> {
                            KBus.post(RefreshWalletListEvent())
                            viewState.showMessage(R.string.wallet_saved, MessageType.SUCCESS)
                            viewState.close()
                        }
                        else ->
                            viewState.showMessage(stringFrom(R.string.invalid_public_key, coin.fullName), MessageType.ERROR)
                    }
                }, {
                    it.printStackTrace()
                    viewState.hideValidatingDialog()
                    when (it) {
                        is SocketTimeoutException ->
                            viewState.showMessage(stringFrom(R.string.server_unavailable_try_later), MessageType.ERROR)
                        else ->
                            viewState.showMessage(stringFrom(R.string.invalid_public_key, coin.fullName), MessageType.ERROR)
                    }
                })
    }

}