package cafe.adriel.cryp.model.repository

import cafe.adriel.cryp.Const
import cafe.adriel.cryp.model.entity.Cryptocurrency
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
        ServiceFactory.newInstance<PriceService>(Const.PRICE_API_BASE_URL)
    }

    fun getAll() = priceDb.allKeys.map { priceDb.read<Prices>(it) }

    fun getById(id: Cryptocurrency) =
        if(contains(id)) priceDb.read<Prices>(id.name)
        else Prices(id)

    fun addOrUpdate(prices: Prices) = priceDb.write(prices.id, prices)

    fun remove(id: Cryptocurrency) = priceDb.delete(id.name).let { !priceDb.contains(id.name) }

    fun contains(id: Cryptocurrency) = priceDb.contains(id.name)

    fun updatePrices(): Observable<List<Prices>> =
        WalletRepository.getAll().let {
            val cryptocurrencies = it.map { it.cryptocurrency.name }
                .toSet()
                .joinToString(",")
            val currencies = listOf(
                    Cryptocurrency.BTC.name,
                    Cryptocurrency.ETH.name,
                    PreferenceRepository.getCurrency().currencyCode.toUpperCase()
                ).joinToString(",")
            if (currencies.isNotEmpty() && cryptocurrencies.isNotEmpty()) {
                priceService.getPrices(cryptocurrencies, currencies)
                        .map {
                            val allPrices = mutableListOf<Prices>()
                            it.forEach { cryptocurrency, mapPrices ->
                                val priceBtc = mapPrices
                                    ?.get(Cryptocurrency.BTC.name)
                                    ?.toBigDecimal() ?: BigDecimal.ZERO
                                val priceEth = mapPrices
                                    ?.get(Cryptocurrency.ETH.name)
                                    ?.toBigDecimal() ?: BigDecimal.ZERO
                                val priceCurrency = mapPrices
                                    ?.get(PreferenceRepository.getCurrency().currencyCode.toUpperCase())
                                    ?.toBigDecimal() ?: BigDecimal.ZERO
                                val prices = Prices(cryptocurrency, priceBtc, priceEth, priceCurrency)
                                allPrices.add(prices)
                                addOrUpdate(prices)
                            }
                            allPrices
                        }
            } else {
                Observable.empty()
            }
        }

    // https://cryptocompare.com/api/
    interface PriceService {
        @GET("pricemulti")
        fun getPrices(
                @Query("fsyms") cryptocurrencies: String,
                @Query("tsyms") currencies: String):
                Observable<Map<Cryptocurrency, Map<String, String>>>
    }
}