package cafe.adriel.cryp.view.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import cafe.adriel.cryp.R
import cafe.adriel.cryp.model.entity.Crypto
import kotlinx.android.synthetic.main.view_crypto_logo.view.*

class CryptoLogoView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private var mBackgroundColor = Color.TRANSPARENT
    private var mTextColor = Color.WHITE

    init {
        inflate(getContext(), R.layout.view_crypto_logo, this)
        if (attrs != null) {
            val styleAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.CryptoLogoView)
            mBackgroundColor = styleAttrs.getColor(R.styleable.CryptoLogoView_clv_backgroundColor, mBackgroundColor)
            mTextColor = styleAttrs.getColor(R.styleable.CryptoLogoView_clv_textColor, mTextColor)
            styleAttrs.recycle()
        }
        vClvLogo.imageTintList = ColorStateList.valueOf(mBackgroundColor)
        vClvName.setTextColor(mTextColor)
    }

    fun setCrypto(crypto: Crypto){
        clear()
        if(crypto.hasLogo()){
            vClvLogo.setImageResource(crypto.logoResId)
        } else {
            vClvLogo.backgroundTintList = ColorStateList.valueOf(mBackgroundColor)
            vClvName.text = crypto.symbol.replace("*", "")
        }
    }

    fun clear(){
        vClvLogo.setImageResource(0)
        vClvLogo.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        vClvName.text = ""
    }

}