package cafe.adriel.cryp

import cafe.adriel.cryp.model.entity.Crypto

data class SwipeMenuOpenedEvent(val itemId: Long)

data class QrCodeScannedEvent(val text: String)

data class SelectedCryptoEvent(val crypto: Crypto)