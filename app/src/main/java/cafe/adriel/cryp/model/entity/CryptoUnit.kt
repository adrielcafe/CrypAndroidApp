package cafe.adriel.cryp.model.entity

enum class CryptoUnit(val fullName: String) {
    BTC("BTC"),
    M_BTC("mBTC"),
    BITS("Bits"),
    SATOSHI("Satoshi");

    override fun toString() = fullName

}