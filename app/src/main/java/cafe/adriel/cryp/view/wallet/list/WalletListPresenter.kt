package cafe.adriel.cryp.view.wallet.list

import cafe.adriel.cryp.R
import cafe.adriel.cryp.model.entity.*
import cafe.adriel.cryp.model.entity.Currency
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
                    BiFunction { wallets: List<Wallet>, prices: Map<String, Map<String, BigDecimal>> ->
                        wallets.forEach {
                            if(prices.containsKey(it.coin.name)){
                                it.priceBtc = prices[it.coin.name]?.get(Coin.BTC.name) ?: BigDecimal.ZERO
                                it.priceCurrency = prices[it.coin.name]?.get(Currency.USD.name) ?: BigDecimal.ZERO
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
        PreferenceRepository.setWalletOrder(walletIds.toTypedArray())

    fun saveCoinFormat(coinFormat: CoinFormat) =
        PreferenceRepository.setCoinFormat(coinFormat)

    fun getCoinFormat() =
        PreferenceRepository.getCoinFormat()

    private fun getSortComparator() = object : Comparator<Wallet> {
        private val order = PreferenceRepository.getWalletOrder()

        override fun compare(w1: Wallet, w2: Wallet) =
                order.indexOf(w1.id).compareTo(order.indexOf(w2.id))
    }

}