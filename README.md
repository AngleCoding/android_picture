# 相册选择库 [![](https://jitpack.io/v/AnglePengCoding/android_picture.svg)](https://jitpack.io/#AnglePengCoding/android_picture)


<h3>相册选择库，单图/多图选择功能，单图类似个人中心头像上传，多图类似意见评论多图上传，支持裁剪，图片旋转功能，支持动画弹窗，裁剪设置例如状态栏颜色，缩放比例，裁剪框横竖颜色，横竖线数量设置等</h3>


 

<h3>添加依赖</h3>

```java

  implementation 'com.github.AnglePengCoding:android_picture:Tag'

```


<h3>录屏</h3>

<div align=start>
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/image.gif" width="250" height="300" />
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/camera.gif" width="250" height="300" />
</div>


<div align=start>
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/dtgif.gif" width="250" height="300" />
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/dtgif2.gif" width="250" height="300" />
</div>

<h3>单图功能</h3>

```java


    PictureChooseDialog.build(this) {
        setSingleCameraRequestCode(10086)//设置单图相机RequestCode
        setSingleImageRequestCode(10096)//设置单图相册RequestCode
        show() //必设置
        }

        
```


<h3> onActivityResult </h3>

```java

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


```

<h3>多图功能</h3>

```java
  
        adapter = GridImageAdapter(mContext)
        adapter.setAddPicClickListener(this)
        cameraBinding.mRecyclerView.layoutManager = FullyGridLayoutManager(mContext, 4)
        cameraBinding.mRecyclerView.adapter = adapter
                
                
    override fun onAddPicClick(position: Int) {
        PictureChooseDialog.build(this) {
            setSingleOrMutableMode(true)//开启多图模式
            setMaxSelectNum(2)//最大数量
            openGalleryChooseMode(SelectMimeType.TYPE_ALL)//多图模式-相册
            openCameraChooseMode(SelectMimeType.TYPE_ALL)//多图模式-相机
            setSelectedData(adapter.data)// 相册已选数据
            setImageMutableForResult(object : OnResultCallbackListener<LocalMedia> {
                //多图模式-选择相册回显数据
                override fun onResult(result: ArrayList<LocalMedia>) {
                    adapter.setList(result)
                }

                override fun onCancel() {
                }

            })
            setCameraMutableForResult(object : OnResultCallbackListener<LocalMedia> {
                //多图模式-选择相机回显数据
                override fun onResult(result: ArrayList<LocalMedia>) {
                    adapter.setList(result)
                }

                override fun onCancel() {
                }

            })

            show()
        }
    }


```


<h3>裁剪功能</h3>

```java
               
            setUCropToolbarColor(R.color.teal_200)//设置裁剪ToolbarColor   可不设置
            setUCropStatusBarColor(R.color.teal_200)//设置裁剪状态栏颜色   可不设置
            setMaxScaleMultiplier(2f)//设置裁剪最大缩放比例  可不设置
            setImageToCropBoundsAnimDuration(1000)//设置图片在切换比例时的动画  可不设置
            setShowCropFrame(true)//设置是否展示矩形裁剪框  可不设置
            setCropGridStrokeWidth(R.color.teal_200)//设置裁剪框横竖线的颜色 可不设置
            setCropGridColumnCount(1)//设置裁剪竖线的数量 可不设置
            setCropGridRowCount(2)//设置裁剪横线的数量 可不设置

```


<h3>弹窗样式以及其他辅助功能</h3>

```java

            setFileTextSize(18f)//设置dialog“相册”按钮字体大小  根据业务需求
            setFileTextColor(Color.parseColor("#FF3700B3"))//设置dialog“相册”按钮字体颜色  根据业务需求
            setCameraTextSize(15f)//设置dialog“相机”按钮字体大小  根据业务需求
            setCameraTextColor(Color.parseColor("#ffcc0000"))//设置dialog“相机”按钮字体颜色  根据业务需求
            setAnimationDuration(2000)//设置dialog动画时长  根据业务需求
            pictureDialogAnimation(PictureDialogAnimation.TranslateFromBottom)//设置dialog弹窗动画  根据业务需求
            setCameraDialogVisibility(true)//设置dialog 相机按钮隐藏  根据业务需求
            setFileDialogVisibility(true)//设置dialog 相册按钮隐藏  根据业务需求

            <!-- 其他辅助功能-->
            setRecyclerAnimationMode()//相册列表动画效果
            setLanguage()//设置相册语言
            setRequestedOrientation()//设置屏幕旋转方向
            setMaxVideoSelectNum()//视频最大选择数量
            setMinVideoSelectNum()//视频最小选择数量
            isPreviewAudio()//是否支持音频预览
            isPreviewImage()//是否支持预览图片
            isPreviewFullScreenMode()//预览点击全屏效果
            isWithSelectVideoImage()//是否支持视频图片同选
            isEmptyResultReturn()//支持未选择返回
            isCameraRotateImage()//拍照是否纠正旋转图片
            isAutoVideoPlay()//预览视频是否自动播放
            isFastSlidingSelect()//快速滑动选择
            isDirectReturnSingle()//单选时是否立即返回
            setCameraImageFormat()//拍照图片输出格式
            setCameraImageFormatForQ()//拍照图片输出格式，Android Q以上
            setCameraVideoFormat()//拍照视频输出格式
            setCameraInterceptListener()//拦截相机事件，实现自定义相机
            

```

<h3>特意描述</h3>

```java
 
相机采用PictureSelector相册库,由于权限库不兼容androidQ以上版本，所以做了权限兼容处理！

```

<h3>有问题，主页联系邮箱</h3>


***
<h3>欢迎帅哥美女打赏，在下感激不尽！！！</h3>

<div align=start>
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/wx.jpg" width="250" height="300" />

<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/zfb.jpg" width="250" height="300" />
</div>


```java


MIT License

Copyright (c) 2023 Yuang

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```