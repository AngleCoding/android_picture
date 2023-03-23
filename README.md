# 相册选择库 [![](https://jitpack.io/v/AnglePengCoding/android_picture.svg)](https://jitpack.io/#AnglePengCoding/android_picture)


<h3>相册选择和相机拍照，裁剪，图片旋转功能，支持动画弹窗，裁剪设置例如状态栏颜色，缩放比例，裁剪框横竖颜色，横竖线数量设置等</h3>


 

<h3>添加依赖</h3>

```java

  implementation 'com.github.AnglePengCoding:android_picture:Tag'

```


<h3>录屏</h3>

<div align=start>

<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/image.gif" width="350" height="450" />
</div>

<div align=end>
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/camera.gif" width="350" height="450" />
</div>

<h3>如何使用？</h3>

```java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.mBtTake).setOnClickListener {
        PictureChooseDialog.build(this) {
        setFileTextSize(18f)//设置dialog“相册”按钮字体大小
        setFileTextColor(Color.parseColor("#FF3700B3"))//设置dialog“相册”按钮字体颜色
        setCameraTextSize(15f)//设置dialog“相机”按钮字体大小
        setCameraTextColor(Color.parseColor("#ffcc0000"))//设置dialog“相机”按钮字体颜色
        setAnimationDuration(2000)//设置dialog动画时长 可不设置
        pictureDialogAnimation(PictureDialogAnimation.TranslateFromBottom)//设置dialog弹窗动画  可不设置
        setCameraDialogVisibility(true)//设置dialog 相机按钮隐藏  根据业务需求
        setFileDialogVisibility(true)//设置dialog 相册按钮隐藏  根据业务需求
        setUCropToolbarColor(R.color.teal_200)//设置裁剪ToolbarColor   可不设置
        setUCropStatusBarColor(R.color.teal_200)//设置裁剪状态栏颜色   可不设置
        setMaxScaleMultiplier(2f)//裁剪最大缩放比例  可不设置
        setImageToCropBoundsAnimDuration(1000)//设置图片在切换比例时的动画  可不设置
        setShowCropFrame(true)//设置是否展示矩形裁剪框  可不设置
        setCropGridStrokeWidth(R.color.teal_200)//设置裁剪框横竖线的颜色 可不设置
        setCropGridColumnCount(1)//设置裁剪竖线的数量 可不设置
        setCropGridRowCount(2)//设置裁剪横线的数量 可不设置
        setCameraRequestCode(10086)//设置相机RequestCode
        setImageRequestCode(10096)//设置相册RequestCode
        show() //必设置
        }
        }
        }

        
```


<h3> onActivityResult 方法</h3>

```java


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

```



