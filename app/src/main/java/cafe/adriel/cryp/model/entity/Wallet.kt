package cafe.adriel.cryp.model.entity

import android.annotation.SuppressLint
import cafe.adriel.cryp.Const
import cafe.adriel.cryp.getCryptoFormat
import cafe.adriel.cryp.getCurrencyFormat
import cafe.adriel.cryp.model.repository.PriceRepository
import io.mironov.smuggler.AutoParcelable
import java.math.BigDecimal

@SuppressLint("ParcelCreator")
data class Wallet(
    val id: String,
    val crypto: Crypto,
    val publicKey: String,
    var name: String = "",
    var balance: BigDecimal = BigDecimal.ONE.negate(),
    var position: Int = Int.MAX_VALUE) : AutoParcelable {

    fun getBalanceMBtc() = balance * Const.BTC_TO_MBTC.toBigDecimal()

    fun getBalanceBits() = balance * Const.BTC_TO_BITS.toBigDecimal()

    fun getBalanceSatoshi() = (balance * Const.BTC_TO_SATOSHI.toBigDecimal()).toLong()

    fun getBalanceBtc() = balance * PriceRepository.getBySymbol(crypto.symbol).priceBtc

    fun getBalanceEth() = balance * PriceRepository.getBySymbol(crypto.symbol).priceEth

    fun getBalanceCurrency() = balance * PriceRepository.getBySymbol(crypto.symbol).priceCurrency

    fun getFormattedBalanceBtc() = getCryptoFormat().format(balance)

    fun getFormattedBalanceMBtc() = getCryptoFormat().format(getBalanceMBtc())

    fun getFormattedBalanceBits() = getCryptoFormat().format(getBalanceBits())

    fun getFormattedBalanceSatoshi() = getCryptoFormat().format(getBalanceSatoshi())

    fun getFormattedBalanceCurrency() = getCurrencyFormat().format(getBalanceCurrency())

}