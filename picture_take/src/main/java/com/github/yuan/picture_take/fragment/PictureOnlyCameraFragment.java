package com.github.yuan.picture_take.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.yuan.picture_take.R;
import com.github.yuan.picture_take.basic.PictureCommonFragment;
import com.github.yuan.picture_take.config.PictureSelectionConfig;
import com.github.yuan.picture_take.entity.LocalMedia;
import com.github.yuan.picture_take.manager.SelectedManager;
import com.github.yuan.picture_take.permissions.PermissionChecker;
import com.github.yuan.picture_take.permissions.PermissionConfig;
import com.github.yuan.picture_take.permissions.PermissionResultCallback;
import com.github.yuan.picture_take.utils.SdkVersionUtils;
import com.github.yuan.picture_take.utils.ToastUtils;


/**
 * @author：luck
 * @date：2021/11/22 2:26 下午
 * @describe：PictureOnlyCameraFragment
 */
public class PictureOnlyCameraFragment extends PictureCommonFragment {
    public static final String TAG = PictureOnlyCameraFragment.class.getSimpleName();

    public static PictureOnlyCameraFragment newInstance() {
        return new PictureOnlyCameraFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getResourceId() {
        return R.layout.ps_empty;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 这里只有非内存回收状态下才走，否则当内存不足Fragment被回收后会重复执行
        if (savedInstanceState == null) {
            if (SdkVersionUtils.isQ()) {
                openSelectedCamera();
            } else {
                String[] writePermissionArray = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                PermissionChecker.getInstance().requestPermissions(this, writePermissionArray, new PermissionResultCallback() {
                    @Override
                    public void onGranted() {
                        openSelectedCamera();
                    }

                    @Override
                    public void onDenied() {
                        handlePermissionDenied(writePermissionArray);
                    }
                });
            }
        }
    }

    @Override
    public void dispatchCameraMediaResult(LocalMedia media) {
        int selectResultCode = confirmSelect(media, false);
        if (selectResultCode == SelectedManager.ADD_SUCCESS) {
            dispatchTransformResult();
        } else {
            onKeyBackFragmentFinish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            onKeyBackFragmentFinish();
        }
    }

    @Override
    public void handlePermissionSettingResult(String[] permissions) {
        onPermissionExplainEvent(false, null);
        boolean isHasPermissions;
        if (PictureSelectionConfig.onPermissionsEventListener != null) {
            isHasPermissions = PictureSelectionConfig.onPermissionsEventListener
                    .hasPermissions(this, permissions);
        } else {
            isHasPermissions = PermissionChecker.isCheckCamera(getContext());
            if (SdkVersionUtils.isQ()) {
            } else {
                isHasPermissions = PermissionChecker.isCheckWriteExternalStorage(getContext());
            }
        }
        if (isHasPermissions) {
            openSelectedCamera();
        } else {
            if (!PermissionChecker.isCheckCamera(getContext())) {
                ToastUtils.showToast(getContext(), getString(R.string.ps_camera));
            } else {
                if (!PermissionChecker.isCheckWriteExternalStorage(getContext())) {
                    ToastUtils.showToast(getContext(), getString(R.string.ps_jurisdiction));
                }
            }
            onKeyBackFragmentFinish();
        }
        PermissionConfig.CURRENT_REQUEST_PERMISSION = new String[]{};
    }
}
