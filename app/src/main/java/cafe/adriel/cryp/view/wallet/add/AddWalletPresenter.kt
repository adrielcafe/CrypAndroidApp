package cafe.adriel.cryp.view.wallet.add

import cafe.adriel.cryp.R
import cafe.adriel.cryp.model.entity.Cryptocurrency
import cafe.adriel.cryp.model.entity.MessageType
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.repository.WalletRepository
import cafe.adriel.cryp.stringFrom
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.SocketTimeoutException

@InjectViewState
class AddWalletPresenter : MvpPresenter<AddWalletView>() {

    fun saveWallet(cryptocurrency: Cryptocurrency, publicKey: String){
        val wallet = Wallet(cryptocurrency, publicKey)
        viewState.showProgressDialog(wallet.cryptocurrency.toString(), stringFrom(R.string.validating_public_key))
        WalletRepository.updateBalance(wallet)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.hideProgressDialog()
                    when {
                        WalletRepository.contains(wallet) ->
                            viewState.showMessage(R.string.wallet_already_saved, MessageType.ERROR)
                        WalletRepository.addOrUpdate(wallet) -> {
                            viewState.showMessage(R.string.wallet_saved, MessageType.SUCCESS)
                            viewState.close()
                        }
                        else ->
                            viewState.showMessage(stringFrom(R.string.invalid_public_key, cryptocurrency.fullName), MessageType.ERROR)
                    }
                }, {
                    it.printStackTrace()
                    viewState.hideProgressDialog()
                    when (it) {
                        is SocketTimeoutException ->
                            viewState.showMessage(stringFrom(R.string.server_unavailable_try_later), MessageType.ERROR)
                        else ->
                            viewState.showMessage(stringFrom(R.string.invalid_public_key, cryptocurrency.fullName), MessageType.ERROR)
                    }
                })
    }

}