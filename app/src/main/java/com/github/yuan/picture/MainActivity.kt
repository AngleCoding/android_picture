package com.github.yuan.picture

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.yuan.picture_take.PictureChooseDialog
import com.github.yuan.picture_take.enums.PictureDialogAnimation
import com.github.yuan.picture_take.utils.PictureUtils
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.mBtTake).setOnClickListener {
            PictureChooseDialog.build(this) {
                setSingleOrMutableMode(false)//上传图片模式是单图/多图 默认是单图
                setImageMutableForResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>) {

                    }

                    override fun onCancel() {
                    }

                })
                setCameraMutableForResult(object :OnResultCallbackListener<LocalMedia>{
                    override fun onResult(result: ArrayList<LocalMedia>?) {
                    }

                    override fun onCancel() {
                    }

                })
                show() //必设置
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                10096 -> { //选择相册之后的处理
                    data?.data?.let { PictureUtils.initUCrop(this, it) }
                }

                10086 -> { //选择相机之后的处理
                    PictureUtils.initUCrop(this, PictureUtils.imageUriFromCamera)
                }

                UCrop.REQUEST_CROP -> { //裁剪之后处理
                    val resultUri = UCrop.getOutput(data!!)
                    //获取的File文件
                    val file =
                        File(PictureUtils.getImageAbsolutePath(this, resultUri).toString())
                    findViewById<ImageView>(R.id.mIv).post {
                        findViewById<ImageView>(R.id.mIv).setImageURI(resultUri)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}