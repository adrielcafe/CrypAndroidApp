package cafe.adriel.cryp.model.repository

import cafe.adriel.cryp.Const
import cafe.adriel.cryp.model.entity.Prices
import cafe.adriel.cryp.model.repository.factory.ServiceFactory
import io.paperdb.Paper
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigDecimal

object PriceRepository {
    private val priceDb by lazy {
        Paper.book(Const.DB_PRICES)
    }
    private val priceService by lazy {
        ServiceFactory.newInstance<PriceService>(Const.CRYPTO_PRICE_API_BASE_URL)
    }

    fun getBySymbol(symbol: String) =
        if(contains(symbol)) priceDb.read<Prices>(symbol)
        else Prices(symbol)

    private fun addOrUpdate(prices: Prices) = priceDb.write(prices.cryptoSymbol, prices)

    fun contains(symbol: String) = priceDb.contains(symbol)

    fun updatePrices(): Observable<List<Prices>> =
        WalletRepository.getAll().let {
            val cryptos = it.map { it.crypto.symbol }
                .toSet()
                .joinToString(",")
            val currencies = listOf(
                    "BTC",
                    "ETH",
                    PreferenceRepository.getCurrency().currencyCode.toUpperCase()
                ).joinToString(",")
            return if (cryptos.isNotEmpty()) {
                priceService.getPrices(cryptos, currencies)
                        .map {
                            val allPrices = mutableListOf<Prices>()
                            it.forEach { crypto, mapPrices ->
                                val priceBtc = mapPrices
                                    ?.get("BTC")
                                    ?.toBigDecimal() ?: BigDecimal.ZERO
                                val priceEth = mapPrices
                                    ?.get("ETH")
                                    ?.toBigDecimal() ?: BigDecimal.ZERO
                                val priceCurrency = mapPrices
                                    ?.get(PreferenceRepository.getCurrency().currencyCode.toUpperCase())
                                    ?.toBigDecimal() ?: BigDecimal.ZERO
                                val prices = Prices(crypto, priceBtc, priceEth, priceCurrency)
                                allPrices.add(prices)
                                addOrUpdate(prices)
                            }
                            allPrices
                        }
            } else {
                Observable.empty()
            }
        }

    // https://min-api.cryptocompare.com
    interface PriceService {
        @GET("pricemulti")
        fun getPrices(
            @Query("fsyms") cryptos: String,
            @Query("tsyms") currencies: String):
                Observable<Map<String, Map<String, String>>>
    }
}