package cafe.adriel.cryp.model.repository

import cafe.adriel.cryp.BuildConfig
import cafe.adriel.cryp.Const
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.entity.response.BalanceResponse
import io.paperdb.Paper
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import khronos.Dates
import retrofit2.http.GET
import retrofit2.http.Path

object WalletRepository {
    private val walletsDb by lazy {
        Paper.book(Const.DB_WALLETS)
    }
    private val service by lazy {
        ServiceFactory.newInstance(
                BuildConfig.WALLET_API_BASE_URL, WalletService::class)
    }

    fun getAll() =
            Observable.fromCallable {
                walletsDb.allKeys.map { walletsDb.read<Wallet>(it) }
            }

    fun addOrUpdate(wallet: Wallet) =
            walletsDb.write(wallet.id, wallet)
                    .contains(wallet.id)

    fun remove(wallet: Wallet) =
            walletsDb.delete(wallet.id)
                    .let { !walletsDb.contains(wallet.id) }

    fun updateAll(wallets: List<Wallet>) =
            wallets.toObservable()
                    .flatMap { update(it) }
                    .toList()

    fun update(wallet: Wallet) =
            service.getBalance(wallet.coin.name.toLowerCase(), wallet.address)
                    .map {
                        with(wallet){
                            balance = (it.balance / 100_000_000).toDouble()
                            updatedAt = Dates.today
                        }
                        walletsDb.write(wallet.id, wallet)
                        wallet
                    }

    fun contains(wallet: Wallet) =
            walletsDb.contains(wallet.id)

    interface WalletService {
        @GET("{coin}/main/addrs/{address}/balance")
        fun getBalance(
                @Path("coin") coin: String,
                @Path("address") publicKey: String):
                Observable<BalanceResponse>
    }
}