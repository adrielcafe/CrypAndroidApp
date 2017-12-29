package cafe.adriel.cryp.model.entity.response

import android.annotation.SuppressLint
import com.squareup.moshi.Json
import io.mironov.smuggler.AutoParcelable

@SuppressLint("ParcelCreator")
data class BalanceResponse(
        @Json(name = "balance")
        var balance: Double = 0.0) : AutoParcelable