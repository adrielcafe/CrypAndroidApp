package cafe.adriel.bitchecker.model.entity.response

import android.annotation.SuppressLint
import com.squareup.moshi.Json
import io.mironov.smuggler.AutoParcelable
import java.util.*

@SuppressLint("ParcelCreator")
data class BalanceResponse(
        @Json(name = "balance")
        var balance: Long) : AutoParcelable