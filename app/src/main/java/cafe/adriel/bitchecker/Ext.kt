package cafe.adriel.bitchecker

import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat

fun getColor(@ColorRes colorRes: Int) =
        ContextCompat.getColor(App.CONTEXT, colorRes)

fun getDrawable(@DrawableRes drawableRes: Int) =
        ContextCompat.getDrawable(App.CONTEXT, drawableRes)