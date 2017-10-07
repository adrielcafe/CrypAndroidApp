package cafe.adriel.bitchecker

import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import cafe.adriel.bitchecker.model.entity.Wallet
import java.math.BigInteger

// Resources
fun getColor(@ColorRes colorRes: Int) =
        ContextCompat.getColor(App.CONTEXT, colorRes)
fun getDrawable(@DrawableRes drawableRes: Int) =
        ContextCompat.getDrawable(App.CONTEXT, drawableRes)

// W
fun Wallet.formatBalance() = with(balance) {
    val COIN = BigInteger("100000000", 10)
    var value = BigInteger.valueOf(this)
    val negative = value < BigInteger.ZERO
    if (negative) {
        value = value.negate()
    }
    val coins = value.divide(COIN)
    val cents = value.remainder(COIN)
    var centStr = String.format("%08d", cents.toInt())
    centStr = centStr.replaceFirst("0+$".toRegex(), "")
    while (centStr.length < 2) {
        centStr += "0"
    }
    String.format("%s%d.%s", if (negative) "-" else "", coins.toInt(), centStr)
}