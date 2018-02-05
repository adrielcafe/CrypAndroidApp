package cafe.adriel.cryp.model.entity

import android.support.annotation.DrawableRes
import cafe.adriel.cryp.R

enum class Cryptocurrency(val fullName: String,
                          @DrawableRes
                          val logoRes: Int,
                          val autoRefresh: Boolean) {

    AEON("Aeon",
            R.drawable.logo_aeon,
            true),

    AUR("Auroracoin",
            R.drawable.logo_aur,
            true),

    BCH("Bitcoin Cash",
            R.drawable.logo_bch,
            true),

    BCN("Bytecoin",
            R.drawable.logo_bcn,
            true),

    BTC("Bitcoin",
            R.drawable.logo_btc,
            true),

    CLOAK("CloakCoin",
            R.drawable.logo_cloak,
            true),

    DASH("Dash",
            R.drawable.logo_dash,
            true),

    DGB("DigiByte",
            R.drawable.logo_dgb,
            true),

    DOGE("Dogecoin",
            R.drawable.logo_doge,
            true),

    EMC("Emercoin",
            R.drawable.logo_emc,
            true),

    ETH("Ethereum",
            R.drawable.logo_eth,
            true),

    FTC("Feathercoin",
            R.drawable.logo_ftc,
            true),

    GRS("Groestlcoin",
            R.drawable.logo_grs,
            true),

    LTC("Litecoin",
            R.drawable.logo_ltc,
            true),

    NMC("Namecoin",
            R.drawable.logo_nmc,
            true),

    NVC("NovaCoin",
            R.drawable.logo_nvc,
            true),

    NXT("Nxt",
            R.drawable.logo_nxt,
            true),

    PART("Particl",
            R.drawable.logo_part,
            true),

    PPC("Peercoin",
            R.drawable.logo_ppc,
            true),

    RDD("ReddCoin",
            R.drawable.logo_rdd,
            true),

    VTC("Vertcoin",
            R.drawable.logo_vtc,
            true),

    // TODO API not working
//    XMR("Monero",
//            R.drawable.logo_xmr,
//            true),

    XVG("Verge",
            R.drawable.logo_xvg,
            true),

    ZEC("Zcash",
            R.drawable.logo_zec,
            true);

    override fun toString() = "$fullName ($name)"

}