package com.github.yuan.picture_take.engine;

import android.content.Context;

import com.github.yuan.picture_take.entity.LocalMedia;
import com.github.yuan.picture_take.interfaces.OnCallbackIndexListener;

/**
 * @author：luck
 * @date：2021/11/23 8:23 下午
 * @describe：SandboxFileEngine Use {@link UriToFileTransformEngine}
 */
@Deprecated
public interface SandboxFileEngine {

    /**
     * Custom Sandbox File engine
     * <p>
     * Users can implement this interface, and then access their own sandbox framework to plug
     * the sandbox path into the {@link LocalMedia} object;
     *
     * </p>
     *
     * <p>
     * 1、LocalMedia media = new LocalMedia();
     * media.setSandboxPath("Your sandbox path");
     * </p>
     * <p>
     * 2、listener.onCall( "you result" );
     * </p>
     *
     * @param context              context
     * @param isOriginalImage The original drawing needs to be processed
     * @param index                The location of the resource in the result queue
     * @param media                LocalMedia
     * @param listener
     */
    void onStartSandboxFileTransform(Context context, boolean isOriginalImage,
                                     int index, LocalMedia media,
                                     OnCallbackIndexListener<LocalMedia> listener);

}
