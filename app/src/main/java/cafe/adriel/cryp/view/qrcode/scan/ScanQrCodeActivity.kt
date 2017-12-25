package cafe.adriel.cryp.view.qrcode.scan

import android.graphics.PointF
import android.os.Bundle
import cafe.adriel.cryp.QrCodeScannedEvent
import cafe.adriel.cryp.R
import cafe.adriel.cryp.view.BaseActivity
import cafe.adriel.kbus.KBus
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import kotlinx.android.synthetic.main.activity_scan_qrcode.*

class ScanQrCodeActivity : BaseActivity(), QRCodeReaderView.OnQRCodeReadListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qrcode)

        vReader.setOnQRCodeReadListener(this)
        vReader.forceAutoFocus()
    }

    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {
        points?.let { vOverlay.setPoints(it) }
        text?.let {
            vReader.postDelayed({
                KBus.post(QrCodeScannedEvent(it))
                finish()
            }, 1000)
        }
    }

}