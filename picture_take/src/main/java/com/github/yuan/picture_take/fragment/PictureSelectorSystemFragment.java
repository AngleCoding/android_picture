package com.github.yuan.picture_take.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.github.yuan.picture_take.R;
import com.github.yuan.picture_take.basic.PictureCommonFragment;
import com.github.yuan.picture_take.config.PermissionEvent;
import com.github.yuan.picture_take.config.PictureSelectionConfig;
import com.github.yuan.picture_take.config.SelectMimeType;
import com.github.yuan.picture_take.config.SelectModeConfig;
import com.github.yuan.picture_take.entity.LocalMedia;
import com.github.yuan.picture_take.interfaces.OnRequestPermissionListener;
import com.github.yuan.picture_take.manager.SelectedManager;
import com.github.yuan.picture_take.permissions.PermissionChecker;
import com.github.yuan.picture_take.permissions.PermissionConfig;
import com.github.yuan.picture_take.permissions.PermissionResultCallback;
import com.github.yuan.picture_take.utils.SdkVersionUtils;
import com.github.yuan.picture_take.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2022/1/16 10:22 下午
 * @describe：PictureSelectorSystemFragment
 */
public class PictureSelectorSystemFragment extends PictureCommonFragment {
    public static final String TAG = PictureSelectorSystemFragment.class.getSimpleName();

    public static PictureSelectorSystemFragment newInstance() {
        return new PictureSelectorSystemFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getResourceId() {
        return R.layout.ps_empty;
    }


    private ActivityResultLauncher<String> mDocMultipleLauncher;

    private ActivityResultLauncher<String> mDocSingleLauncher;

    private ActivityResultLauncher<String> mContentsLauncher;

    private ActivityResultLauncher<String> mContentLauncher;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createSystemContracts();
        if (PermissionChecker.isCheckReadStorage(config.chooseMode,getContext())) {
            openSystemAlbum();
        } else {
            String[] readPermissionArray = PermissionConfig.getReadPermissionArray(config.chooseMode);
            onPermissionExplainEvent(true, readPermissionArray);
            if (PictureSelectionConfig.onPermissionsEventListener != null) {
                onApplyPermissionsEvent(PermissionEvent.EVENT_SYSTEM_SOURCE_DATA, readPermissionArray);
            } else {
                PermissionChecker.getInstance().requestPermissions(this, readPermissionArray, new PermissionResultCallback() {
                    @Override
                    public void onGranted() {
                        openSystemAlbum();
                    }

                    @Override
                    public void onDenied() {
                        handlePermissionDenied(readPermissionArray);
                    }
                });
            }
        }
    }

    @Override
    public void onApplyPermissionsEvent(int event, String[] permissionArray) {
        if (event == PermissionEvent.EVENT_SYSTEM_SOURCE_DATA) {
            PictureSelectionConfig.onPermissionsEventListener.requestPermission(this,
                    PermissionConfig.getReadPermissionArray(config.chooseMode), new OnRequestPermissionListener() {
                        @Override
                        public void onCall(String[] permissionArray, boolean isResult) {
                            if (isResult) {
                                openSystemAlbum();
                            } else {
                                handlePermissionDenied(permissionArray);
                            }
                        }
                    });
        }
    }

    /**
     * 打开系统相册
     */
    private void openSystemAlbum() {
        onPermissionExplainEvent(false, null);
        if (config.selectionMode == SelectModeConfig.SINGLE) {
            if (config.chooseMode == SelectMimeType.ofAll()) {
                mDocSingleLauncher.launch(SelectMimeType.SYSTEM_ALL);
            } else {
                mContentLauncher.launch(getInput());
            }
        } else {
            if (config.chooseMode == SelectMimeType.ofAll()) {
                mDocMultipleLauncher.launch(SelectMimeType.SYSTEM_ALL);
            } else {
                mContentsLauncher.launch(getInput());
            }
        }
    }

    /**
     * createSystemContracts
     */
    private void createSystemContracts() {
        if (config.selectionMode == SelectModeConfig.SINGLE) {
            if (config.chooseMode == SelectMimeType.ofAll()) {
                createSingleDocuments();
            } else {
                createContent();
            }
        } else {
            if (config.chooseMode == SelectMimeType.ofAll()) {
                createMultipleDocuments();
            } else {
                createMultipleContents();
            }
        }
    }

    /**
     * 同时获取图片或视频(多选)
     * <p>部分机型可能不支持多选操作</p>
     */
    private void createMultipleDocuments() {
        mDocMultipleLauncher = registerForActivityResult(new ActivityResultContract<String, List<Uri>>() {

            @Override
            public List<Uri> parseResult(int resultCode, @Nullable Intent intent) {
                List<Uri> result = new ArrayList<>();
                if (intent == null) {
                    return result;
                }
                if (intent.getClipData() != null) {
                    ClipData clipData = intent.getClipData();
                    int itemCount = clipData.getItemCount();
                    for (int i = 0; i < itemCount; i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        result.add(uri);
                    }
                } else if (intent.getData() != null) {
                    result.add(intent.getData());
                }
                return result;
            }

            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, String mimeTypes) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType(mimeTypes);
                return intent;
            }
        }, new ActivityResultCallback<List<Uri>>() {
            @Override
            public void onActivityResult(List<Uri> result) {
                if (result == null || result.size() == 0) {
                    onKeyBackFragmentFinish();
                } else {
                    for (int i = 0; i < result.size(); i++) {
                        LocalMedia media = buildLocalMedia(result.get(i).toString());
                        media.setPath(SdkVersionUtils.isQ() ? media.getPath() : media.getRealPath());
                        SelectedManager.addSelectResult(media);
                    }
                    dispatchTransformResult();
                }
            }
        });
    }


    /**
     * 同时获取图片或视频(单选)
     */
    private void createSingleDocuments() {
        mDocSingleLauncher = registerForActivityResult(new ActivityResultContract<String, Uri>() {

            @Override
            public Uri parseResult(int resultCode, @Nullable Intent intent) {
                if (intent == null) {
                    return null;
                }
                return intent.getData();
            }

            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, String mimeTypes) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(mimeTypes);
                return intent;
            }
        }, new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result == null) {
                    onKeyBackFragmentFinish();
                } else {
                    LocalMedia media = buildLocalMedia(result.toString());
                    media.setPath(SdkVersionUtils.isQ() ? media.getPath() : media.getRealPath());
                    int selectResultCode = confirmSelect(media, false);
                    if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                        dispatchTransformResult();
                    } else {
                        onKeyBackFragmentFinish();
                    }
                }
            }
        });
    }


    /**
     * 获取图片或视频
     *  <p>部分机型可能不支持多选操作</p>
     */
    private void createMultipleContents() {
        mContentsLauncher = registerForActivityResult(new ActivityResultContract<String, List<Uri>>() {

            @Override
            public List<Uri> parseResult(int resultCode, @Nullable Intent intent) {
                List<Uri> result = new ArrayList<>();
                if (intent == null) {
                    return result;
                }
                if (intent.getClipData() != null) {
                    ClipData clipData = intent.getClipData();
                    int itemCount = clipData.getItemCount();
                    for (int i = 0; i < itemCount; i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        Uri uri = item.getUri();
                        result.add(uri);
                    }
                } else if (intent.getData() != null) {
                    result.add(intent.getData());
                }
                return result;
            }

            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, String mimeType) {
                Intent intent;
                if (TextUtils.equals(SelectMimeType.SYSTEM_VIDEO, mimeType)) {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                } else if (TextUtils.equals(SelectMimeType.SYSTEM_AUDIO, mimeType)) {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                } else {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                return intent;
            }
        }, new ActivityResultCallback<List<Uri>>() {
            @Override
            public void onActivityResult(List<Uri> result) {
                if (result == null || result.size() == 0) {
                    onKeyBackFragmentFinish();
                } else {
                    for (int i = 0; i < result.size(); i++) {
                        LocalMedia media = buildLocalMedia(result.get(i).toString());
                        media.setPath(SdkVersionUtils.isQ() ? media.getPath() : media.getRealPath());
                        SelectedManager.addSelectResult(media);
                    }
                    dispatchTransformResult();
                }
            }
        });
    }

    /**
     * 单选图片或视频
     */
    private void createContent() {
        mContentLauncher = registerForActivityResult(new ActivityResultContract<String, Uri>() {

            @Override
            public Uri parseResult(int resultCode, @Nullable Intent intent) {
                if (intent == null) {
                    return null;
                }
                return intent.getData();
            }

            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, String mimeType) {
                Intent intent;
                if (TextUtils.equals(SelectMimeType.SYSTEM_VIDEO, mimeType)) {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                } else if (TextUtils.equals(SelectMimeType.SYSTEM_AUDIO, mimeType)) {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                } else {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                return intent;
            }
        }, new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result == null) {
                    onKeyBackFragmentFinish();
                } else {
                    LocalMedia media = buildLocalMedia(result.toString());
                    media.setPath(SdkVersionUtils.isQ() ? media.getPath() : media.getRealPath());
                    int selectResultCode = confirmSelect(media, false);
                    if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                        dispatchTransformResult();
                    } else {
                        onKeyBackFragmentFinish();
                    }
                }
            }
        });
    }

    /**
     * 获取选资源取类型
     *
     * @return
     */
    private String getInput() {
        if (config.chooseMode == SelectMimeType.ofVideo()) {
            return SelectMimeType.SYSTEM_VIDEO;
        } else if (config.chooseMode == SelectMimeType.ofAudio()) {
            return SelectMimeType.SYSTEM_AUDIO;
        } else {
            return SelectMimeType.SYSTEM_IMAGE;
        }
    }

    @Override
    public void handlePermissionSettingResult(String[] permissions) {
        onPermissionExplainEvent(false, null);
        boolean isCheckReadStorage;
        if (PictureSelectionConfig.onPermissionsEventListener != null) {
            isCheckReadStorage = PictureSelectionConfig.onPermissionsEventListener
                    .hasPermissions(this, permissions);
        } else {
            isCheckReadStorage = PermissionChecker.isCheckReadStorage(config.chooseMode, getContext());
        }
        if (isCheckReadStorage) {
            openSystemAlbum();
        } else {
            ToastUtils.showToast(getContext(), getString(R.string.ps_jurisdiction));
            onKeyBackFragmentFinish();
        }
        PermissionConfig.CURRENT_REQUEST_PERMISSION = new String[]{};
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            onKeyBackFragmentFinish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDocMultipleLauncher != null) {
            mDocMultipleLauncher.unregister();
        }
        if (mDocSingleLauncher != null) {
            mDocSingleLauncher.unregister();
        }
        if (mContentsLauncher != null) {
            mContentsLauncher.unregister();
        }
        if (mContentLauncher != null) {
            mContentLauncher.unregister();
        }
    }
}
