package com.github.yuan.picture

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.yuan.picture_take.PictureChooseDialog
import com.github.yuan.picture_take.utils.PictureUtils
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MainActivity : AppCompatActivity() {


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.mBtTake).setOnClickListener {
            PictureChooseDialog.build(this) {
                show()
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureUtils.GET_IMAGE_FROM_PHONE -> { //选择相册之后的处理
                    data?.data?.let { PictureUtils.initUCrop(this, it) }
                }

                PictureUtils.GET_IMAGE_BY_CAMERA -> { //选择相机之后的处理
                    PictureUtils.initUCrop(this, PictureUtils.imageUriFromCamera)
                }

                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    val my_avatar =
                        File(PictureUtils.getImageAbsolutePath(this, resultUri).toString())
                    findViewById<ImageView>(R.id.mIv).post {
                        findViewById<ImageView>(R.id.mIv).setImageURI(resultUri)
                    }

//                    val partList: MutableList<MultipartBody.Part> = ArrayList()
//                    val requestBody =
//                        RequestBody.create(MediaType.parse("multipart/form-data"), my_avatar)
//                    val imageBodyPart =
//                        MultipartBody.Part.createFormData("files", my_avatar.name, requestBody)
//                    partList.add(imageBodyPart)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }
}