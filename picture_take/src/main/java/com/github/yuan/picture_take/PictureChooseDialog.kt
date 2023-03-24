package com.github.yuan.picture_take

import android.content.Context
import androidx.annotation.ColorInt
import com.github.yuan.picture_take.core.DialogInfo
import com.github.yuan.picture_take.dialog.PictureDialog
import com.github.yuan.picture_take.enums.PictureDialogAnimation
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener

/**
 * @author Yuang
 *  相册选择库
 *
 */
class PictureChooseDialog {

    companion object {
        inline fun build(context: Context, block: Builder.() -> Unit) =
            Builder(context).apply(block).build()
    }

    class Builder(private val context: Context) {

        /**
         * 设置dialog“相册”按钮字体大小
         */
        fun setFileTextSize(fileTextSize: Float): Builder {
            DialogInfo.fileTextSize = fileTextSize
            return this
        }

        /**
         * 设置dialog“相册”按钮字体颜色
         */
        fun setFileTextColor(@ColorInt fileTextColor: Int): Builder {
            DialogInfo.fileTextColor = fileTextColor
            return this
        }

        /**
         * 设置dialog“相机”按钮字体大小
         */
        fun setCameraTextSize(cameraTextSize: Float): Builder {
            DialogInfo.cameraTextSize = cameraTextSize
            return this
        }

        /**
         * 设置dialog“相机”按钮字体颜色
         */
        fun setCameraTextColor(@ColorInt cameraTextColor: Int): Builder {
            DialogInfo.cameraTextColor = cameraTextColor
            return this
        }

        /**
         * 设置dialog动画时长
         */
        fun setAnimationDuration(duration: Int): Builder {
            DialogInfo.duration = duration
            return this
        }

        /**
         * 设置dialog弹窗动画
         */
        fun pictureDialogAnimation(dialogAnimation: PictureDialogAnimation): Builder {
            DialogInfo.dialogAnimation = dialogAnimation
            return this
        }

        /**
         * 设置dialog 相册按钮隐藏
         */
        fun setFileDialogVisibility(fileDialogVisibility: Boolean): Builder {
            DialogInfo.fileDialogVisibility = fileDialogVisibility
            return this
        }

        /**
         * 设置dialog 相机按钮隐藏
         */
        fun setCameraDialogVisibility(fileDialogVisibility: Boolean): Builder {
            DialogInfo.cameraDialogVisibility = fileDialogVisibility
            return this
        }


        /**
         * 设置裁剪ToolbarColor
         */
        fun setUCropToolbarColor(uCropToolbarColor: Int): Builder {
            DialogInfo.uCropToolbarColor = uCropToolbarColor
            return this
        }

        /**
         * 设置裁剪状态栏颜色
         */
        fun setUCropStatusBarColor(uCropStatusBarColor: Int): Builder {
            DialogInfo.uCropStatusBarColor = uCropStatusBarColor
            return this
        }

        /**
         * 裁剪最大缩放比例
         */
        fun setMaxScaleMultiplier(maxScaleMultiplier: Float): Builder {
            DialogInfo.maxScaleMultiplier = maxScaleMultiplier
            return this
        }

        /**
         *设置图片在切换比例时的动画
         */
        fun setImageToCropBoundsAnimDuration(imageToCropBoundsAnimDuration: Int): Builder {
            DialogInfo.imageToCropBoundsAnimDuration = imageToCropBoundsAnimDuration
            return this
        }

        /**
         *设置是否展示矩形裁剪框
         */
        fun setShowCropFrame(cropFrame: Boolean): Builder {
            DialogInfo.cropFrame = cropFrame
            return this
        }

        /**
         *设置裁剪框横竖线的颜色
         */
        fun setCropGridStrokeWidth(@ColorInt cropGridColor: Int): Builder {
            DialogInfo.cropGridColor = cropGridColor
            return this
        }

        /**
         *设置裁剪竖线的数量
         */
        fun setCropGridColumnCount(cropGridColumnCount: Int): Builder {
            DialogInfo.cropGridColumnCount = cropGridColumnCount
            return this
        }

        /**
         *设置裁剪横线的数量
         */
        fun setCropGridRowCount(cropGridRowCount: Int): Builder {
            DialogInfo.cropGridRowCount = cropGridRowCount
            return this
        }

        /**
         * 设置单图相机RequestCode
         */
        fun setSingleCameraRequestCode(cameraSingleRequestCode: Int): Builder {
            DialogInfo.cameraSingleRequestCode = cameraSingleRequestCode
            return this
        }

        /**
         * 设置单图相册RequestCode
         */
        fun setSingleImageRequestCode(imageSingleRequestCode: Int): Builder {
            DialogInfo.imageSingleRequestCode = imageSingleRequestCode
            return this
        }

        /**
         * 上传模式是单图/多图
         * true是单图 false 多图
         */
        fun setSingleOrMutableMode(singleOrMutableMode: Boolean): Builder {
            DialogInfo.singleOrMutableMode = singleOrMutableMode
            return this
        }


        /**
         * 多图模式-相册回显数据
         */
        fun setImageMutableForResult(onImageResultCallbackListener: OnResultCallbackListener<LocalMedia>): Builder {
            DialogInfo.onImageResultCallbackListener = onImageResultCallbackListener
            return this
        }

        /**
         * 多图模式-相机回显数据
         */
        fun setCameraMutableForResult(onImageResultCallbackListener: OnResultCallbackListener<LocalMedia>): Builder {
            DialogInfo.onImageResultCallbackListener = onImageResultCallbackListener
            return this
        }

        /**
         * 显示弹窗
         */
        fun show(): Builder {
            PictureDialog(context).show()
            return this
        }

        fun build() = PictureChooseDialog()
    }


}