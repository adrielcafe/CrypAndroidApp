package cafe.adriel.cryp.model.repository

import cafe.adriel.cryp.Const
import cafe.adriel.cryp.model.entity.Coin
import cafe.adriel.cryp.model.entity.Currency
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.entity.response.BalanceResponse
import cafe.adriel.cryp.now
import io.paperdb.Paper
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import khronos.Dates
import khronos.minus
import khronos.minute
import khronos.seconds
import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigDecimal

object WalletRepository {
    private val walletsDb by lazy {
        Paper.book(Const.DB_WALLETS)
    }
    private val walletService by lazy {
        ServiceFactory.newInstance<WalletService>(Const.WALLET_API_BASE_URL)
    }
    private val priceService by lazy {
        ServiceFactory.newInstance<PriceService>(Const.PRICE_API_BASE_URL)
    }

    fun getAll() =
            walletsDb.allKeys.map { walletsDb.read<Wallet>(it) }

    fun getById(id: String) =
        getAll().firstOrNull { it.id == id }

    fun addOrUpdate(wallet: Wallet) =
            walletsDb.write(wallet.id, wallet)
                    .contains(wallet.id)

    fun remove(wallet: Wallet) =
            walletsDb.delete(wallet.id)
                    .let { !walletsDb.contains(wallet.id) }

    fun updatePrices(wallets: List<Wallet>) =
            if (wallets.isNotEmpty()) {
                with(wallets) {
                    val currencies = listOf(Coin.BTC.name, Currency.USD.name)
                    val coins = mutableListOf<String>()
                    wallets.forEach {
                        // 30 sec interval before update prices
                        // to avoid API rate limits and socket timeout
                        val canUpdate = it.priceBtc == BigDecimal.ZERO
                                        || it.priceCurrency == BigDecimal.ZERO
                                        || it.updatedAt?.before(Dates.now() - 30.seconds) == true
                        if(canUpdate) {
                            coins.add(it.coin.name)
                        }
                    }
                    if(currencies.isNotEmpty() && coins.isNotEmpty()) {
                        priceService.getPrices(
                                coins.joinToString(","),
                                currencies.joinToString(","))
                    } else {
                        Observable.fromCallable { emptyMap<String, Map<String, BigDecimal>>() }
                    }
                }
            } else {
                Observable.fromCallable { emptyMap<String, Map<String, BigDecimal>>() }
            }

    fun updateBalances(wallets: List<Wallet>) =
            wallets.toObservable()
                    .onExceptionResumeNext {  }
                    .flatMap {
                        // 1 min interval before update balances
                        // to avoid API rate limits and socket timeout
                        val canUpdate = it.updatedAt == null || it.updatedAt?.before(Dates.now() - 1.minute) == true
                        if(canUpdate){
                            update(it)
                        } else {
                            Observable.fromCallable { it }
                        }
                    }
                    .toList()

    fun update(wallet: Wallet) =
            walletService.getBalance(wallet.coin.name.toLowerCase(), wallet.address)
                    .map {
                        wallet.apply {
                            balance = it.balance
                            updatedAt = Dates.now()
                        }
                    }

    fun contains(wallet: Wallet) =
            walletsDb.contains(wallet.id)

    interface WalletService {
        @GET("address_balance/fallback/")
        fun getBalance(
                @Query("currency") coin: String,
                @Query("address") publicKey: String):
                Observable<BalanceResponse>
    }

    interface PriceService {
        @GET("pricemulti")
        fun getPrices(
                @Query("fsyms") coins: String,
                @Query("tsyms") currencies: String):
                Observable<Map<String, Map<String, BigDecimal>>>
    }
}