package cafe.adriel.cryp.view.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.support.annotation.ColorRes
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View
import android.view.ViewAnimationUtils
import cafe.adriel.cryp.colorFrom
import io.mironov.smuggler.AutoParcelable

// Based on https://medium.com/@gabornovak/circular-reveal-animation-between-fragments-d8ed9011aec
object RevealAnimationHelper {
    private const val LONG_DURATION = 500L
    private const val SHORT_DURATION = LONG_DURATION / 2

    fun startEnterAnimation(view: View, settings: AnimationSettings, listener: (() -> Unit)? = null) {
        view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                v.removeOnLayoutChangeListener(this)

                val finalRadius = Math.hypot(settings.width.toDouble(), settings.height.toDouble()).toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(v, settings.centerX, settings.centerY, 0F, finalRadius)
                anim.run {
                    duration = LONG_DURATION
                    interpolator = FastOutSlowInInterpolator()
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            listener?.let { it() }
                        }
                    })
                    start()
                }
                view.setBackgroundColor(colorFrom(settings.bgColor))
            }
        })
    }

    fun startExitAnimation(view: View, settings: AnimationSettings, listener: (() -> Unit)? = null) {
        val initRadius = Math.sqrt((settings.width * settings.width + settings.height * settings.height).toDouble()).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(view, settings.centerX, settings.centerY, initRadius, 0f)
        anim.run {
            duration = SHORT_DURATION
            interpolator = FastOutSlowInInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                    listener?.let { it() }
                }
            })
            start()
        }
        view.setBackgroundColor(colorFrom(settings.bgColor))
    }

    @SuppressLint("ParcelCreator")
    class AnimationSettings(
        var centerX: Int,
        var centerY: Int,
        var width: Int,
        var height: Int,
        @ColorRes var bgColor: Int) : AutoParcelable

}