package cafe.adriel.cryp.model.entity

import android.graphics.drawable.Drawable
import cafe.adriel.cryp.R
import cafe.adriel.cryp.drawableFrom

enum class Coin(val fullName: String,
                val logo: Drawable) {

    AEON("Aeon",
            drawableFrom(R.drawable.logo_aeon)),

    AUR("Auroracoin",
            drawableFrom(R.drawable.logo_aur)),

    BCH("Bitcoin Cash",
            drawableFrom(R.drawable.logo_bch)),

    BCN("Bytecoin",
            drawableFrom(R.drawable.logo_bcn)),

    BTC("Bitcoin",
            drawableFrom(R.drawable.logo_btc)),

    CLOAK("CloakCoin",
            drawableFrom(R.drawable.logo_cloak)),

    DASH("Dash",
            drawableFrom(R.drawable.logo_dash)),

    DGB("DigiByte",
            drawableFrom(R.drawable.logo_dgb)),

    DOGE("Dogecoin",
            drawableFrom(R.drawable.logo_doge)),

    EMC("Emercoin",
            drawableFrom(R.drawable.logo_emc)),

    ETH("Ethereum",
            drawableFrom(R.drawable.logo_eth)),

    FTC("Feathercoin",
            drawableFrom(R.drawable.logo_ftc)),

    GRS("Groestlcoin",
            drawableFrom(R.drawable.logo_grs)),

    LTC("Litecoin",
            drawableFrom(R.drawable.logo_ltc)),

    NMC("Namecoin",
            drawableFrom(R.drawable.logo_nmc)),

    NVC("NovaCoin",
            drawableFrom(R.drawable.logo_nvc)),

    NXT("Nxt",
            drawableFrom(R.drawable.logo_nxt)),

    PART("Particl",
            drawableFrom(R.drawable.logo_part)),

    PPC("Peercoin",
            drawableFrom(R.drawable.logo_ppc)),

    RDD("ReddCoin",
            drawableFrom(R.drawable.logo_rdd)),

    VTC("Vertcoin",
            drawableFrom(R.drawable.logo_vtc)),

    // TODO not working
//    XMR("Monero",
//            drawableFrom(R.drawable.logo_xmr)),

    XVG("Verge",
            drawableFrom(R.drawable.logo_xvg)),

    ZEC("Zcash",
            drawableFrom(R.drawable.logo_zec));

    override fun toString() = "$fullName ($name)"

}