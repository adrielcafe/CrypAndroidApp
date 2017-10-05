package cafe.adriel.bitchecker.model.repository

import cafe.adriel.bitchecker.BuildConfig
import cafe.adriel.bitchecker.model.entity.Wallet
import cafe.adriel.bitchecker.model.entity.response.BalanceResponse
import io.paperdb.Paper
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import khronos.Dates
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*
import java.util.concurrent.TimeUnit

object WalletRepository {
    private val walletsDb by lazy {
        Paper.book("wallets")
    }
    private val service by lazy {
        ServiceFactory.newInstance(
                BuildConfig.WALLET_API_BASE_URL, WalletService::class)
    }

    fun getAll() =
            Observable.fromCallable {
                walletsDb.allKeys.map { walletsDb.read<Wallet>(it) }
            }

    fun add(wallet: Wallet) =
            walletsDb.write(wallet.id, wallet)
                    .exist(wallet.id)

    fun remove(wallet: Wallet) =
            walletsDb.delete(wallet.id)

    fun updateAll(wallets: List<Wallet>) =
            wallets.toObservable()
                    .flatMap { update(it) }
                    .toList()

    fun update(wallet: Wallet) =
            service.getBalance(wallet.coin.name.toLowerCase(), wallet.address)
                    .map {
                        with(wallet){
                            balance = it.balance
                            updatedAt = Dates.today
                        }
                        walletsDb.write(wallet.id, wallet)
                        wallet
                    }

    interface WalletService {
        @GET("{coin}/main/addrs/{address}/balance")
        fun getBalance(
                @Path("coin") coin: String,
                @Path("address") publicKey: String):
                Observable<BalanceResponse>
    }
}