package com.github.yuan.picture_take.interfaces;

import android.content.Context;

import androidx.annotation.Nullable;

import com.github.yuan.picture_take.config.PictureSelectionConfig;
import com.github.yuan.picture_take.entity.LocalMedia;


/**
 * @author：luck
 * @date：2022/1/8 2:12 下午
 * @describe：OnSelectLimitTipsListener
 */
public interface OnSelectLimitTipsListener {
    /**
     * Custom limit tips
     *
     * @param media Current Selection {@link LocalMedia}
     * @param config    PictureSelectionConfig
     * @param limitType Use {@link SelectLimitType}
     * @return If true is returned, the user needs to customize the implementation prompt content，
     * Otherwise, use the system default prompt
     */
    boolean onSelectLimitTips(Context context, @Nullable LocalMedia media, PictureSelectionConfig config, int limitType);
}
