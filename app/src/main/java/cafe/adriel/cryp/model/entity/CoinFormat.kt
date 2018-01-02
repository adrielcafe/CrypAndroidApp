package cafe.adriel.cryp.model.entity

enum class CoinFormat(val fullName: String) {
    BTC("BTC"),
    M_BTC("mBTC"),
    BITS("Bits"),
    SATOSHI("Satoshi");

    companion object {
        fun getByName(fullName: String) : CoinFormat {
            return values().firstOrNull { it.fullName == fullName }
                    ?: BTC
        }
    }

    override fun toString() = fullName

}