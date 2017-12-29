package cafe.adriel.cryp

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Parcelable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.IntegerRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat
import cafe.adriel.cryp.model.entity.Wallet
import com.google.zxing.EncodeHintType
import khronos.Dates
import net.glxn.qrgen.android.QRCode
import net.glxn.qrgen.core.image.ImageType
import java.io.Serializable
import java.text.DecimalFormat
import java.util.*


// Resources
fun stringFrom(@StringRes stringRes: Int, vararg params : String? = emptyArray()) =
        App.CONTEXT.getString(stringRes, params)

fun intFrom(@IntegerRes intRes: Int) =
        App.CONTEXT.resources.getInteger(intRes)

fun colorFrom(@ColorRes colorRes: Int) =
        ContextCompat.getColor(App.CONTEXT, colorRes)

fun drawableFrom(@DrawableRes drawableRes: Int) =
        ContextCompat.getDrawable(App.CONTEXT, drawableRes)!!

// Context
inline fun <reified T : Activity> Context.startActivity(vararg extras: Pair<String, Any> = emptyArray()) {
    val intent = Intent(this, T::class.java)
    extras.forEach {
        when(it.second){
            is Parcelable -> intent.putExtra(it.first, it.second as Parcelable)
            is Serializable -> intent.putExtra(it.first, it.second as Serializable)
            is String -> intent.putExtra(it.first, it.second as String)
            is Int -> intent.putExtra(it.first, it.second as Int)
            else -> throw Exception("${it.second::class.java} not implemented")
        }
    }
    startActivity(intent)
}

inline fun <reified T : Activity> Fragment.startActivity(vararg extras: Pair<String, Parcelable>) {
    val intent = Intent(context, T::class.java)
    extras.forEach { intent.putExtra(it.first, it.second) }
    startActivity(intent)
}

// String
fun String.getQrCode(@ColorRes colorRes: Int = Color.BLACK) =
        QRCode.from(this)
            .to(ImageType.PNG)
            .withColor(colorFrom(colorRes), Color.WHITE)
            .withSize(300, 300)
            .withHint(EncodeHintType.MARGIN, 1)
            .bitmap()

fun String.copyToClipboard(label: String) {
    val clipboardManager = App.CONTEXT.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.primaryClip = ClipData.newPlainText(label, this)
}

// Int
val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.darken: Int
    get() {
        val hsv = FloatArray(3)
        Color.colorToHSV(this, hsv)
        hsv[2] *= 0.8f
        return Color.HSVToColor(hsv)
    }

// Date
fun Dates.now() =
        Calendar.getInstance().time

// Wallet
val coinFormat = DecimalFormat().apply {
    maximumFractionDigits = 8
    groupingSize = 3
    isGroupingUsed = true
    decimalFormatSymbols.apply {
        decimalSeparator = '.'
        groupingSeparator = Character.MIN_VALUE
        decimalFormatSymbols = this
    }
}
val currencyFormat = DecimalFormat().apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
    currency = Currency.getInstance(Locale.US)
}

fun Wallet.getFormattedBalanceBtc() = coinFormat.format(balance)

fun Wallet.getFormattedBalanceMBtc() = coinFormat.format(getBalanceMBtc())

fun Wallet.getFormattedBalanceBits() = coinFormat.format(getBalanceBits())

fun Wallet.getFormattedBalanceSatoshi() = coinFormat.format(getBalanceSatoshi())

fun Wallet.getFormattedBalanceCurrency() = currencyFormat.format(getBalanceCurrency())

fun Wallet.share(activity: Activity) =
        ShareCompat.IntentBuilder
            .from(activity)
            .setType("text/plain")
            .setText("$coin\n$address")
            .startChooser()
