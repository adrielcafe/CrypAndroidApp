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
        get() = when(this){
            BTC -> getColor(R.color.buttercup)
            LTC -> getColor(R.color.concrete)
            ETH -> getColor(R.color.wet_asphalt)
            DASH -> getColor(R.color.summer_sky)
            DOGE -> getColor(R.color.sandstorm)
        }
    // TODO missing logos
    val icon: Drawable
        get() = when(this){
            BTC -> getDrawable(R.drawable.ic_add)
            LTC -> getDrawable(R.drawable.ic_add)
            ETH -> getDrawable(R.drawable.ic_add)
            DASH -> getDrawable(R.drawable.ic_add)
            DOGE -> getDrawable(R.drawable.ic_add)
        }
}