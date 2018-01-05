package cafe.adriel.cryp.view.wallet.list

import cafe.adriel.cryp.R
import cafe.adriel.cryp.model.entity.Cryptocurrency
import cafe.adriel.cryp.model.entity.MessageType
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.repository.PreferenceRepository
import cafe.adriel.cryp.model.repository.WalletRepository
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.toObservable
import java.math.BigDecimal
import java.util.*

@InjectViewState
class WalletListPresenter: MvpPresenter<WalletListView>() {

    fun loadAll() = WalletRepository.getAll()
            .toObservable()
            .sorted(getSortComparator())
            .toList()
            .flatMap {
                viewState.addAll(it)
                WalletRepository.updateBalances(it)
            }
            .zipWith(WalletRepository.updatePrices(WalletRepository.getAll()).singleOrError(),
                    BiFunction { wallets: List<Wallet>, prices: Map<Cryptocurrency, Map<String, String>> ->
                        wallets.forEach {
                            if(prices.containsKey(it.cryptocurrency)){
                                it.priceBtc = prices[it.cryptocurrency]
                                        ?.get(Cryptocurrency.BTC.name)
                                        ?.toBigDecimal() ?: BigDecimal.ZERO
                                it.priceCurrency = prices[it.cryptocurrency]
                                        ?.get(PreferenceRepository.getCurrency().currencyCode.toUpperCase())
                                        ?.toBigDecimal() ?: BigDecimal.ZERO
                            }
                            WalletRepository.addOrUpdate(it)
                        }
                        wallets
                    }
            )
            .toObservable()

    fun exists(wallet: Wallet) =
            WalletRepository.contains(wallet)

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

    private fun getSortComparator() = Comparator<Wallet> { w1, w2 ->
        val result = w1.position.compareTo(w2.position)
        if(result == 0) w1.cryptocurrency.compareTo(w2.cryptocurrency)
        else result
    }

}