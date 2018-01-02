package cafe.adriel.cryp.model.entity

import android.annotation.SuppressLint
import io.mironov.smuggler.AutoParcelable
import java.math.BigDecimal
import java.util.*

@SuppressLint("ParcelCreator")
data class Wallet(
        val coin: Coin,
        val address: String,
        var balance: BigDecimal = BigDecimal.ONE.negate(),
        var priceBtc: BigDecimal = BigDecimal.ZERO,
        var priceCurrency: BigDecimal = BigDecimal.ZERO,
        var updatedAt: Date? = null) : AutoParcelable {

    private val BTC_TO_MBTC_MULTIPLIER = 1_000.toBigDecimal()
    private val BTC_TO_BITS_MULTIPLIER = 1_000_000.toBigDecimal()
    private val BTC_TO_SATOSHI_MULTIPLIER = 100_000_000.toBigDecimal()

    // Compound Key
    val id = "${coin.name}:$address"

    fun getBalanceMBtc() = balance * BTC_TO_MBTC_MULTIPLIER

    fun getBalanceBits() = balance * BTC_TO_BITS_MULTIPLIER

    fun getBalanceSatoshi() = (balance * BTC_TO_SATOSHI_MULTIPLIER).toLong()

    fun getBalanceCurrency() = balance * priceCurrency

}