package com.github.yuan.picture_take.permissions;

import android.Manifest;

import com.github.yuan.picture_take.config.SelectMimeType;
import com.github.yuan.picture_take.utils.SdkVersionUtils;

/**
 * @author：luck
 * @date：2021/12/11 8:24 下午
 * @describe：PermissionConfig
 */
public class PermissionConfig {

    public static final String READ_MEDIA_AUDIO = "android.permission.READ_MEDIA_AUDIO";
    public static final String READ_MEDIA_IMAGES = "android.permission.READ_MEDIA_IMAGES";
    public static final String READ_MEDIA_VIDEO = "android.permission.READ_MEDIA_VIDEO";

    /**
     * 当前申请权限
     */
    public static String[] CURRENT_REQUEST_PERMISSION = new String[]{};

    /**
     * 相机权限
     */
    public final static String[] CAMERA = new String[]{Manifest.permission.CAMERA};

    /**
     * 获取外部读取权限
     */
    public static String[] getReadPermissionArray(int chooseMode) {
        if (SdkVersionUtils.isTIRAMISU()) {
            if (chooseMode == SelectMimeType.ofImage()) {
                return new String[]{READ_MEDIA_IMAGES, Manifest.permission.READ_EXTERNAL_STORAGE};
            } else if (chooseMode == SelectMimeType.ofVideo()) {
                return new String[]{READ_MEDIA_VIDEO, Manifest.permission.READ_EXTERNAL_STORAGE};
            } else if (chooseMode == SelectMimeType.ofAudio()) {
                return new String[]{READ_MEDIA_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE};
            } else {
                return new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, Manifest.permission.READ_EXTERNAL_STORAGE};
            }
        }
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    }

}
