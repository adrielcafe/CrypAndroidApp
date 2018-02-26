package cafe.adriel.cryp.view.custom

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import cafe.adriel.cryp.model.entity.Crypto

class CryptoLogoImageView(context: Context, attrs: AttributeSet? = null) : AppCompatImageView(context, attrs) {

    fun setLogo(crypto: Crypto){
        if(crypto.hasLogo()){

        } else {

        }
    }

}