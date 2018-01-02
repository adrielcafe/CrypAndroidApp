package cafe.adriel.cryp.model.entity.response

import android.annotation.SuppressLint
import com.squareup.moshi.Json
import io.mironov.smuggler.AutoParcelable
import java.math.BigDecimal

@SuppressLint("ParcelCreator")
data class BalanceResponse(
        @Json(name = "balance")
        val balance: BigDecimal = BigDecimal.ONE.negate()) : AutoParcelable