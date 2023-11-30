package com.github.yuan.picture_take

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.github.yuan.picture_take.app.DialogInfo
import com.github.yuan.picture_take.dialog.PictureDialog
import com.github.yuan.picture_take.entity.LocalMedia
import com.github.yuan.picture_take.enums.PictureDialogAnimation
import com.github.yuan.picture_take.interfaces.OnCameraInterceptListener
import com.github.yuan.picture_take.interfaces.OnResultCallbackListener

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
         * 设置adapter展位图
         */

        fun setAdapterOccupyBitmap(@DrawableRes resId: Int) : Builder {
            DialogInfo.mResource = resId
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
         * 开启多图模式
         * true是单图 false 多图
         */
        fun setSingleOrMutableMode(singleOrMutableMode: Boolean): Builder {
            DialogInfo.singleOrMutableMode = singleOrMutableMode
            return this
        }


        /**
         *多图模式-选择相册回显数据
         */
        fun setImageMutableForResult(onImageResultCallbackListener: OnResultCallbackListener<LocalMedia>): Builder {
            DialogInfo.onImageResultCallbackListener = onImageResultCallbackListener
            return this
        }

        /**
         * 多图模式-选择相机回显数据
         */
        fun setCameraMutableForResult(onImageResultCallbackListener: OnResultCallbackListener<LocalMedia>): Builder {
            DialogInfo.onImageResultCallbackListener = onImageResultCallbackListener
            return this
        }


        /**
         * 多图模式-相册
         * 相册/视频/音频/全部
         *  SelectMimeType.TYPE_IMAGE
         */
        fun openGalleryChooseMode(galleryChooseMode: Int): Builder {
            DialogInfo.galleryChooseMode = galleryChooseMode
            return this
        }

        /**
         *  多图模式-相机
         * 相册/视频/音频/全部
         */
        fun openCameraChooseMode(cameraChooseMode: Int): Builder {
            DialogInfo.cameraChooseMode = cameraChooseMode
            return this
        }

        /**
         * 多图模式- 图片最大选择数量
         */
        fun setMaxSelectNum(maxSelectNum: Int): Builder {
            DialogInfo.maxSelectNum = maxSelectNum
            return this
        }

        /**
         * 多图模式- 图片最小选择数量
         */
        fun setMinSelectNum(minSelectNum: Int): Builder {
            DialogInfo.minSelectNum = minSelectNum
            return this
        }

        /**
         * 多图模式- 单选或是多选
         */
        fun selectionMode(selectionMode: Int): Builder {
            DialogInfo.selectionMode = selectionMode
            return this
        }

        /**
         *多图模式- 相册已选数据
         */
        fun setSelectedData(selectedList: List<LocalMedia>): Builder {
            DialogInfo.selectedList = selectedList
            return this
        }

        /**
         * 多图模式- 相册列表动画效果
         */
        fun setRecyclerAnimationMode(animationMode: Int): Builder {
            DialogInfo.animationMode = animationMode
            return this
        }

        /**
         * 多图模式-设置相册语言
         */
        fun setLanguage(language: Int): Builder {
            DialogInfo.language = language
            return this
        }

        /**
         * 多图模式-设置屏幕旋转方向
         */
        fun setRequestedOrientation(requestedOrientation: Int): Builder {
            DialogInfo.requestedOrientation = requestedOrientation
            return this
        }


        /**
         * 多图模式-视频最大选择数量
         */
        fun setMaxVideoSelectNum(maxVideoSelectNum: Int): Builder {
            DialogInfo.maxVideoSelectNum = maxVideoSelectNum
            return this
        }


        /**
         * 多图模式-视频最小选择数量
         */
        fun setMinVideoSelectNum(minVideoSelectNum: Int): Builder {
            DialogInfo.minVideoSelectNum = minVideoSelectNum
            return this
        }


        /**
         * 多图模式-是否支持音频预览
         */
        fun isPreviewAudio(isPreviewAudio: Boolean): Builder {
            DialogInfo.isPreviewAudio = isPreviewAudio
            return this
        }


        /**
         * 多图模式-是否支持预览图片
         */
        fun isPreviewImage(isPreviewImage: Boolean): Builder {
            DialogInfo.isPreviewImage = isPreviewImage
            return this
        }


        /**
         * 多图模式-预览点击全屏效果
         */
        fun isPreviewFullScreenMode(isFullScreenModel: Boolean): Builder {
            DialogInfo.isFullScreenModel = isFullScreenModel
            return this
        }

        /**
         * 多图模式-是否支持视频图片同选
         */
        fun isWithSelectVideoImage(isWithVideoImage: Boolean): Builder {
            DialogInfo.isWithVideoImage = isWithVideoImage
            return this
        }

        /**
         * 多图模式-支持未选择返回
         */
        fun isEmptyResultReturn(isEmptyReturn: Boolean): Builder {
            DialogInfo.isEmptyReturn = isEmptyReturn
            return this
        }

        /**
         * 多图模式-拍照是否纠正旋转图片
         */
        fun isCameraRotateImage(isCameraRotateImage: Boolean): Builder {
            DialogInfo.isCameraRotateImage = isCameraRotateImage
            return this
        }

        /**
         * 多图模式-预览视频是否自动播放
         */
        fun isAutoVideoPlay(isAutoPlay: Boolean): Builder {
            DialogInfo.isAutoPlay = isAutoPlay
            return this
        }


        /**
         * 多图模式-快速滑动选择
         */
        fun isFastSlidingSelect(isFastSlidingSelect: Boolean): Builder {
            DialogInfo.isFastSlidingSelect = isFastSlidingSelect
            return this
        }


        /**
         * 多图模式- 单选时是否立即返回
         */
        fun isDirectReturnSingle(isDirectReturn: Boolean): Builder {
            DialogInfo.isDirectReturn = isDirectReturn
            return this
        }

        /**
         * 多图模式- 拍照图片输出格式
         */
        fun setCameraImageFormat(imageFormat: String): Builder {
            DialogInfo.imageFormat = imageFormat
            return this
        }

        /**
         * 多图模式- 拍照图片输出格式，Android Q以上
         */
        fun setCameraImageFormatForQ(imageFormatQ: String): Builder {
            DialogInfo.imageFormatQ = imageFormatQ
            return this
        }

        /**
         * 多图模式- 拍照视频输出格式
         */
        fun setCameraVideoFormat(videoFormat: String): Builder {
            DialogInfo.videoFormat = videoFormat
            return this
        }


        /**
         * 多图模式-拍照视频输出格式，Android Q以上
         */
        fun setCameraVideoFormatForQ(videoFormatQ: String): Builder {
            DialogInfo.videoFormatQ = videoFormatQ
            return this
        }

        /**
         * 多图模式- 拦截相机事件，实现自定义相机
         */
        fun setCameraInterceptListener(listener: OnCameraInterceptListener?): Builder {
            DialogInfo.listener = listener
            return this
        }

        /**
         * 设置图片输出地址
         */
        fun  setOutputCameraDir(outPutCameraDir:String){
            DialogInfo.outPutCameraDir = outPutCameraDir
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