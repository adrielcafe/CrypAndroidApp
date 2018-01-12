package cafe.adriel.cryp

data class SwipeMenuOpenedEvent(val itemId: Long)

data class QrCodeScannedEvent(val text: String)