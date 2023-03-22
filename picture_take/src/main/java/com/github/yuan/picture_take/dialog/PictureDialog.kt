package com.github.yuan.picture_take.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.github.yuan.picture_take.R
import com.github.yuan.picture_take.animator.DialogAnimator
import com.github.yuan.picture_take.animator.TranslateAlphaAnimator
import com.github.yuan.picture_take.animator.TranslateAnimator
import com.github.yuan.picture_take.core.DialogInfo
import com.github.yuan.picture_take.core.DialogInfo.cameraDialogVisibility
import com.github.yuan.picture_take.core.DialogInfo.fileDialogVisibility
import com.github.yuan.picture_take.enums.PictureDialogAnimation
import com.github.yuan.picture_take.permissions.PermissionCheck
import com.github.yuan.picture_take.utils.PictureUtils

class PictureDialog(context: Context) : Dialog(context) {
    private var dialogAnimation: DialogAnimator? = null
    private var mContext: Context
    private lateinit var mLlDialog: LinearLayout

    init {
        this.mContext = context
        initView()
    }

    @SuppressLint("MissingInflatedId")
    private fun initView() {
        setContentView(R.layout.dialog_picker_pictrue)
        this.window!!.setBackgroundDrawableResource(R.drawable.shape_round_white)
        mLlDialog = findViewById(R.id.mLlDialog)
        val lp = window!!.attributes
        lp!!.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.BOTTOM
        window?.attributes = lp
        initViewVisibility()
        initAnimator()
        initClickListener()

    }

    private fun initViewVisibility() {
        findViewById<TextView>(R.id.mTvFile).let {
            if (fileDialogVisibility) it.visibility = VISIBLE else it.visibility = GONE
        }
        findViewById<TextView>(R.id.mTvCamera).let {
            if (cameraDialogVisibility) it.visibility = VISIBLE else it.visibility = GONE
        }
    }

    private fun initClickListener() {
        findViewById<TextView>(R.id.mTvCancel).setOnClickListener {
            cancel()
        }
        //相册
        findViewById<TextView>(R.id.mTvFile).setOnClickListener {
            if (PermissionCheck.checkReadingPermission(mContext)) {
                PictureUtils.openLocalImage(mContext)
                if (isShowing) {
                    cancel()
                }
            }
        }

        //相机
        findViewById<TextView>(R.id.mTvCamera).setOnClickListener {
            if (PermissionCheck.checkCameraPermission(mContext)) {
                if (PermissionCheck.checkReadingPermission(mContext)) {
                    PictureUtils.openCameraImage(mContext)
                    if (isShowing) {
                        cancel()
                    }
                }
            }
        }

    }

    private fun initAnimator() {
        dialogAnimation = genAnimatorByDialogType()
        dialogAnimation?.initAnimator()
        dialogAnimation?.animateShow()
    }


    private fun genAnimatorByDialogType(): DialogAnimator? {
        if (DialogInfo.dialogAnimation == PictureDialogAnimation.TranslateAlphaFromBottom)
            return TranslateAlphaAnimator(
                mLlDialog.getChildAt(0),
                DialogInfo.duration,
                DialogInfo.dialogAnimation!!
            )
        else if (DialogInfo.dialogAnimation == PictureDialogAnimation.TranslateFromBottom) {
            return TranslateAnimator(
                mLlDialog.getChildAt(0),
                DialogInfo.duration,
                DialogInfo.dialogAnimation
            )
        }
        return null

    }

    override fun dismiss() {
        super.dismiss()
        dialogAnimation?.animateDismiss()
    }

}