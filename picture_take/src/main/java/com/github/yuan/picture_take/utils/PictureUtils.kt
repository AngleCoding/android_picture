package com.github.yuan.picture_take.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.yuan.picture_take.app.DialogInfo.cameraSingleRequestCode
import com.github.yuan.picture_take.app.DialogInfo.cropFrame
import com.github.yuan.picture_take.app.DialogInfo.cropGridColor
import com.github.yuan.picture_take.app.DialogInfo.cropGridColumnCount
import com.github.yuan.picture_take.app.DialogInfo.cropGridRowCount
import com.github.yuan.picture_take.app.DialogInfo.cropGridStrokeWidth
import com.github.yuan.picture_take.app.DialogInfo.imageSingleRequestCode
import com.github.yuan.picture_take.app.DialogInfo.imageToCropBoundsAnimDuration
import com.github.yuan.picture_take.app.DialogInfo.uCropStatusBarColor
import com.github.yuan.picture_take.app.DialogInfo.uCropToolbarColor
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import com.yalantis.ucrop.util.FileUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


object PictureUtils {
    const val GET_IMAGE_BY_CAMERA = 5001
    const val GET_IMAGE_FROM_PHONE = 5002
    lateinit var imageUriFromCamera: Uri

    /**
     * 选择相册之后的处理
     */
    @SuppressLint("IntentReset")
    fun openLocalImage(context: Context) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        (context as AppCompatActivity).startActivityForResult(intent, imageSingleRequestCode)
    }

    /**
     * 选择照相机之后的处理
     */
    fun openCameraImage(context: Context) {
        imageUriFromCamera = createImagePathUri((context as AppCompatActivity))
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriFromCamera)
        context.startActivityForResult(intent, cameraSingleRequestCode)
    }


    private fun createImagePathUri(context: Context): Uri {
        val imageFilePath = arrayOf<Uri?>(null)
        val status = Environment.getExternalStorageState()
        val timeFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
        val time = System.currentTimeMillis()
        val imageName = timeFormatter.format(Date(time))
        val values = ContentValues(3)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
        values.put(MediaStore.Images.Media.DATE_TAKEN, time)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        if (status == Environment.MEDIA_MOUNTED) {
            imageFilePath[0] =
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            imageFilePath[0] =
                context.contentResolver.insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values)
        }
        Log.i("", "生成的照片输出路径：" + imageFilePath[0].toString())
        return imageFilePath[0]!!
    }


    fun getImageAbsolutePath(context: Context?, imageUri: Uri?): String? {
        if (context == null || imageUri == null) return null
        if (DocumentsContract.isDocumentUri(
                context,
                imageUri
            )
        ) {
            if (FileUtils.isExternalStorageDocument(imageUri)) {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (FileUtils.isDownloadsDocument(imageUri)) {
                val id = DocumentsContract.getDocumentId(imageUri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return FileUtils.getDataColumn(context, contentUri, null, null)
            } else if (FileUtils.isMediaDocument(imageUri)) {
                val docId = DocumentsContract.getDocumentId(imageUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(split[1])
                return FileUtils.getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } // MediaStore (and general)
        else if ("content".equals(imageUri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (FileUtils.isGooglePhotosUri(imageUri)) imageUri.lastPathSegment else FileUtils.getDataColumn(
                context,
                imageUri,
                null,
                null
            )
        } else if ("file".equals(imageUri.scheme, ignoreCase = true)) {
            return imageUri.path
        }
        return null
    }


    /**
     * 图片裁剪
     */
    fun initUCrop(activity: AppCompatActivity, uri: Uri) {
        val timeFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
        val time = System.currentTimeMillis()
        val imageName = timeFormatter.format(Date(time))
        val destinationUri = Uri.fromFile(File(activity.cacheDir, "$imageName.jpeg"))
        val options = UCrop.Options()
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)
        options.setToolbarColor(ActivityCompat.getColor(activity, uCropToolbarColor))
        options.setStatusBarColor(ActivityCompat.getColor(activity, uCropStatusBarColor))
        options.setImageToCropBoundsAnimDuration(imageToCropBoundsAnimDuration)
        options.setShowCropFrame(cropFrame)
        options.setCropGridStrokeWidth(cropGridStrokeWidth)
        options.setCropGridColor(cropGridColor)
        options.setCropGridColumnCount(cropGridColumnCount)
        options.setCropGridRowCount(cropGridRowCount)

        UCrop.of<Any>(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .withOptions(options)
            .start(activity)
    }


}