package cafe.adriel.cryp.model.entity

import android.annotation.SuppressLint
import io.mironov.smuggler.AutoParcelable
import java.math.BigDecimal
import java.util.*

@SuppressLint("ParcelCreator")
data class Prices(
        val cryptocurrency: Cryptocurrency,
        var priceBtc: BigDecimal = BigDecimal.ZERO,
        var priceEth: BigDecimal = BigDecimal.ZERO,
        var priceCurrency: BigDecimal = BigDecimal.ZERO,
        var updatedAt: Date? = null) : AutoParcelable {

    val id = cryptocurrency.name

}