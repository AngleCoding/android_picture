package com.github.yuan.picture_take.basic;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.github.yuan.picture_take.R;
import com.github.yuan.picture_take.config.PictureConfig;
import com.github.yuan.picture_take.config.PictureSelectionConfig;
import com.github.yuan.picture_take.entity.LocalMedia;
import com.github.yuan.picture_take.fragment.PictureOnlyCameraFragment;
import com.github.yuan.picture_take.fragment.PictureSelectorPreviewFragment;
import com.github.yuan.picture_take.fragment.PictureSelectorSystemFragment;
import com.github.yuan.picture_take.immersive.ImmersiveManager;
import com.github.yuan.picture_take.manager.SelectedManager;
import com.github.yuan.picture_take.style.PictureWindowAnimationStyle;
import com.github.yuan.picture_take.style.SelectMainStyle;
import com.github.yuan.picture_take.utils.StyleUtils;

import java.util.ArrayList;

/**
 * @author：luck
 * @date：2022/2/10 6:07 下午
 * @describe：PictureSelectorTransparentActivity
 */
public class PictureSelectorTransparentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        immersive();
        setContentView(R.layout.ps_empty);
        if (isExternalPreview()) {
            // TODO ignore
        } else {
            setActivitySize();
        }
        setupFragment();
    }

    private boolean isExternalPreview() {
        int modeTypeSource = getIntent().getIntExtra(PictureConfig.EXTRA_MODE_TYPE_SOURCE, 0);
        return modeTypeSource == PictureConfig.MODE_TYPE_EXTERNAL_PREVIEW_SOURCE;
    }

    private void immersive() {
        if (PictureSelectionConfig.selectorStyle == null) {
            PictureSelectionConfig.getInstance();
        }
        SelectMainStyle mainStyle = PictureSelectionConfig.selectorStyle.getSelectMainStyle();
        int statusBarColor = mainStyle.getStatusBarColor();
        int navigationBarColor = mainStyle.getNavigationBarColor();
        boolean isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack();
        if (!StyleUtils.checkStyleValidity(statusBarColor)) {
            statusBarColor = ContextCompat.getColor(this, R.color.ps_color_grey);
        }
        if (!StyleUtils.checkStyleValidity(navigationBarColor)) {
            navigationBarColor = ContextCompat.getColor(this, R.color.ps_color_grey);
        }
        ImmersiveManager.immersiveAboveAPI23(this, statusBarColor, navigationBarColor, isDarkStatusBarBlack);
    }

    private void setupFragment() {
        String fragmentTag;
        Fragment targetFragment = null;
        int modeTypeSource = getIntent().getIntExtra(PictureConfig.EXTRA_MODE_TYPE_SOURCE, 0);
        if (modeTypeSource == PictureConfig.MODE_TYPE_SYSTEM_SOURCE) {
            fragmentTag = PictureSelectorSystemFragment.TAG;
            targetFragment = PictureSelectorSystemFragment.newInstance();
        } else if (modeTypeSource == PictureConfig.MODE_TYPE_EXTERNAL_PREVIEW_SOURCE) {
            if (PictureSelectionConfig.onInjectActivityPreviewListener != null) {
                targetFragment = PictureSelectionConfig.onInjectActivityPreviewListener.onInjectPreviewFragment();
            }
            if (targetFragment != null) {
                fragmentTag = ((PictureSelectorPreviewFragment) targetFragment).getFragmentTag();
            } else {
                fragmentTag = PictureSelectorPreviewFragment.TAG;
                targetFragment = PictureSelectorPreviewFragment.newInstance();
            }
            int position = getIntent().getIntExtra(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, 0);
            ArrayList<LocalMedia> previewResult = SelectedManager.getSelectedPreviewResult();
            ArrayList<LocalMedia> previewData = new ArrayList<>(previewResult);
            boolean isDisplayDelete = getIntent()
                    .getBooleanExtra(PictureConfig.EXTRA_EXTERNAL_PREVIEW_DISPLAY_DELETE, false);
            ((PictureSelectorPreviewFragment) targetFragment).setExternalPreviewData(position, previewData.size(), previewData, isDisplayDelete);
        } else {
            fragmentTag = PictureOnlyCameraFragment.TAG;
            targetFragment = PictureOnlyCameraFragment.newInstance();
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
        FragmentInjectManager.injectSystemRoomFragment(supportFragmentManager, fragmentTag, targetFragment);
    }

    @SuppressLint("RtlHardcoded")
    private void setActivitySize() {
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
    }

    @Override
    public void finish() {
        super.finish();
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        int modeTypeSource = getIntent().getIntExtra(PictureConfig.EXTRA_MODE_TYPE_SOURCE, 0);
        if (modeTypeSource == PictureConfig.MODE_TYPE_EXTERNAL_PREVIEW_SOURCE && !config.isPreviewZoomEffect) {
            PictureWindowAnimationStyle windowAnimationStyle = PictureSelectionConfig.selectorStyle.getWindowAnimationStyle();
            overridePendingTransition(0, windowAnimationStyle.activityExitAnimation);
        } else {
            overridePendingTransition(0, R.anim.ps_anim_fade_out);
        }
    }
}
