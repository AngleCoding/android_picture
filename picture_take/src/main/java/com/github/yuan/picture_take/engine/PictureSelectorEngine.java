package com.github.yuan.picture_take.engine;

import android.view.View;

import com.github.yuan.picture_take.basic.IBridgeLoaderFactory;
import com.github.yuan.picture_take.entity.LocalMedia;
import com.github.yuan.picture_take.interfaces.OnInjectLayoutResourceListener;
import com.github.yuan.picture_take.interfaces.OnResultCallbackListener;


/**
 * @author：luck
 * @date：2020/4/22 11:36 AM
 * @describe：PictureSelectorEngine
 */
public interface PictureSelectorEngine {

    /**
     * Create ImageLoad Engine
     *
     * @return
     */
    ImageEngine createImageLoaderEngine();

    /**
     * Create compress Engine
     *
     * @return
     */
    CompressEngine createCompressEngine();

    /**
     * Create compress Engine
     *
     * @return
     */
    CompressFileEngine createCompressFileEngine();

    /**
     * Create loader data Engine
     *
     * @return
     */
    ExtendLoaderEngine createLoaderDataEngine();

    /**
     * Create video player  Engine
     *
     * @return
     */
    VideoPlayerEngine createVideoPlayerEngine();

    /**
     * Create loader data Engine
     *
     * @return
     */
    IBridgeLoaderFactory onCreateLoader();

    /**
     * Create SandboxFileEngine  Engine
     *
     * @return
     */
    SandboxFileEngine createSandboxFileEngine();

    /**
     * Create UriToFileTransformEngine  Engine
     *
     * @return
     */
    UriToFileTransformEngine createUriToFileTransformEngine();

    /**
     * Create LayoutResource  Listener
     *
     * @return
     */
    OnInjectLayoutResourceListener createLayoutResourceListener();

    /**
     * Create Result Listener
     *
     * @return
     */
    OnResultCallbackListener<LocalMedia> getResultCallbackListener();
}
