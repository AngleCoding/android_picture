package com.github.yuan.picture_take.utils;

import android.content.Context;
import android.net.Uri;


import com.github.yuan.picture_take.basic.PictureContentResolver;
import com.github.yuan.picture_take.config.PictureMimeType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author：luck
 * @date：2019-11-08 19:25
 * @describe：SandboxTransformUtils
 */
public class SandboxTransformUtils {

    /**
     * 把外部目录下的图片拷贝至沙盒内
     *
     * @param ctx
     * @param url
     * @param mineType
     * @return
     */
    public static String copyPathToSandbox(Context ctx, String url, String mineType) {
        return copyPathToSandbox(ctx, url, mineType, "");
    }

    /**
     * 把外部目录下的图片拷贝至沙盒内
     *
     * @param ctx
     * @param url
     * @param mineType
     * @param customFileName
     * @return
     */
    public static String copyPathToSandbox(Context ctx, String url, String mineType, String customFileName) {
        try {
            if (PictureMimeType.isHasHttp(url)){
                return null;
            }
            InputStream inputStream;
            String sandboxPath = PictureFileUtils.createFilePath(ctx,  mineType, customFileName);
            if (PictureMimeType.isContent(url)) {
                inputStream = PictureContentResolver.getContentResolverOpenInputStream(ctx, Uri.parse(url));
            } else {
                inputStream = new FileInputStream(url);
            }
            boolean copyFileSuccess = PictureFileUtils.writeFileFromIS(inputStream, new FileOutputStream(sandboxPath));
            if (copyFileSuccess) {
                return sandboxPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
