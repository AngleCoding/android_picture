package com.github.yuan.picture_take.dialog

import android.app.Dialog
import android.content.Context
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
import com.github.yuan.picture_take.basic.PictureSelector
import com.github.yuan.picture_take.app.DialogInfo
import com.github.yuan.picture_take.app.DialogInfo.animationMode
import com.github.yuan.picture_take.app.DialogInfo.cameraChooseMode
import com.github.yuan.picture_take.app.DialogInfo.cameraDialogVisibility
import com.github.yuan.picture_take.app.DialogInfo.cameraTextColor
import com.github.yuan.picture_take.app.DialogInfo.cameraTextSize
import com.github.yuan.picture_take.app.DialogInfo.fileDialogVisibility
import com.github.yuan.picture_take.app.DialogInfo.fileTextColor
import com.github.yuan.picture_take.app.DialogInfo.fileTextSize
import com.github.yuan.picture_take.app.DialogInfo.galleryChooseMode
import com.github.yuan.picture_take.app.DialogInfo.imageFormat
import com.github.yuan.picture_take.app.DialogInfo.imageFormatQ
import com.github.yuan.picture_take.app.DialogInfo.isAutoPlay
import com.github.yuan.picture_take.app.DialogInfo.isCameraRotateImage
import com.github.yuan.picture_take.app.DialogInfo.isDirectReturn
import com.github.yuan.picture_take.app.DialogInfo.isEmptyReturn
import com.github.yuan.picture_take.app.DialogInfo.isFastSlidingSelect
import com.github.yuan.picture_take.app.DialogInfo.isFullScreenModel
import com.github.yuan.picture_take.app.DialogInfo.isPreviewAudio
import com.github.yuan.picture_take.app.DialogInfo.isPreviewImage
import com.github.yuan.picture_take.app.DialogInfo.isWithVideoImage
import com.github.yuan.picture_take.app.DialogInfo.language
import com.github.yuan.picture_take.app.DialogInfo.listener
import com.github.yuan.picture_take.app.DialogInfo.maxSelectNum
import com.github.yuan.picture_take.app.DialogInfo.maxVideoSelectNum
import com.github.yuan.picture_take.app.DialogInfo.minSelectNum
import com.github.yuan.picture_take.app.DialogInfo.minVideoSelectNum
import com.github.yuan.picture_take.app.DialogInfo.onImageResultCallbackListener
import com.github.yuan.picture_take.app.DialogInfo.requestedOrientation
import com.github.yuan.picture_take.app.DialogInfo.selectedList
import com.github.yuan.picture_take.app.DialogInfo.selectionMode
import com.github.yuan.picture_take.app.DialogInfo.singleOrMutableMode
import com.github.yuan.picture_take.app.DialogInfo.videoFormat
import com.github.yuan.picture_take.app.DialogInfo.videoFormatQ
import com.github.yuan.picture_take.engine.GlideEngine
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
                if (!singleOrMutableMode) {
                    PictureUtils.openLocalImage(mContext)
                } else {
                    PictureSelector.create(mContext)
                        .openGallery(galleryChooseMode)
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .setMaxSelectNum(maxSelectNum)
                        .setMinSelectNum(minSelectNum)
                        .setSelectionMode(selectionMode)
                        .setSelectedData(selectedList)
                        .setRecyclerAnimationMode(animationMode)
                        .setLanguage(language)
                        .setRequestedOrientation(requestedOrientation)
                        .setMaxVideoSelectNum(maxVideoSelectNum)
                        .setMinVideoSelectNum(minVideoSelectNum)
                        .isPreviewAudio(isPreviewAudio)
                        .isPreviewImage(isPreviewImage)
                        .isPreviewFullScreenMode(isFullScreenModel)
                        .isWithSelectVideoImage(isWithVideoImage)
                        .isEmptyResultReturn(isEmptyReturn)
                        .isAutoVideoPlay(isAutoPlay)
                        .isCameraRotateImage(isCameraRotateImage)
                        .isFastSlidingSelect(isFastSlidingSelect)
                        .isDirectReturnSingle(isDirectReturn)
                        .setCameraImageFormat(imageFormat)
                        .setCameraImageFormatForQ(imageFormatQ)
                        .setCameraVideoFormat(videoFormat)
                        .setCameraVideoFormatForQ(videoFormatQ)
                        .setOutputCameraDir(DialogInfo.outPutCameraDir)
                        .setCameraInterceptListener(listener)
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
                    if (!singleOrMutableMode) {
                        PictureUtils.openCameraImage(mContext)
                    } else {
                        PictureSelector.create(mContext)
                            .openCamera(cameraChooseMode)
                            .setSelectedData(selectedList)
                            .setLanguage(language)
                            .isCameraRotateImage(isCameraRotateImage)
                            .setCameraImageFormat(imageFormat)
                            .setCameraImageFormatForQ(imageFormatQ)
                            .setCameraVideoFormat(videoFormat)
                            .setCameraVideoFormatForQ(videoFormatQ)
                            .setOutputCameraDir(DialogInfo.outPutCameraDir)
                            .setCameraInterceptListener(listener)
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