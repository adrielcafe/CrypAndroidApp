package cafe.adriel.cryp.model.entity

enum class CryptocurrencyUnit(val fullName: String) {
    BTC("BTC"),
    M_BTC("mBTC"),
    BITS("Bits"),
    SATOSHI("Satoshi");

    companion object {
        fun getByFullName(fullName: String) : CryptocurrencyUnit {
            return values().firstOrNull { it.fullName == fullName }
                    ?: BTC
        }
    }

    override fun toString() = fullName

}