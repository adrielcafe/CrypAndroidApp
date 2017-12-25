package cafe.adriel.cryp.model.entity

import android.annotation.SuppressLint
import io.mironov.smuggler.AutoParcelable
import khronos.Dates
import java.util.*

@SuppressLint("ParcelCreator")
data class Wallet(
        val coin: Coin,
        val address: String,
        var balance: Double = -1.0, // BTC format
        var updatedAt: Date = Dates.today) : AutoParcelable {

    // Compound Key
    val id = "$coin:$address"

    fun getBalanceMBtc() = balance * 1_000

    fun getBalanceUBtc() = balance * 1_000_000

    fun getBalanceSatoshi() = (balance * 100_000_000).toLong()

}