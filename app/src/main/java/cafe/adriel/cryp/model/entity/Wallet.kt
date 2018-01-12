package cafe.adriel.cryp.model.entity

import android.annotation.SuppressLint
import cafe.adriel.cryp.Const
import io.mironov.smuggler.AutoParcelable
import java.math.BigDecimal
import java.util.*

@SuppressLint("ParcelCreator")
data class Wallet(
        val cryptocurrency: Cryptocurrency,
        val address: String,
        var balance: BigDecimal = BigDecimal.ONE.negate(),
        var priceBtc: BigDecimal = BigDecimal.ZERO,
        var priceEth: BigDecimal = BigDecimal.ZERO,
        var priceCurrency: BigDecimal = BigDecimal.ZERO,
        var position: Int = Int.MAX_VALUE,
        var updatedAt: Date? = null) : AutoParcelable {

    // Compound Key
    val id = "${cryptocurrency.name}:$address"

    fun getBalanceMBtc() = balance * Const.BTC_TO_MBTC.toBigDecimal()

    fun getBalanceBits() = balance * Const.BTC_TO_BITS.toBigDecimal()

    fun getBalanceSatoshi() = (balance * Const.BTC_TO_SATOSHI.toBigDecimal()).toLong()

    fun getBalanceCurrency() = balance * priceCurrency

}