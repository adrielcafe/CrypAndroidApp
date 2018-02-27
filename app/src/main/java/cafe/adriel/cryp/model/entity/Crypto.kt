package cafe.adriel.cryp.model.entity

import android.annotation.SuppressLint
import cafe.adriel.cryp.App
import com.squareup.moshi.Json
import io.mironov.smuggler.AutoParcelable

@SuppressLint("ParcelCreator")
data class Crypto(
        @Json(name = "Symbol")
        val symbol: String,
        @Json(name = "CoinName")
        val name: String,
        @Json(name = "IsTrading")
        val isTrading: Boolean = false) : AutoParcelable {

    val fullName = "$name ($symbol)"
    val logoResId = App.context.resources.getIdentifier(
                "logo_${symbol.toLowerCase()}", "drawable", App.context.packageName)

    fun hasLogo() = logoResId > 0

}