package com.github.yuan.picture_take.engine;


import androidx.fragment.app.Fragment;


import com.github.yuan.picture_take.entity.LocalMedia;

import java.util.ArrayList;

/**
 * @author：luck
 * @date：2021/11/23 8:13 下午
 * Please Use {@link CropFileEngine}
 * @describe：CropEngine
 */
public interface CropEngine {

    /**
     * Custom crop image engine
     * <p>
     * Users can implement this interface, and then access their own crop framework to plug
     * the crop path into the {@link LocalMedia} object;
     * <p>
     * 1、If Activity start crop use context;
     * activity.startActivityForResult({@link Crop.REQUEST_CROP})
     * <p>
     * 2、If Fragment start crop use fragment;
     * fragment.startActivityForResult({@link Crop.REQUEST_CROP})
     * <p>
     * 3、If you implement your own clipping function, you need to assign the following values in
     * Intent.putExtra {@link CustomIntentKey}
     *
     * </p>
     *
     * @param fragment          Fragment
     * @param currentLocalMedia current crop data
     * @param dataSource        crop data
     * @param requestCode       Activity result code or fragment result code
     */
    void onStartCrop(Fragment fragment, LocalMedia currentLocalMedia, ArrayList<LocalMedia> dataSource, int requestCode);

}
