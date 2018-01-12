package cafe.adriel.cryp.model.repository

import cafe.adriel.cryp.Const
import cafe.adriel.cryp.model.entity.Cryptocurrency
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.entity.response.BalanceResponse
import cafe.adriel.cryp.now
import io.paperdb.Paper
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import khronos.Dates
import khronos.minus
import khronos.minute
import retrofit2.http.GET
import retrofit2.http.Query

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

    fun getAll() = walletsDb.allKeys.map { walletsDb.read<Wallet>(it) }

    fun getById(id: String) = walletsDb.read<Wallet>(id)

    fun addOrUpdate(wallet: Wallet) =
            walletsDb.write(wallet.id, wallet)
                    .contains(wallet.id)

    fun remove(wallet: Wallet) =
            walletsDb.delete(wallet.id)
                    .let { !walletsDb.contains(wallet.id) }

    fun updatePrices(wallets: List<Wallet>) =
            if (wallets.isNotEmpty()) {
                with(wallets) {
                    val cryptocurrencies = wallets.map { it.cryptocurrency.name }.toSet()
                    val currencies = listOf(
                            Cryptocurrency.BTC.name,
                            Cryptocurrency.ETH.name,
                            PreferenceRepository.getCurrency().currencyCode.toUpperCase())
                    if(currencies.isNotEmpty() && cryptocurrencies.isNotEmpty()) {
                        priceService.getPrices(
                                cryptocurrencies.joinToString(","),
                                currencies.joinToString(","))
                    } else {
                        Observable.fromCallable { emptyMap<Cryptocurrency, Map<String, String>>() }
                    }
                }
            } else {
                Observable.fromCallable { emptyMap<Cryptocurrency, Map<String, String>>() }
            }

    fun updateBalances(wallets: List<Wallet>) =
            wallets.toObservable()
                    .onExceptionResumeNext {  }
                    .flatMap {
                        // 1 min interval before updateBalance balances
                        // to avoid API rate limits and socket timeout
                        val canUpdate = it.updatedAt == null || it.updatedAt?.before(Dates.now() - 1.minute) == true
                        if(canUpdate){
                            updateBalance(it)
                        } else {
                            Observable.fromCallable { it }
                        }
                    }
                    .toList()

    fun updateBalance(wallet: Wallet) =
            walletService.getBalance(wallet.cryptocurrency.name.toLowerCase(), wallet.address)
                    .map {
                        wallet.apply {
                            balance = it.balance
                            updatedAt = Dates.now()
                        }
                    }

    fun contains(wallet: Wallet) = walletsDb.contains(wallet.id)

    interface WalletService {
        @GET("address_balance/fallback/")
        fun getBalance(
                @Query("currency") cryptocurrency: String,
                @Query("address") publicKey: String):
                Observable<BalanceResponse>
    }

    interface PriceService {
        @GET("pricemulti")
        fun getPrices(
                @Query("fsyms") cryptocurrencies: String,
                @Query("tsyms") currencies: String):
                Observable<Map<Cryptocurrency, Map<String, String>>>
    }
}