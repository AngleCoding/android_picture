package com.github.yuan.picture_take.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.yuan.picture_take.R
import com.github.yuan.picture_take.animator.DialogAnimator
import com.github.yuan.picture_take.animator.TranslateAlphaAnimator
import com.github.yuan.picture_take.animator.TranslateAnimator
import com.github.yuan.picture_take.core.DialogInfo
import com.github.yuan.picture_take.core.DialogInfo.cameraDialogVisibility
import com.github.yuan.picture_take.core.DialogInfo.cameraTextColor
import com.github.yuan.picture_take.core.DialogInfo.cameraTextSize
import com.github.yuan.picture_take.core.DialogInfo.fileDialogVisibility
import com.github.yuan.picture_take.core.DialogInfo.fileTextColor
import com.github.yuan.picture_take.core.DialogInfo.fileTextSize
import com.github.yuan.picture_take.core.DialogInfo.onImageResultCallbackListener
import com.github.yuan.picture_take.core.DialogInfo.singleOrMutableMode
import com.github.yuan.picture_take.engine.GlideEngine
import com.github.yuan.picture_take.enums.PictureDialogAnimation
import com.github.yuan.picture_take.permissions.PermissionCheck
import com.github.yuan.picture_take.utils.PictureUtils
import com.luck.picture.lib.PictureSelectorFragment
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import java.util.ArrayList

class PictureDialog(context: Context) : Dialog(context) {
    private var dialogAnimation: DialogAnimator? = null
    private var mContext: Context
    private lateinit var mLlDialog: LinearLayout

    init {
        this.mContext = context
        initView()
    }

    private fun initView() {
        setContentView(R.layout.dialog_picker_pictrue)
        initWindowAttributes()
        initViewVisibility()
        initAnimator()
        initClickListener()

    }

    private fun initWindowAttributes() {
        this.window!!.setBackgroundDrawableResource(R.drawable.shape_round_white)
        mLlDialog = findViewById(R.id.mLlDialog)
        val lp = window!!.attributes
        lp!!.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.BOTTOM
        window?.attributes = lp
    }

    private fun initViewVisibility() {
        findViewById<TextView>(R.id.mTvFile).let {
            if (fileDialogVisibility) it.visibility = VISIBLE else it.visibility = GONE
            it.textSize = fileTextSize
            it.setTextColor(fileTextColor)
        }
        findViewById<TextView>(R.id.mTvCamera).let {
            if (cameraDialogVisibility) it.visibility = VISIBLE else it.visibility = GONE
            it.textSize = cameraTextSize
            it.setTextColor(cameraTextColor)
        }

    }

    private fun initClickListener() {
        findViewById<TextView>(R.id.mTvCancel).setOnClickListener {
            cancel()
        }
        //相册
        findViewById<TextView>(R.id.mTvFile).setOnClickListener {
            if (PermissionCheck.checkReadingPermission(mContext)) {
                if (singleOrMutableMode) {
                    PictureUtils.openLocalImage(mContext)
                } else {
                    PictureSelector.create(mContext)
                        .openGallery(SelectMimeType.ofImage())
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .forResult(onImageResultCallbackListener)

                    if (isShowing) {
                        cancel()
                    }
                }
            }
        }

        //相机
        findViewById<TextView>(R.id.mTvCamera).setOnClickListener {
            if (PermissionCheck.checkCameraPermission(mContext)) {
                if (PermissionCheck.checkReadingPermission(mContext)) {
                    if (singleOrMutableMode) {
                        PictureUtils.openCameraImage(mContext)
                    } else {
                        mContext as AppCompatActivity
                        PictureSelector.create(mContext)
                            .openCamera(SelectMimeType.ofImage())
                            .forResult(onImageResultCallbackListener)
                    }
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
                DialogInfo.dialogAnimation
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