package com.github.yuan.picture_take.engine;

import android.content.Context;

import com.github.yuan.picture_take.entity.LocalMedia;
import com.github.yuan.picture_take.entity.LocalMediaFolder;
import com.github.yuan.picture_take.interfaces.OnQueryAlbumListener;
import com.github.yuan.picture_take.interfaces.OnQueryAllAlbumListener;
import com.github.yuan.picture_take.interfaces.OnQueryDataResultListener;

/**
 * @author：luck
 * @date：2021/12/5 7:31 下午
 * @describe：Custom data loader engine
 */
@Deprecated
public interface ExtendLoaderEngine {
    /**
     * load all album list data
     * <p>
     * Users can implement some interfaces to access their own query data,
     * provided that they comply with the {@link LocalMediaFolder} standard
     * </p>
     *
     * <p>
     * query.onComplete(List<LocalMediaFolder> result);
     * </p>
     *
     * @param context
     * @param query
     */
    void loadAllAlbumData(Context context, OnQueryAllAlbumListener<LocalMediaFolder> query);


    /**
     * load resources in the specified directory
     * <p>
     * Users can implement some interfaces to access their own query data,
     * provided that they comply with the {@link LocalMediaFolder} standard
     * </p>
     *
     * <p>
     * query.onComplete(LocalMediaFolder result);
     * </p>
     *
     * @param context
     * @param query
     */
    void loadOnlyInAppDirAllMediaData(Context context, OnQueryAlbumListener<LocalMediaFolder> query);


    /**
     * load the first item of data in the album list
     * {@link PictureSelectionConfig} Valid only in isPageStrategy mode
     * <p>
     * Users can implement some interfaces to access their own query data,
     * provided that they comply with the {@link LocalMedia} standard
     * </p>
     * <p>
     * query.onComplete(List<LocalMedia> result, int currentPage, boolean isHasMore);
     * <p>
     * <p>
     * isHasMore; Whether there is more data needs to be controlled by developers
     * </p>
     *
     * @param context
     * @param bucketId Album ID
     * @param page     first page
     * @param pageSize How many entries per page
     * @param query
     */
    void loadFirstPageMediaData(Context context, long bucketId, int page, int pageSize,
                                OnQueryDataResultListener<LocalMedia> query);


    /**
     * load the first item of data in the album list
     * {@link PictureSelectionConfig} Valid only in isPageStrategy mode
     * <p>
     * <p>
     * Users can implement some interfaces to access their own query data,
     * provided that they comply with the {@link LocalMedia} standard
     * </p>
     * query.onComplete(List<LocalMedia> result, int currentPage, boolean isHasMore);
     * <p>
     * <p>
     * currentPage; Represents the current page number
     * isHasMore; Whether there is more data needs to be controlled by developers
     * </p>
     *
     * @param context
     * @param bucketId Album ID
     * @param page     Current page number
     * @param limit    query limit
     * @param pageSize How many entries per page
     * @param query
     */
    void loadMoreMediaData(Context context, long bucketId, int page, int limit, int pageSize,
                           OnQueryDataResultListener<LocalMedia> query);
}
