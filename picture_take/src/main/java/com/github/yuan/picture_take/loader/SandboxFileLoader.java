package com.github.yuan.picture_take.loader;

import android.content.Context;
import android.text.TextUtils;


import com.github.yuan.picture_take.config.PictureMimeType;
import com.github.yuan.picture_take.config.PictureSelectionConfig;
import com.github.yuan.picture_take.config.SelectMimeType;
import com.github.yuan.picture_take.entity.LocalMedia;
import com.github.yuan.picture_take.entity.LocalMediaFolder;
import com.github.yuan.picture_take.entity.MediaExtraInfo;
import com.github.yuan.picture_take.utils.MediaUtils;
import com.github.yuan.picture_take.utils.SdkVersionUtils;
import com.github.yuan.picture_take.utils.SortUtils;
import com.github.yuan.picture_take.utils.ValueOf;

import java.io.File;
import java.io.FileFilter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * @author：luck
 * @date：2021/11/10 5:40 下午
 * @describe：SandboxFileLoader
 */
public final class SandboxFileLoader {

    /**
     * 查询应用内部目录的图片
     *
     * @param context    上下文
     * @param sandboxDir 资源目标路径
     */
    public static LocalMediaFolder loadInAppSandboxFolderFile(Context context, String sandboxDir) {
        ArrayList<LocalMedia> list = loadInAppSandboxFile(context, sandboxDir);
        LocalMediaFolder folder = null;
        if (list != null && list.size() > 0) {
            SortUtils.sortLocalMediaAddedTime(list);
            LocalMedia firstMedia = list.get(0);
            folder = new LocalMediaFolder();
            folder.setFolderName(firstMedia.getParentFolderName());
            folder.setFirstImagePath(firstMedia.getPath());
            folder.setFirstMimeType(firstMedia.getMimeType());
            folder.setBucketId(firstMedia.getBucketId());
            folder.setFolderTotalNum(list.size());
            folder.setData(list);
        }
        return folder;
    }


    /**
     * 查询应用内部目录的图片
     *
     * @param context    上下文
     * @param sandboxDir 资源目标路径
     */
    public static ArrayList<LocalMedia> loadInAppSandboxFile(Context context, String sandboxDir) {
        if (TextUtils.isEmpty(sandboxDir)) {
            return null;
        }
        ArrayList<LocalMedia> list = new ArrayList<>();
        File sandboxFile = new File(sandboxDir);
        if (sandboxFile.exists()) {
            File[] files = sandboxFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return !file.isDirectory();
                }
            });
            if (files == null) {
                return list;
            }
            PictureSelectionConfig config = PictureSelectionConfig.getInstance();
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            for (File f : files) {
                String mimeType = MediaUtils.getMimeTypeFromMediaUrl(f.getAbsolutePath());
                if (config.chooseMode == SelectMimeType.ofImage()) {
                    if (!PictureMimeType.isHasImage(mimeType)) {
                        continue;
                    }
                } else if (config.chooseMode == SelectMimeType.ofVideo()) {
                    if (!PictureMimeType.isHasVideo(mimeType)) {
                        continue;
                    }
                } else if (config.chooseMode == SelectMimeType.ofAudio()) {
                    if (!PictureMimeType.isHasAudio(mimeType)) {
                        continue;
                    }
                }

                if (config.queryOnlyList != null
                        && config.queryOnlyList.size() > 0
                        && !config.queryOnlyList.contains(mimeType)) {
                    continue;
                }

                if (!config.isGif) {
                    if (PictureMimeType.isHasGif(mimeType)) {
                        continue;
                    }
                }
                String absolutePath = f.getAbsolutePath();
                long size = f.length();
                if (size <= 0) {
                    continue;
                }
                long id;
                if (md != null) {
                    md.update(absolutePath.getBytes());
                    id = new BigInteger(1, md.digest()).longValue();
                } else {
                    id = f.lastModified() / 1000;
                }
                long bucketId = ValueOf.toLong(sandboxFile.getName().hashCode());
                long dateAdded = f.lastModified() / 1000;
                long duration;
                int width, height;
                if (PictureMimeType.isHasVideo(mimeType)) {
                    MediaExtraInfo videoSize = MediaUtils.getVideoSize(context, absolutePath);
                    width = videoSize.getWidth();
                    height = videoSize.getHeight();
                    duration = videoSize.getDuration();
                } else if (PictureMimeType.isHasAudio(mimeType)) {
                    MediaExtraInfo audioSize = MediaUtils.getAudioSize(context, absolutePath);
                    width = audioSize.getWidth();
                    height = audioSize.getHeight();
                    duration = audioSize.getDuration();
                } else {
                    MediaExtraInfo imageSize = MediaUtils.getImageSize(context, absolutePath);
                    width = imageSize.getWidth();
                    height = imageSize.getHeight();
                    duration = 0L;
                }

                if (PictureMimeType.isHasVideo(mimeType) || PictureMimeType.isHasAudio(mimeType)) {
                    if (config.filterVideoMinSecond > 0 && duration < config.filterVideoMinSecond) {
                        // If you set the minimum number of seconds of video to display
                        continue;
                    }
                    if (config.filterVideoMaxSecond > 0 && duration > config.filterVideoMaxSecond) {
                        // If you set the maximum number of seconds of video to display
                        continue;
                    }
                    if (duration == 0) {
                        //If the length is 0, the corrupted video is processed and filtered out
                        continue;
                    }
                }
                LocalMedia media = LocalMedia.create();
                media.setId(id);
                media.setPath(absolutePath);
                media.setRealPath(absolutePath);
                media.setFileName(f.getName());
                media.setParentFolderName(sandboxFile.getName());
                media.setDuration(duration);
                media.setChooseModel(config.chooseMode);
                media.setMimeType(mimeType);
                media.setWidth(width);
                media.setHeight(height);
                media.setSize(size);
                media.setBucketId(bucketId);
                media.setDateAddedTime(dateAdded);
                if (PictureSelectionConfig.onQueryFilterListener != null) {
                    if (PictureSelectionConfig.onQueryFilterListener.onFilter(media)) {
                        continue;
                    }
                }
                media.setSandboxPath(SdkVersionUtils.isQ() ? absolutePath : null);
                list.add(media);
            }
        }
        return list;
    }
}
