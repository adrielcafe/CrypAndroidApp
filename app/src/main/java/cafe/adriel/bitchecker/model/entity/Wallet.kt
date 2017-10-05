package cafe.adriel.bitchecker.model.entity

import android.annotation.SuppressLint
import io.mironov.smuggler.AutoParcelable
import khronos.Dates
import java.util.*

@SuppressLint("ParcelCreator")
data class Wallet(
        val coin: Coin,
        val address: String,
        var name: String,
        var balance: Long = -1,
        var updatedAt: Date = Dates.today) : AutoParcelable {

    // Compound Key
    val id = "$coin:$address"

}