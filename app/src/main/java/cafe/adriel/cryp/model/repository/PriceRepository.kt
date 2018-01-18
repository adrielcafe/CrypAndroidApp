package cafe.adriel.cryp.model.repository

import cafe.adriel.cryp.Const
import cafe.adriel.cryp.model.entity.Cryptocurrency
import cafe.adriel.cryp.model.repository.factory.ServiceFactory
import io.paperdb.Paper
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

object PriceRepository {
    private val priceDb by lazy {
        Paper.book(Const.DB_PRICES)
    }
    private val priceService by lazy {
        ServiceFactory.newInstance<PriceService>(Const.PRICE_API_BASE_URL)
    }

    fun getAll() =
        mutableMapOf<Cryptocurrency, Map<String, String>>().apply {
            priceDb.allKeys.map {
                put(Cryptocurrency.valueOf(it), priceDb.read<Map<String, String>>(it)) }
        }

    fun getById(id: Cryptocurrency) = priceDb.read<Map<String, String>>(id.name)

    fun addOrUpdate(id: Cryptocurrency, prices: Map<String, String>) = priceDb.write(id.name, prices)

    fun remove(id: Cryptocurrency) = priceDb.delete(id.name).let { !priceDb.contains(id.name) }

    fun contains(id: Cryptocurrency) = priceDb.contains(id.name)

    fun updatePrices() =
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
                            it.apply { forEach { addOrUpdate(it.key, it.value) } }
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