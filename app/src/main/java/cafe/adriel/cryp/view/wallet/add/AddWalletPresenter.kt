package cafe.adriel.cryp.view.wallet.add

import cafe.adriel.cryp.Const
import cafe.adriel.cryp.R
import cafe.adriel.cryp.model.entity.Crypto
import cafe.adriel.cryp.model.entity.MessageType
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.repository.WalletRepository
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import java.math.BigDecimal
import java.util.*

@InjectViewState
class AddWalletPresenter : MvpPresenter<AddWalletView>() {

    fun saveWallet(id: String, crypto: Crypto, publicKey: String, name: String, balance: BigDecimal?){
        val walletId = if(id.isNotEmpty()) id else UUID.randomUUID().toString()
        val wallet = Wallet(walletId, crypto, publicKey, name, balance ?: BigDecimal.ONE.negate())
        WalletRepository.addOrUpdate(wallet)
        viewState.showMessage(R.string.wallet_saved, MessageType.SUCCESS)
        viewState.close()

//        val exists = WalletRepository.contains(wallet)
//        if(exists && crypto.autoRefresh) {
//            wallet.balance = WalletRepository.getById(wallet.id).balance
//        }
//        if(!exists && crypto.autoRefresh) {
//            viewState.showProgressDialog(
//                wallet.crypto.fullName,
//                stringFrom(R.string.validating_public_key)
//            )
//            WalletRepository.updateBalance(wallet)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    viewState.hideProgressDialog()
//                    if (WalletRepository.contains(wallet)) {
//                        viewState.showMessage(R.string.wallet_saved, MessageType.SUCCESS)
//                        viewState.close()
//                    } else {
//                        viewState.showMessage(
//                            stringFrom(
//                                R.string.invalid_public_key,
//                                crypto.fullName
//                            ), MessageType.ERROR
//                        )
//                    }
//                }, {
//                    it.printStackTrace()
//                    viewState.hideProgressDialog()
//                    when (it) {
//                        is SocketTimeoutException ->
//                            viewState.showMessage(
//                                stringFrom(R.string.server_unavailable_try_later),
//                                MessageType.ERROR
//                            )
//                        else ->
//                            viewState.showMessage(
//                                stringFrom(
//                                    R.string.invalid_public_key,
//                                    crypto.fullName
//                                ), MessageType.ERROR
//                            )
//                    }
//                })
//        } else {
//        }
    }

    fun hasWalletSlotRemaining() = WalletRepository.count() < Const.WALLET_SLOTS_FREE

}