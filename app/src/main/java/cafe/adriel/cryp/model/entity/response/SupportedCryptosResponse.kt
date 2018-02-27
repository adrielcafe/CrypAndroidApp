package cafe.adriel.cryp.model.entity.response

import android.annotation.SuppressLint
import cafe.adriel.cryp.model.entity.Crypto
import com.squareup.moshi.Json
import io.mironov.smuggler.AutoParcelable

@SuppressLint("ParcelCreator")
data class SupportedCryptosResponse(
        @Json(name = "Data")
        val data: Map<String, Crypto>) : AutoParcelable