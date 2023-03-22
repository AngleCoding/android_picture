# android_picture
图片上传，相册选择和相机拍照，裁剪功能，支持动画弹窗，裁剪设置例如状态栏颜色，缩放比例，裁剪框横竖颜色，横竖线数量设置等


<h3>如何使用？</h3>

```java
  

```

```java

    findViewById<Button>(R.id.mBtTake).setOnClickListener {
        PictureChooseDialog.build(this) {
        setUCropToolbarColor(R.color.teal_200)//设置裁剪ToolbarColor   可不设置
        setUCropStatusBarColor(R.color.teal_200)//设置裁剪状态栏颜色   可不设置
        setMaxScaleMultiplier(2f)//裁剪最大缩放比例  可不设置
        setImageToCropBoundsAnimDuration(1000)//设置图片在切换比例时的动画  可不设置
        setShowCropFrame(true)//设置是否展示矩形裁剪框  可不设置
        setCropGridStrokeWidth(R.color.teal_200)//设置裁剪框横竖线的颜色 可不设置
        setCropGridColumnCount(1)//设置裁剪竖线的数量 可不设置
        setCropGridRowCount(2)//设置裁剪横线的数量 可不设置
        setAnimationDuration(2000)//设置dialog动画时长 可不设置
        pictureDialogAnimation(PictureDialogAnimation.TranslateFromBottom)//设置dialog弹窗动画  可不设置
        setCameraDialogVisibility(true)//设置dialog 相机按钮隐藏  根据业务需求
        setFileDialogVisibility(true)//设置dialog 相册按钮隐藏  根据业务需求
        show() //必设置
        }
        }


```