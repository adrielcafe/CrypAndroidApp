package cafe.adriel.cryp.model.entity

import android.annotation.SuppressLint
import cafe.adriel.cryp.Const
import cafe.adriel.cryp.model.repository.PriceRepository
import io.mironov.smuggler.AutoParcelable
import java.math.BigDecimal

@SuppressLint("ParcelCreator")
data class Wallet(
    val crypto: Crypto,
    val address: String,
    var name: String = "",
    var balance: BigDecimal = BigDecimal.ONE.negate(),
    var position: Int = Int.MAX_VALUE) : AutoParcelable {

    // Compound Key
    val id = "${crypto.symbol}:$address"

    fun getBalanceMBtc() = balance * Const.BTC_TO_MBTC.toBigDecimal()

    fun getBalanceBits() = balance * Const.BTC_TO_BITS.toBigDecimal()

    fun getBalanceSatoshi() = (balance * Const.BTC_TO_SATOSHI.toBigDecimal()).toLong()

    fun getBalanceBtc() = balance * PriceRepository.getBySymbol(crypto.symbol).priceBtc

    fun getBalanceEth() = balance * PriceRepository.getBySymbol(crypto.symbol).priceEth

    fun getBalanceCurrency() = balance * PriceRepository.getBySymbol(crypto.symbol).priceCurrency

}