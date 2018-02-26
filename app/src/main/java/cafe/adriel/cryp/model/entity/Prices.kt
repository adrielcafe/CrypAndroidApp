package cafe.adriel.cryp.model.entity

import android.annotation.SuppressLint
import io.mironov.smuggler.AutoParcelable
import java.math.BigDecimal

@SuppressLint("ParcelCreator")
data class Prices(
        val cryptoSymbol: String,
        var priceBtc: BigDecimal = BigDecimal.ZERO,
        var priceEth: BigDecimal = BigDecimal.ZERO,
        var priceCurrency: BigDecimal = BigDecimal.ZERO) : AutoParcelable