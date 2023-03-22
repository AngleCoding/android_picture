package com.github.yuan.picture_take.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.yuan.picture_take.R
import com.github.yuan.picture_take.animator.DialogAnimator
import com.github.yuan.picture_take.animator.TranslateAlphaAnimator
import com.github.yuan.picture_take.animator.TranslateAnimator
import com.github.yuan.picture_take.core.DialogInfo
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
        this.window!!.setBackgroundDrawableResource(R.drawable.transparent_bg)
        mLlDialog = findViewById(R.id.mLlDialog)
        val lp = window!!.attributes
        lp!!.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.BOTTOM
        window?.attributes = lp
        initAnimator()
        initClickListener()
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
                DialogInfo.dialogAnimation!!
            )
        }
        return null

    }

    override fun dismiss() {
        super.dismiss()
        dialogAnimation?.animateDismiss()
    }

}