package cafe.adriel.cryp.model.repository

import cafe.adriel.cryp.Const
import cafe.adriel.cryp.model.entity.Crypto
import cafe.adriel.cryp.model.entity.response.SupportedCryptosResponse
import cafe.adriel.cryp.model.repository.factory.ServiceFactory
import cafe.adriel.cryp.now
import io.paperdb.Paper
import io.reactivex.Observable
import khronos.days
import khronos.minus
import retrofit2.http.GET
import java.util.*

object CryptoRepository {
    private val cryptoDb by lazy {
        Paper.book(Const.DB_CRYPTOS)
    }
    private val cryptoService by lazy {
        ServiceFactory.newInstance<CryptoService>(Const.CRYPTO_PRICE_API_BASE_URL)
    }

    fun getAll(): Observable<List<Crypto>> {
        val symbols = cryptoDb.allKeys
        val lastModified = Date(cryptoDb.lastModified("BTC"))
        // Download list of supported cryptos every week
        return if(symbols.isEmpty() || lastModified.before(now() - 7.days)){
            cryptoService.getSupportedCryptos()
                .map {
                    cryptoDb.destroy()
                    it.data.values
                        .filter { it.isTrading }
                        .apply {
                            forEach { cryptoDb.write(it.symbol, it) }
                        }
                }
        } else {
            Observable.fromCallable { symbols.map { cryptoDb.read<Crypto>(it) } }
        }
    }

    fun contains(symbol: String) = cryptoDb.contains(symbol)

    // https://min-api.cryptocompare.com
    interface CryptoService {
        @GET("all/coinlist")
        fun getSupportedCryptos():
                Observable<SupportedCryptosResponse>
    }
}