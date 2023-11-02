# 相册选择库 [![](https://jitpack.io/v/AnglePengCoding/android_picture.svg)](https://jitpack.io/#AnglePengCoding/android_picture)

<h3>
Photo album selection library (Kotlin version), single image similar to personal center avatar upload, multi image similar to opinion comments multi image upload, support for cropping, image rotation function, support for animation pop-up, etc</h3>

<h3>Add Dependency</h3>

```java

implementation'com.github.AnglePengCoding:android_picture:Tag'

```

<h3>Version differences v.1.0.0/v2.0.0</h3>

v.1.0.0 Using only a single image，v2.0.0In the future, multiple image selection has been added, and developers can choose the corresponding development mode。


<h3>Screen recording</h3>

<div align=start>
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/image.gif" width="250" height="300" />
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/camera.gif" width="250" height="300" />
</div>


<div align=start>
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/dtgif.gif" width="250" height="300" />
<img src="https://github.com/AnglePengCoding/android_picture/blob/main/GIF/dtgif2.gif" width="250" height="300" />
</div>

<h3>Single image function</h3>

```java


PictureChooseDialog.build(this){
        setSingleCameraRequestCode(10086)//Set up a single image camera RequestCode
        setSingleImageRequestCode(10096)//Set up a single photo album RequestCode
        show() //必设置
        }


```

<h3> onActivityResult </h3>

```java

@Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){
            if(resultCode==RESULT_OK){
            when(requestCode){
            10096->{ //Processing after selecting an album
            data?.data?.let{PictureUtils.initUCrop(this,it)}
            }

            10086->{ //Processing after selecting a camera
            PictureUtils.initUCrop(this,PictureUtils.imageUriFromCamera)
            }

            UCrop.REQUEST_CROP->{ //Processing after cropping
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

<h3>Multi image function</h3>

```java

adapter=GridImageAdapter(mContext)
        adapter.setAddPicClickListener(this)
        cameraBinding.mRecyclerView.layoutManager=FullyGridLayoutManager(mContext,4)
        cameraBinding.mRecyclerView.adapter=adapter


        override fun onAddPicClick(position:Int){
        PictureChooseDialog.build(this){
        setSingleOrMutableMode(true)//Enable multi image mode
        setMaxSelectNum(2)//maximum number
        openGalleryChooseMode(SelectMimeType.TYPE_ALL)//Multi Image Mode - Album
        openCameraChooseMode(SelectMimeType.TYPE_ALL)//Multi Image Mode - Camera
        setSelectedData(adapter.data)// Album Selected Data
        setImageMutableForResult(object:OnResultCallbackListener<LocalMedia> {
        //Multi Image Mode - Select Album Echo Data
        override fun onResult(result:ArrayList<LocalMedia>){
        adapter.setList(result)
        }

        override fun onCancel(){
        }

        })
        setCameraMutableForResult(object:OnResultCallbackListener<LocalMedia> {
        //Multi image mode - select camera echo data
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

<h3>Cropping function</h3>

```java

setUCropToolbarColor(R.color.teal_200)//Setting the cropping ToolbarColor is optional
        setUCropStatusBarColor(R.color.teal_200)//Setting the color of the cropping status bar is optional
        setMaxScaleMultiplier(2f)//Set the maximum zoom ratio for cropping
        setImageToCropBoundsAnimDuration(1000)//Animate images when switching scales 
        setShowCropFrame(true)//Set whether to display rectangular clipping boxes
        setCropGridStrokeWidth(R.color.teal_200)//Set the color of the crop box's horizontal and vertical lines
        setCropGridColumnCount(1)//Set the number of crop vertical lines
        setCropGridRowCount(2)//Set the number of crop lines

```

<h3>Pop up style and other auxiliary functions</h3>

```java

setFileTextSize(18f)//Set the font size of the dialog "album" button according to business needs
        setFileTextColor(Color.parseColor("#FF3700B3"))//Set the font color of the dialog "album" button according to business needs
        setCameraTextSize(15f)//Set the font size of the dialog "camera" button according to business needs
        setCameraTextColor(Color.parseColor("#ffcc0000"))//Set the font color of the dialog "camera" button according to business needs
        setAnimationDuration(2000)//Set the duration of the dialog animation according to business needs
        pictureDialogAnimation(PictureDialogAnimation.TranslateFromBottom)//Set dialog pop-up animation according to business needs
        setCameraDialogVisibility(true)//Set dialog camera buttons to hide according to business needs
        setFileDialogVisibility(true)//Set the dialog album button to hide according to business needs

<!--Other auxiliary functions-->
        setRecyclerAnimationMode()//Album List Animation Effects
        setLanguage()//Set Album Language
        setRequestedOrientation()//Set screen rotation direction
        setMaxVideoSelectNum()//Maximum number of video selections
        setMinVideoSelectNum()//Minimum number of video selections
        isPreviewAudio()//Does it support audio preview
        isPreviewImage()//Does it support previewing images
        isPreviewFullScreenMode()//Preview click full screen effect
        isWithSelectVideoImage()//Does it support video and image selection
        isEmptyResultReturn()//Support unselected return
        isCameraRotateImage()//Does the photo correct the rotation of the image
        isAutoVideoPlay()//Whether the preview video will automatically play
        isFastSlidingSelect()//Quick slide selection
        isDirectReturnSingle()//Do you want to return immediately when selecting a single option
        setCameraImageFormat()//Photo output format
        setCameraImageFormatForQ()//Photo output format, Android Q or above
        setCameraVideoFormat()//Photo Video Output Format
        setCameraInterceptListener()//Intercept camera events and implement custom cameras


```

<h3>Intentional description</h3>

```java

The camera uses the PictureSelector album library, but due to the incompatibility of the permission library with Android Q and above versions, permission compatibility has been processed!

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
