package com.github.yuan.picture_take.animator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewPropertyAnimator
import com.github.yuan.picture_take.enums.PictureDialogAnimation

abstract class DialogAnimator(
    target: View,
    var animationDuration: Int,
    var popupAnimation: PictureDialogAnimation?
) {
    protected var animating = false
    var hasInit = false
    var targetView: View = target

    abstract fun initAnimator()
    abstract fun animateShow()
    abstract fun animateDismiss()
    open fun getDuration(): Int {
        return animationDuration
    }

    protected open fun observerAnimator(animator: ValueAnimator): ValueAnimator? {
        animator.removeAllListeners()
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                animating = true
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animating = false
            }
        })
        return animator
    }

    protected open fun observerAnimator(animator: ViewPropertyAnimator): ViewPropertyAnimator? {
        animator.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                animating = true
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animating = false
            }
        })
        return animator
    }

}