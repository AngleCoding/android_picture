package com.github.yuan.picture_take.animator

import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.github.yuan.picture_take.enums.PictureDialogAnimation

class TranslateAlphaAnimator(
    target: View,
    animationDuration: Int,
    popupAnimation: PictureDialogAnimation
) : DialogAnimator(target, animationDuration, popupAnimation) {

    private var startTranslationX = 0f
    private var startTranslationY: Float = 0f
    private var defTranslationX = 0f
    private var defTranslationY: Float = 0f

    override fun initAnimator() {
        defTranslationX = targetView.translationX
        defTranslationY = targetView.translationY
        targetView.alpha = 0f
        applyTranslation()
        startTranslationX = targetView.translationX
        startTranslationY = targetView.translationY
    }

    private fun applyTranslation() {
        if (popupAnimation == PictureDialogAnimation.TranslateAlphaFromBottom) targetView.translationY =
            targetView.measuredHeight.toFloat()
    }


    override fun animateShow() {
        targetView.animate().translationX(defTranslationX).translationY(defTranslationY).alpha(1f)
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(animationDuration.toLong())
            .withLayer()
            .start()
    }

    override fun animateDismiss() {
        if (animating) return
        observerAnimator(
            targetView.animate().translationX(startTranslationX).translationY(startTranslationY)
                .alpha(0f)
                .setInterpolator(FastOutSlowInInterpolator())
                .setDuration(animationDuration.toLong())
                .withLayer()
        )?.start()
    }
}