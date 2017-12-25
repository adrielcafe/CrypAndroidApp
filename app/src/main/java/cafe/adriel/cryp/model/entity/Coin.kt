package cafe.adriel.cryp.model.entity

import android.graphics.drawable.Drawable
import cafe.adriel.cryp.R
import cafe.adriel.cryp.colorFrom
import cafe.adriel.cryp.drawableFrom

enum class Coin(val fullName: String,
                val color: Int,
                val logo: Drawable) {

    BTC("Bitcoin",
        colorFrom(R.color.buttercup),
        drawableFrom(R.drawable.logo_bitcoin)),

    LTC("Litecoin",
        colorFrom(R.color.cascade),
        drawableFrom(R.drawable.logo_litecoin)),

    ETH("Ethereum",
        colorFrom(R.color.pickled_bluewood),
        drawableFrom(R.drawable.logo_ether)),

    DASH("Dash",
        colorFrom(R.color.summer_sky),
        drawableFrom(R.drawable.logo_dash)),

    DOGE("Dogecoin",
        colorFrom(R.color.sandstorm),
        drawableFrom(R.drawable.logo_dogecoin));

    override fun toString() = "$fullName ($name)"

}