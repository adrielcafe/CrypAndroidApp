package cafe.adriel.bitchecker.model.entity

import android.graphics.drawable.Drawable
import cafe.adriel.bitchecker.R
import cafe.adriel.bitchecker.getColor
import cafe.adriel.bitchecker.getDrawable

enum class Coin {
    BTC,
    LTC,
    ETH,
    DASH,
    DOGE;

    val color: Int
        get() = getColor(when(this){
            BTC -> R.color.buttercup
            LTC -> R.color.concrete
            ETH -> R.color.wet_asphalt
            DASH -> R.color.summer_sky
            DOGE -> R.color.sandstorm
        })
    val logo: Drawable
        get() = getDrawable(when(this){
            BTC -> R.drawable.logo_bitcoin
            LTC -> R.drawable.logo_litecoin
            ETH -> R.drawable.logo_ether
            DASH -> R.drawable.logo_dash
            DOGE -> R.drawable.logo_dogecoin
        })
}