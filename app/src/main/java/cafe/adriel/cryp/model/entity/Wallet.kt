package cafe.adriel.cryp.model.entity

import android.annotation.SuppressLint
import io.mironov.smuggler.AutoParcelable
import java.util.*

@SuppressLint("ParcelCreator")
data class Wallet(
        val coin: Coin,
        val address: String,
        var balance: Double = 0.0,
        var priceBtc: Double = 0.0,
        var priceCurrency: Double = 0.0,
        var updatedAt: Date? = null) : AutoParcelable {

    private val BTC_TO_MBTC_MULTIPLIER = 1_000
    private val BTC_TO_BITS_MULTIPLIER = 1_000_000
    private val BTC_TO_SATOSHI_MULTIPLIER = 100_000_000

    // Compound Key
    val id = "${coin.name}:$address"

    fun getBalanceMBtc() = balance * BTC_TO_MBTC_MULTIPLIER

    fun getBalanceBits() = balance * BTC_TO_BITS_MULTIPLIER

    fun getBalanceSatoshi() = (balance * BTC_TO_SATOSHI_MULTIPLIER).toLong()

    fun getBalanceCurrency() = balance * priceCurrency

}