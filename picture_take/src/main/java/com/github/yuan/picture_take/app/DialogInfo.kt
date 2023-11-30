package com.github.yuan.picture_take.app

import android.content.pm.ActivityInfo
import android.graphics.Color
import com.github.yuan.picture_take.R
import com.github.yuan.picture_take.animators.AnimationType.SLIDE_IN_BOTTOM_ANIMATION
import com.github.yuan.picture_take.config.PictureMimeType
import com.github.yuan.picture_take.config.SelectMimeType
import com.github.yuan.picture_take.config.SelectModeConfig
import com.github.yuan.picture_take.entity.LocalMedia
import com.github.yuan.picture_take.enums.PictureDialogAnimation
import com.github.yuan.picture_take.interfaces.OnCameraInterceptListener
import com.github.yuan.picture_take.interfaces.OnResultCallbackListener
import com.github.yuan.picture_take.language.LanguageConfig
import com.github.yuan.picture_take.utils.PictureUtils.GET_IMAGE_BY_CAMERA
import com.github.yuan.picture_take.utils.PictureUtils.GET_IMAGE_FROM_PHONE


object DialogInfo {
    var imageToCropBoundsAnimDuration: Int = 666//图片在切换比例时的动画
    var maxScaleMultiplier: Float = 5f //最大缩放比例
    var uCropToolbarColor: Int = R.color.u_crop_toolbar_color//裁剪ToolbarColor
    var uCropStatusBarColor: Int = R.color.u_crop_status_bar_color//裁剪状态栏颜色
    var mResource: Int = R.drawable.icon_take//adapter占位图
    var duration: Int = 300 //动画时长
    var dialogAnimation: PictureDialogAnimation = PictureDialogAnimation.TranslateFromBottom
    var cropFrame: Boolean = true
    var cropGridStrokeWidth: Int = 20//裁剪框横竖线的宽度
    var cropGridColor: Int = Color.GREEN//裁剪框横竖线的颜色
    var cropGridColumnCount: Int = 2//竖线的数量
    var cropGridRowCount: Int = 1//横线的数量
    var fileDialogVisibility: Boolean = true//设置dialog 相册按钮隐藏
    var cameraDialogVisibility: Boolean = true//设置dialog 相机按钮隐藏
    var fileTextSize: Float = 16f
    var cameraTextSize: Float = 16f
    var fileTextColor: Int = Color.parseColor("#5C5C68")
    var cameraTextColor: Int = Color.parseColor("#5C5C68")
    var cameraSingleRequestCode: Int = GET_IMAGE_BY_CAMERA//相机RequestCode
    var imageSingleRequestCode: Int = GET_IMAGE_FROM_PHONE//相机RequestCode
    var singleOrMutableMode: Boolean = false //true是单图 false 多图
    lateinit var onImageResultCallbackListener: OnResultCallbackListener<LocalMedia>
    var galleryChooseMode: Int = SelectMimeType.TYPE_IMAGE
    var cameraChooseMode: Int = SelectMimeType.TYPE_IMAGE
    var maxSelectNum: Int = 9
    var minSelectNum: Int = 1
    var selectionMode: Int = SelectModeConfig.MULTIPLE
    var selectedList: List<LocalMedia> = listOf()
    var animationMode: Int = SLIDE_IN_BOTTOM_ANIMATION
    var language: Int = LanguageConfig.CHINESE
    var requestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    var maxVideoSelectNum: Int = 1
    var minVideoSelectNum: Int = 1
    var isPreviewAudio: Boolean = true
    var isPreviewImage: Boolean = true
    var isFullScreenModel: Boolean = true
    var isWithVideoImage: Boolean = false
    var isEmptyReturn: Boolean = true
    var isCameraRotateImage: Boolean = true
    var isAutoPlay: Boolean = true
    var isFastSlidingSelect: Boolean = true
    var isDirectReturn: Boolean = true
    var imageFormat: String = PictureMimeType.JPEG
    var imageFormatQ: String = PictureMimeType.MIME_TYPE_IMAGE
    var videoFormat: String = PictureMimeType.MP4
    var videoFormatQ: String = PictureMimeType.MIME_TYPE_VIDEO
    var listener: OnCameraInterceptListener? = null
    var outPutCameraDir: String = ""


}