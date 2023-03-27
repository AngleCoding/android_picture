package com.github.yuan.picture_take.basic;


import com.github.yuan.picture_take.loader.IBridgeMediaLoader;

/**
 * @author：luck
 * @date：2022/6/10 9:37 上午
 * @describe：IBridgeLoaderFactory
 */
public interface IBridgeLoaderFactory {
    /**
     * CreateLoader
     */
    IBridgeMediaLoader onCreateLoader();
}
