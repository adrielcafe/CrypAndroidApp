package cafe.adriel.cryp.model.repository

import cafe.adriel.cryp.Const
import cafe.adriel.cryp.model.entity.CoinFormat
import io.paperdb.Paper

object PreferenceRepository {
    private val prefDb by lazy {
        Paper.book(Const.DB_PREFERENCES)
    }

    fun getWalletOrder() =
            prefDb.read<Array<String>>(Const.PREF_WALLET_ORDER, arrayOf())

    fun setWalletOrder(order: Array<String>) =
            prefDb.write(Const.PREF_WALLET_ORDER, order)
                    .contains(Const.PREF_WALLET_ORDER)

    fun getCoinFormat() =
            prefDb.read<CoinFormat>(Const.PREF_COIN_FORMAT, CoinFormat.BTC)

    fun setCoinFormat(coinFormat: CoinFormat) =
            prefDb.write(Const.PREF_COIN_FORMAT, coinFormat)
                    .contains(Const.PREF_COIN_FORMAT)

}