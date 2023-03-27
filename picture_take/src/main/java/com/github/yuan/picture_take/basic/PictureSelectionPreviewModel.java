package com.github.yuan.picture_take.basic;

import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.github.yuan.picture_take.R;
import com.github.yuan.picture_take.config.PictureConfig;
import com.github.yuan.picture_take.config.PictureSelectionConfig;
import com.github.yuan.picture_take.config.SelectMimeType;
import com.github.yuan.picture_take.engine.ImageEngine;
import com.github.yuan.picture_take.engine.VideoPlayerEngine;
import com.github.yuan.picture_take.entity.LocalMedia;
import com.github.yuan.picture_take.fragment.PictureSelectorPreviewFragment;
import com.github.yuan.picture_take.interfaces.OnCustomLoadingListener;
import com.github.yuan.picture_take.interfaces.OnExternalPreviewEventListener;
import com.github.yuan.picture_take.interfaces.OnInjectActivityPreviewListener;
import com.github.yuan.picture_take.interfaces.OnInjectLayoutResourceListener;
import com.github.yuan.picture_take.magical.BuildRecycleItemViewParams;
import com.github.yuan.picture_take.manager.SelectedManager;
import com.github.yuan.picture_take.style.PictureSelectorStyle;
import com.github.yuan.picture_take.style.PictureWindowAnimationStyle;
import com.github.yuan.picture_take.utils.ActivityCompatHelper;
import com.github.yuan.picture_take.utils.DensityUtil;
import com.github.yuan.picture_take.utils.DoubleUtils;

import java.util.ArrayList;

/**
 * @author：luck
 * @date：2022/1/17 6:10 下午
 * @describe：PictureSelectionPreviewModel
 */
public final class PictureSelectionPreviewModel {
    private final PictureSelectionConfig selectionConfig;
    private final PictureSelector selector;

    public PictureSelectionPreviewModel(PictureSelector selector) {
        this.selector = selector;
        selectionConfig = PictureSelectionConfig.getCleanInstance();
        selectionConfig.isPreviewZoomEffect = false;
    }


    /**
     * Image Load the engine
     *
     * @param engine Image Load the engine
     *               <p>
     *               <a href="https://github.com/LuckSiege/PictureSelector/blob/version_component/app/src/main/java/com/luck/pictureselector/GlideEngine.java">
     *               </p>
     * @return
     */
    public PictureSelectionPreviewModel setImageEngine(ImageEngine engine) {
        PictureSelectionConfig.imageEngine = engine;
        return this;
    }

    /**
     * Set up player engine
     *  <p>
     *   Used to preview custom player instances，MediaPlayer by default
     *  </p>
     * @param engine
     * @return
     */
    public PictureSelectionPreviewModel setVideoPlayerEngine(VideoPlayerEngine engine) {
        PictureSelectionConfig.videoPlayerEngine = engine;
        return this;
    }

    /**
     * PictureSelector theme style settings
     *
     * @param uiStyle <p>
     *                Use {@link  PictureSelectorStyle
     *                It consists of the following parts and can be set separately}
     *                {@link com.luck.picture.lib.style.TitleBarStyle}
     *                {@link com.luck.picture.lib.style.AlbumWindowStyle}
     *                {@link com.luck.picture.lib.style.SelectMainStyle}
     *                {@link com.luck.picture.lib.style.BottomNavBarStyle}
     *                {@link com.luck.picture.lib.style.PictureWindowAnimationStyle}
     *                <p/>
     * @return PictureSelectorStyle
     */
    public PictureSelectionPreviewModel setSelectorUIStyle(PictureSelectorStyle uiStyle) {
        if (uiStyle != null) {
            PictureSelectionConfig.selectorStyle = uiStyle;
        }
        return this;
    }

    /**
     * Set App Language
     *
     * @param language {@link LanguageConfig}
     * @return PictureSelectionModel
     */
    public PictureSelectionPreviewModel setLanguage(int language) {
        selectionConfig.language = language;
        return this;
    }

    /**
     * Set App default Language
     *
     * @param defaultLanguage default language {@link LanguageConfig}
     * @return PictureSelectionModel
     */
    public PictureSelectionPreviewModel setDefaultLanguage(int defaultLanguage) {
        selectionConfig.defaultLanguage = defaultLanguage;
        return this;
    }

    /**
     * Intercept custom inject layout events, Users can implement their own layout
     * on the premise that the view ID must be consistent
     *
     * @param listener
     * @return
     */
    public PictureSelectionPreviewModel setInjectLayoutResourceListener(OnInjectLayoutResourceListener listener) {
        selectionConfig.isInjectLayoutResource = listener != null;
        PictureSelectionConfig.onLayoutResourceListener = listener;
        return this;
    }

    /**
     * View lifecycle listener
     *
     * @param viewLifecycle
     * @return
     */
    public PictureSelectionPreviewModel setAttachViewLifecycle(IBridgeViewLifecycle viewLifecycle) {
        PictureSelectionConfig.viewLifecycle = viewLifecycle;
        return this;
    }

    /**
     * Preview Full Screen Mode
     *
     * @param isFullScreenModel
     * @return
     */
    public PictureSelectionPreviewModel isPreviewFullScreenMode(boolean isFullScreenModel) {
        selectionConfig.isPreviewFullScreenMode = isFullScreenModel;
        return this;
    }

    /**
     * Preview Zoom Effect Mode
     *
     * @param isPreviewZoomEffect
     * @param listView  Use {@link RecyclerView,ListView}
     */
    public PictureSelectionPreviewModel isPreviewZoomEffect(boolean isPreviewZoomEffect, ViewGroup listView) {
        return isPreviewZoomEffect(isPreviewZoomEffect, selectionConfig.isPreviewFullScreenMode, listView);
    }

    /**
     * It is forbidden to correct or synchronize the width and height of the video
     *
     * @param isEnableVideoSize Use {@link .isSyncWidthAndHeight()}
     */
    @Deprecated
    public PictureSelectionPreviewModel isEnableVideoSize(boolean isEnableVideoSize) {
        selectionConfig.isSyncWidthAndHeight = isEnableVideoSize;
        return this;
    }

    /**
     * It is forbidden to correct or synchronize the width and height of the video
     *
     * @param isSyncWidthAndHeight
     * @return
     */
    public PictureSelectionPreviewModel isSyncWidthAndHeight(boolean isSyncWidthAndHeight) {
        selectionConfig.isSyncWidthAndHeight = isSyncWidthAndHeight;
        return this;
    }

    /**
     * Preview Zoom Effect Mode
     *
     * @param isPreviewZoomEffect
     * @param isFullScreenModel
     * @param listView   Use {@link RecyclerView,ListView}
     */
    public PictureSelectionPreviewModel isPreviewZoomEffect(boolean isPreviewZoomEffect, boolean isFullScreenModel, ViewGroup listView) {
        if (listView instanceof RecyclerView || listView instanceof ListView) {
            if (isPreviewZoomEffect) {
                if (isFullScreenModel) {
                    BuildRecycleItemViewParams.generateViewParams(listView, 0);
                } else {
                    BuildRecycleItemViewParams.generateViewParams(listView, DensityUtil.getStatusBarHeight(selector.getActivity()));
                }
            }
            selectionConfig.isPreviewZoomEffect = isPreviewZoomEffect;
        } else {
            throw new IllegalArgumentException(listView.getClass().getCanonicalName()
                    + " Must be " + RecyclerView.class + " or " + ListView.class);
        }
        return this;
    }

    /**
     * Whether to play video automatically when previewing
     *
     * @param isAutoPlay
     * @return
     */
    public PictureSelectionPreviewModel isAutoVideoPlay(boolean isAutoPlay) {
        selectionConfig.isAutoVideoPlay = isAutoPlay;
        return this;
    }

    /**
     * loop video
     *
     * @param isLoopAutoPlay
     * @return
     */
    public PictureSelectionPreviewModel isLoopAutoVideoPlay(boolean isLoopAutoPlay) {
        selectionConfig.isLoopAutoPlay = isLoopAutoPlay;
        return this;
    }

    /**
     * The video supports pause and resume
     *
     * @param isPauseResumePlay
     * @return
     */
    public PictureSelectionPreviewModel isVideoPauseResumePlay(boolean isPauseResumePlay) {
        selectionConfig.isPauseResumePlay = isPauseResumePlay;
        return this;
    }

    /**
     * Intercept external preview click events, and users can implement their own preview framework
     *
     * @param listener
     * @return
     */
    public PictureSelectionPreviewModel setExternalPreviewEventListener(OnExternalPreviewEventListener listener) {
        PictureSelectionConfig.onExternalPreviewEventListener = listener;
        return this;
    }

    /**
     * startActivityPreview(); Preview mode, custom preview callback
     *
     * @param listener
     * @return
     */
    public PictureSelectionPreviewModel setInjectActivityPreviewFragment(OnInjectActivityPreviewListener listener) {
        PictureSelectionConfig.onInjectActivityPreviewListener = listener;
        return this;
    }

    /**
     * Custom show loading dialog
     *
     * @param listener
     * @return
     */
    public PictureSelectionPreviewModel setCustomLoadingListener(OnCustomLoadingListener listener) {
        PictureSelectionConfig.onCustomLoadingListener = listener;
        return this;
    }

    /**
     * @param isHidePreviewDownload Previews do not show downloads
     * @return
     */
    public PictureSelectionPreviewModel isHidePreviewDownload(boolean isHidePreviewDownload) {
        selectionConfig.isHidePreviewDownload = isHidePreviewDownload;
        return this;
    }

    /**
     * preview LocalMedia
     *
     * @param currentPosition
     * @param isDisplayDelete
     * @param list
     */
    public void startFragmentPreview(int currentPosition, boolean isDisplayDelete, ArrayList<LocalMedia> list) {
        startFragmentPreview(null, currentPosition, isDisplayDelete, list);
    }

    /**
     * preview LocalMedia
     *
     * @param previewFragment PictureSelectorPreviewFragment
     * @param currentPosition current position
     * @param isDisplayDelete if visible delete
     * @param list            preview data
     */
    public void startFragmentPreview(PictureSelectorPreviewFragment previewFragment, int currentPosition, boolean isDisplayDelete, ArrayList<LocalMedia> list) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                throw new NullPointerException("Activity cannot be null");
            }
            if (PictureSelectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw new NullPointerException("imageEngine is null,Please implement ImageEngine");
            }
            if (list == null || list.size() == 0) {
                throw new NullPointerException("preview data is null");
            }
            FragmentManager fragmentManager = null;
            if (activity instanceof FragmentActivity) {
                fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
            }
            if (fragmentManager == null) {
                throw new NullPointerException("FragmentManager cannot be null");
            }
            String fragmentTag;
            if (previewFragment != null) {
                fragmentTag = previewFragment.getFragmentTag();
            } else {
                fragmentTag = PictureSelectorPreviewFragment.TAG;
                previewFragment = PictureSelectorPreviewFragment.newInstance();
            }
            if (ActivityCompatHelper.checkFragmentNonExits((FragmentActivity) activity, fragmentTag)) {
                ArrayList<LocalMedia> previewData = new ArrayList<>(list);
                previewFragment.setExternalPreviewData(currentPosition, previewData.size(), previewData, isDisplayDelete);
                FragmentInjectManager.injectSystemRoomFragment(fragmentManager, fragmentTag, previewFragment);
            }
        }
    }

    /**
     * preview LocalMedia
     *
     * @param currentPosition current position
     * @param isDisplayDelete if visible delete
     * @param list            preview data
     *                        <p>
     *                        You can do it {@link .setInjectActivityPreviewFragment()} interface, custom Preview
     *                        </p>
     */
    public void startActivityPreview(int currentPosition, boolean isDisplayDelete, ArrayList<LocalMedia> list) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Activity activity = selector.getActivity();
            if (activity == null) {
                throw new NullPointerException("Activity cannot be null");
            }
            if (PictureSelectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw new NullPointerException("imageEngine is null,Please implement ImageEngine");
            }
            if (list == null || list.size() == 0) {
                throw new NullPointerException("preview data is null");
            }
            Intent intent = new Intent(activity, PictureSelectorTransparentActivity.class);
            SelectedManager.addSelectedPreviewResult(list);
            intent.putExtra(PictureConfig.EXTRA_EXTERNAL_PREVIEW, true);
            intent.putExtra(PictureConfig.EXTRA_MODE_TYPE_SOURCE, PictureConfig.MODE_TYPE_EXTERNAL_PREVIEW_SOURCE);
            intent.putExtra(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, currentPosition);
            intent.putExtra(PictureConfig.EXTRA_EXTERNAL_PREVIEW_DISPLAY_DELETE, isDisplayDelete);
            Fragment fragment = selector.getFragment();
            if (fragment != null) {
                fragment.startActivity(intent);
            } else {
                activity.startActivity(intent);
            }
            if (selectionConfig.isPreviewZoomEffect) {
                activity.overridePendingTransition(R.anim.ps_anim_fade_in, R.anim.ps_anim_fade_in);
            } else {
                PictureWindowAnimationStyle windowAnimationStyle = PictureSelectionConfig.selectorStyle.getWindowAnimationStyle();
                activity.overridePendingTransition(windowAnimationStyle.activityEnterAnimation, R.anim.ps_anim_fade_in);
            }
        }
    }

}
