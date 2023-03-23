package com.github.yuan.picture_take.core

import android.graphics.Color
import com.github.yuan.picture_take.R
import com.github.yuan.picture_take.enums.PictureDialogAnimation
import com.github.yuan.picture_take.utils.PictureUtils.GET_IMAGE_BY_CAMERA
import com.github.yuan.picture_take.utils.PictureUtils.GET_IMAGE_FROM_PHONE


object DialogInfo {
    var imageToCropBoundsAnimDuration: Int = 666//图片在切换比例时的动画
    var maxScaleMultiplier: Float = 5f //最大缩放比例
    var uCropToolbarColor: Int = R.color.u_crop_toolbar_color//裁剪ToolbarColor
    var uCropStatusBarColor: Int = R.color.u_crop_status_bar_color//裁剪状态栏颜色
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
    var cameraRequestCode: Int = GET_IMAGE_BY_CAMERA//相机RequestCode
    var imageRequestCode: Int = GET_IMAGE_FROM_PHONE//相机RequestCode

}