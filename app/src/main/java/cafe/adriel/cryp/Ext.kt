package cafe.adriel.cryp

import android.app.Activity
import android.app.Fragment
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Parcelable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.IntegerRes
import android.support.annotation.StringRes
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat
import cafe.adriel.cryp.model.entity.Wallet
import cafe.adriel.cryp.model.repository.PreferenceRepository
import com.google.zxing.EncodeHintType
import net.glxn.qrgen.android.QRCode
import net.glxn.qrgen.core.image.ImageType
import java.io.Serializable
import java.text.DecimalFormat
import java.util.*

// Resources
fun stringFrom(@StringRes stringRes: Int, vararg param : String? = emptyArray()) =
        App.context.getString(stringRes, *param)

fun intFrom(@IntegerRes intRes: Int) =
        App.context.resources.getInteger(intRes)

fun colorFrom(@ColorRes colorRes: Int) =
        ContextCompat.getColor(App.context, colorRes)

fun drawableFrom(@DrawableRes drawableRes: Int) =
        ContextCompat.getDrawable(App.context, drawableRes)!!

// Context
inline fun <reified T : Activity> Context.start(vararg extras: Pair<String, Any> = emptyArray()) {
    val intent = Intent(this, T::class.java)
    extras.forEach {
        when(it.second){
            is Parcelable -> intent.putExtra(it.first, it.second as Parcelable)
            is Serializable -> intent.putExtra(it.first, it.second as Serializable)
            is String -> intent.putExtra(it.first, it.second as String)
            is Int -> intent.putExtra(it.first, it.second as Int)
            is Long -> intent.putExtra(it.first, it.second as Long)
            else -> throw Exception("${it.second::class.java} not implemented")
        }
    }
    startActivity(intent)
}

inline fun <reified T : Activity> Fragment.start(vararg extras: Pair<String, Any> = emptyArray()) {
    activity.start<T>(*extras)
}

fun Context.isConnected(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo?.isConnectedOrConnecting ?: false
}

// String
fun String.getQrCode(@ColorRes colorRes: Int = android.R.color.black) =
        QRCode.from(this)
            .to(ImageType.PNG)
            .withColor(colorFrom(colorRes), Color.WHITE)
            .withSize(300, 300)
            .withHint(EncodeHintType.MARGIN, 1)
            .bitmap()

fun String.copyToClipboard(label: String) {
    val clipboardManager = App.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.primaryClip = ClipData.newPlainText(label, this)
}

fun String.share(activity: Activity) =
        ShareCompat.IntentBuilder
                .from(activity)
                .setType("text/plain")
                .setText(this)
                .startChooser()

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
fun now() =
        Calendar.getInstance().time

// Wallet
fun getCryptoFormat() =
    DecimalFormat().apply {
        maximumFractionDigits = 8
        groupingSize = 3
        isGroupingUsed = true
        decimalFormatSymbols.apply {
            decimalSeparator = '.'
            groupingSeparator = Character.MIN_VALUE
            decimalFormatSymbols = this
        }
    }

fun getCurrencyFormat() =
    DecimalFormat().apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
        currency = PreferenceRepository.getCurrency()
    }

fun Wallet.share(activity: Activity) = "${crypto.fullName}\n$publicKey".share(activity)