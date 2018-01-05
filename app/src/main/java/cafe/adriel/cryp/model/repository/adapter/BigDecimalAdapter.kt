package cafe.adriel.cryp.model.repository.adapter

import cafe.adriel.cryp.Const
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode 

class BigDecimalAdapter {

    @ToJson
    fun toJson(value: BigDecimal) =
            value.toPlainString()

    @FromJson
    fun fromJson(value: String) =
        // Satoshi format
        if (value.endsWith(".0") || !value.contains('.'))
            BigDecimal(value, MathContext.DECIMAL128)
                    .setScale(0, RoundingMode.DOWN) / Const.BTC_TO_SATOSHI.toBigDecimal()
        // BTC format
        else
            BigDecimal(value, MathContext.DECIMAL128)
                    .setScale(8, RoundingMode.DOWN)

}