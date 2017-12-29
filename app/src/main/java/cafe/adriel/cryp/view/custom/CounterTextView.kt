package cafe.adriel.cryp.view.custom

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import java.text.DecimalFormat

class CounterTextView(context: Context,
                      attrs: AttributeSet? = null) : AppCompatTextView(context, attrs) {

    private val DEFAULT_DURATION: Long = 2000

    private val animator: ValueAnimator
    private var decimalFormat: DecimalFormat? = null
    private var isAnimating = false

    init {
        animator = ValueAnimator().apply {
            this.duration = DEFAULT_DURATION
            this.addUpdateListener({
                text = decimalFormat?.format(it.animatedValue)
                        ?: it.animatedValue.toString()
            })
            this.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    isAnimating = true
                }
                override fun onAnimationEnd(animation: Animator) {
                    isAnimating = false
                }
                override fun onAnimationCancel(animation: Animator) { }
                override fun onAnimationRepeat(animation: Animator) { }
            })
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }

    fun setDecimalFormat(decimalFormat: DecimalFormat): CounterTextView {
        this.decimalFormat = decimalFormat
        return this
    }

    fun startAnimation(from: Float, to: Float) {
        if (isAnimating) {
            animator.cancel()
        }

        animator.setFloatValues(from, to)
        animator.start()
    }

}