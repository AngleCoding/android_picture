package com.github.yuan.picture_take.animator

import android.view.View
import android.view.ViewPropertyAnimator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.github.yuan.picture_take.enums.PictureDialogAnimation

class TranslateAnimator(
    target: View,
    animationDuration: Int,
    popupAnimation: PictureDialogAnimation
) : DialogAnimator(target, animationDuration, popupAnimation) {

    var startTranslationX = 0f
    var startTranslationY: Float = 0f
    var endTranslationX = 0f
    var endTranslationY: Float = 0f

    override fun initAnimator() {
        if (!hasInit) {
            endTranslationX = targetView.translationX
            endTranslationY = targetView.translationY
            applyTranslation()
            startTranslationX = targetView.translationX
            startTranslationY = targetView.translationY
        }
    }


    private fun applyTranslation() {
        if (popupAnimation == PictureDialogAnimation.TranslateFromBottom) targetView.translationY =
            (targetView.parent as View).measuredHeight - targetView.top + targetView.translationY
    }


    override fun animateShow() {
        var animator: ViewPropertyAnimator? = null
        if (popupAnimation == PictureDialogAnimation.TranslateFromBottom) animator =
            targetView.animate().translationY(endTranslationY)
        animator?.setInterpolator(FastOutSlowInInterpolator())
            ?.setDuration(animationDuration.toLong())
            ?.withLayer()?.start()
    }

    override fun animateDismiss() {
        if (animating) return
        var animator: ViewPropertyAnimator? = null
        if (popupAnimation == PictureDialogAnimation.TranslateFromBottom) {
            startTranslationY =
                ((targetView.parent as View).measuredHeight - targetView.top).toFloat()
            animator = targetView.animate().translationY(startTranslationY)
        }
        if (animator != null) observerAnimator(
            animator.setInterpolator(FastOutSlowInInterpolator())
                .setDuration((animationDuration * .8).toLong())
                .withLayer()
        )?.start()
    }


}