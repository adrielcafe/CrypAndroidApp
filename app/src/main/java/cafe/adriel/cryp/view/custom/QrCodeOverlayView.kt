package cafe.adriel.cryp.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import cafe.adriel.cryp.R
import cafe.adriel.cryp.colorFrom

class QrCodeOverlayView(context: Context, attributeSet: AttributeSet? = null)
        : View(context, attributeSet) {

    private var points: Array<out PointF>? = null
    private var paint = Paint()

    init {
        paint.apply {
            color = colorFrom(R.color.colorAccent)
            style = Paint.Style.FILL
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        points?.forEach {
            canvas.drawCircle(it.x, it.y, 20f, paint)
        }
    }

    fun setPoints(points: Array<out PointF>) {
        this.points = points
        invalidate()
    }
}