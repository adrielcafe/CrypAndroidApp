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
import java.math.BigDecimal
import java.net.SocketTimeoutException

@InjectViewState
class AddWalletPresenter : MvpPresenter<AddWalletView>() {

    fun saveWallet(cryptocurrency: Cryptocurrency, publicKey: String, name: String, balance: BigDecimal?){
        val wallet = Wallet(cryptocurrency, publicKey, name, balance ?: BigDecimal.ONE.negate())
        val exists = WalletRepository.contains(wallet)
        // TODO prices should be kept outside wallet object
        if(exists) {
            if(cryptocurrency.autoRefresh)
                wallet.balance = WalletRepository.getById(wallet.id).balance
            wallet.priceCurrency = WalletRepository.getById(wallet.id).priceCurrency
            wallet.priceBtc = WalletRepository.getById(wallet.id).priceBtc
            wallet.priceEth = WalletRepository.getById(wallet.id).priceEth
        }
        if(!exists && cryptocurrency.autoRefresh) {
            viewState.showProgressDialog(
                wallet.cryptocurrency.toString(),
                stringFrom(R.string.validating_public_key)
            )
            WalletRepository.updateBalance(wallet)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.hideProgressDialog()
                    if (WalletRepository.contains(wallet)) {
                        viewState.showMessage(R.string.wallet_saved, MessageType.SUCCESS)
                        viewState.close()
                    } else {
                        viewState.showMessage(
                            stringFrom(
                                R.string.invalid_public_key,
                                cryptocurrency.fullName
                            ), MessageType.ERROR
                        )
                    }
                }, {
                    it.printStackTrace()
                    viewState.hideProgressDialog()
                    when (it) {
                        is SocketTimeoutException ->
                            viewState.showMessage(
                                stringFrom(R.string.server_unavailable_try_later),
                                MessageType.ERROR
                            )
                        else ->
                            viewState.showMessage(
                                stringFrom(
                                    R.string.invalid_public_key,
                                    cryptocurrency.fullName
                                ), MessageType.ERROR
                            )
                    }
                })
        } else {
            WalletRepository.addOrUpdate(wallet)
            viewState.showMessage(R.string.wallet_saved, MessageType.SUCCESS)
            viewState.close()
        }
    }

}