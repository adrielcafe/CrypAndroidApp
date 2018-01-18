package cafe.adriel.cryp

import cafe.adriel.cryp.model.entity.Cryptocurrency

data class SwipeMenuOpenedEvent(val itemId: Long)

data class QrCodeScannedEvent(val text: String)

data class SelectCryptocurrencyEvent(val cryptocurrency: Cryptocurrency)