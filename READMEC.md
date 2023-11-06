# Album Selection Library [![](https://jitpack.io/v/AnglePengCoding/android_picture.svg)](https://jitpack.io/#AnglePengCoding/android_picture)

[英文](https://github.com/AnglePengCoding/android_picture/blob/main/README)

<div align="center">
  <img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/android.png">
</div>
<h3>
相册选择库（Kotlin版本），类似个人中心头像的单图上传，类似意见评论的多图上传，支持裁剪，图像旋转功能，支持动画弹出等</h3>


<h3>添加引用</h3>

```java

implementation'com.github.AnglePengCoding:android_picture:Tag'

```

<h3>版本差异 v.1.0.0/v2.0.0</h3>

v.1.0.0 仅使用单个图像，v2.0.0 增加了多种图像选择，开发者可以选择相应的开发模式。


<h3>Screen recording</h3>

<div align=start>
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/image.gif" width="250" height="300" />
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/camera.gif" width="250" height="300" />
</div>


<div align=start>
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/dtgif.gif" width="250" height="300" />
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/dtgif2.gif" width="250" height="300" />
</div>

<h3>单图像功能</h3>

```java


PictureChooseDialog.build(this){
        setSingleCameraRequestCode(10086)//设置单个图像相机RequestCode
        setSingleImageRequestCode(10096)//设置单个相册RequestCode
        show() //必设置
        }


```

<h3> onActivityResult </h3>

```java

@Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){
            if(resultCode==RESULT_OK){
            when(requestCode){
            10096->{ //选择相册后的处理
            data?.data?.let{PictureUtils.initUCrop(this,it)}
            }

            10086->{ //选择相机后的处理
            PictureUtils.initUCrop(this,PictureUtils.imageUriFromCamera)
            }

            UCrop.REQUEST_CROP->{ //裁剪后处理
            val resultUri=UCrop.getOutput(data!!)
            
            val file=
            File(PictureUtils.getImageAbsolutePath(this,resultUri).toString())
            findViewById<ImageView>(R.id.mIv).post{
        findViewById<ImageView>(R.id.mIv).setImageURI(resultUri)
        }
        }
        }
        }
        super.onActivityResult(requestCode,resultCode,data)
        }


```

<h3>多图功能</h3>

```java

adapter=GridImageAdapter(mContext)
        adapter.setAddPicClickListener(this)
        cameraBinding.mRecyclerView.layoutManager=FullyGridLayoutManager(mContext,4)
        cameraBinding.mRecyclerView.adapter=adapter


        override fun onAddPicClick(position:Int){
        PictureChooseDialog.build(this){
        setSingleOrMutableMode(true)//启用多图像模式
        setMaxSelectNum(2)//最大数量
        openGalleryChooseMode(SelectMimeType.TYPE_ALL)//多图像模式-相册
        openCameraChooseMode(SelectMimeType.TYPE_ALL)//多图像模式-相机
        setSelectedData(adapter.data)// 相册选定数据
        setImageMutableForResult(object:OnResultCallbackListener<LocalMedia> {
        //多图像模式-选择相册数据
        override fun onResult(result:ArrayList<LocalMedia>){
        adapter.setList(result)
        }

        override fun onCancel(){
        }

        })
        setCameraMutableForResult(object:OnResultCallbackListener<LocalMedia> {
        //多图像模式-选择相机数据
        override fun onResult(result:ArrayList<LocalMedia>){
        adapter.setList(result)
        }

        override fun onCancel(){
        }

        })

        show()
        }
        }


```

<h3>裁剪功能</h3>

```java

setUCropToolbarColor(R.color.teal_200)//设置裁剪ToolbarColor
        setUCropStatusBarColor(R.color.teal_200)//设置裁剪状态栏的颜色
        setMaxScaleMultiplier(2f)//设置裁剪的最大缩放比例
        setImageToCropBoundsAnimDuration(1000)//切换比例时设置图像动画
        setShowCropFrame(true)//设置是否显示矩形剪切框
        setCropGridStrokeWidth(R.color.teal_200)//设置裁剪框的水平线和垂直线的颜色
        setCropGridColumnCount(1)//设置垂直线的数量
        setCropGridRowCount(2)//设置横线的数量

```

<h3>弹出式和其他辅助功能</h3>

```java

setFileTextSize(18f)//设置对话框“相册”按钮的字体大小
        setFileTextColor(Color.parseColor("#FF3700B3"))//设置对话框“相册”按钮的字体颜色
        setCameraTextSize(15f)//设置对话框“相机”按钮的字体大小
        setCameraTextColor(Color.parseColor("#ffcc0000"))//设置对话框“相机”按钮的字体颜色
        setAnimationDuration(2000)//设置对话框动画的持续时间
        pictureDialogAnimation(PictureDialogAnimation.TranslateFromBottom)//设置对话框弹出动画
        setCameraDialogVisibility(true)//设置隐藏对话框相机按钮
        setFileDialogVisibility(true)//相册按钮设置为隐藏

<!--Other auxiliary functions-->
        setRecyclerAnimationMode()//专辑列表动画效果
        setLanguage()//设置相册语言
        setRequestedOrientation()//设置屏幕旋转方向
        setMaxVideoSelectNum()//最大视频选择数
        setMinVideoSelectNum()//视频选择的最小数量
        isPreviewAudio()//是否支持音频预览
        isPreviewImage()//是否支持预览图像
        isPreviewFullScreenMode()//预览点击全屏效果
        isWithSelectVideoImage()//支持视频和图像选择
        isEmptyResultReturn()//支持未选择返回
        isCameraRotateImage()//照片是否校正了图像的旋转
        isAutoVideoPlay()//预览视频是否自动播放
        isFastSlidingSelect()//快速滑动选择
        isDirectReturnSingle()//选择单个选项时是否立即返回
        setCameraImageFormat()//照片输出格式
        setCameraImageFormatForQ()//照片输出格式，Android Q或更高版本
        setCameraVideoFormat()//照片视频输出格式
        setCameraInterceptListener()//拦截摄像机事件并实现自定义摄像机


```

<h3>描述</h3>

```java

相机使用PictureSelector相册库，但由于权限库与Android Q及以上版本不兼容，已处理权限兼容性!

```


***


```java


MIT License

        Copyright(c)2023Yuang

        Permission is hereby granted,free of charge,to any person obtaining a copy
        of this software and associated documentation files(the"Software"),to deal
        in the Software without restriction,including without limitation the rights
        to use,copy,modify,merge,publish,distribute,sublicense,and/or sell
        copies of the Software,and to permit persons to whom the Software is
        furnished to do so,subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED"AS IS",WITHOUT WARRANTY OF ANY KIND,EXPRESS OR
        IMPLIED,INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,DAMAGES OR OTHER
        LIABILITY,WHETHER IN AN ACTION OF CONTRACT,TORT OR OTHERWISE,ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        SOFTWARE.
```
