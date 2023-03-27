package com.github.yuan.picture_take.fragment;

import android.annotation.SuppressLint;
import android.app.Service;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.github.yuan.picture_take.R;
import com.github.yuan.picture_take.adapter.PictureImageGridAdapter;
import com.github.yuan.picture_take.animators.AlphaInAnimationAdapter;
import com.github.yuan.picture_take.animators.AnimationType;
import com.github.yuan.picture_take.animators.SlideInBottomAnimationAdapter;
import com.github.yuan.picture_take.basic.FragmentInjectManager;
import com.github.yuan.picture_take.basic.IPictureSelectorEvent;
import com.github.yuan.picture_take.basic.PictureCommonFragment;
import com.github.yuan.picture_take.config.InjectResourceSource;
import com.github.yuan.picture_take.config.PermissionEvent;
import com.github.yuan.picture_take.config.PictureConfig;
import com.github.yuan.picture_take.config.PictureMimeType;
import com.github.yuan.picture_take.config.PictureSelectionConfig;
import com.github.yuan.picture_take.config.SelectMimeType;
import com.github.yuan.picture_take.config.SelectModeConfig;
import com.github.yuan.picture_take.decoration.GridSpacingItemDecoration;
import com.github.yuan.picture_take.dialog.AlbumListPopWindow;
import com.github.yuan.picture_take.entity.LocalMedia;
import com.github.yuan.picture_take.entity.LocalMediaFolder;
import com.github.yuan.picture_take.interfaces.OnAlbumItemClickListener;
import com.github.yuan.picture_take.interfaces.OnQueryAlbumListener;
import com.github.yuan.picture_take.interfaces.OnQueryAllAlbumListener;
import com.github.yuan.picture_take.interfaces.OnQueryDataResultListener;
import com.github.yuan.picture_take.interfaces.OnRecyclerViewPreloadMoreListener;
import com.github.yuan.picture_take.interfaces.OnRecyclerViewScrollListener;
import com.github.yuan.picture_take.interfaces.OnRecyclerViewScrollStateListener;
import com.github.yuan.picture_take.interfaces.OnRequestPermissionListener;
import com.github.yuan.picture_take.loader.IBridgeMediaLoader;
import com.github.yuan.picture_take.loader.LocalMediaLoader;
import com.github.yuan.picture_take.loader.LocalMediaPageLoader;
import com.github.yuan.picture_take.magical.BuildRecycleItemViewParams;
import com.github.yuan.picture_take.manager.SelectedManager;
import com.github.yuan.picture_take.permissions.PermissionChecker;
import com.github.yuan.picture_take.permissions.PermissionConfig;
import com.github.yuan.picture_take.permissions.PermissionResultCallback;
import com.github.yuan.picture_take.style.PictureSelectorStyle;
import com.github.yuan.picture_take.style.SelectMainStyle;
import com.github.yuan.picture_take.utils.ActivityCompatHelper;
import com.github.yuan.picture_take.utils.AnimUtils;
import com.github.yuan.picture_take.utils.DateUtils;
import com.github.yuan.picture_take.utils.DensityUtil;
import com.github.yuan.picture_take.utils.DoubleUtils;
import com.github.yuan.picture_take.utils.StyleUtils;
import com.github.yuan.picture_take.utils.ToastUtils;
import com.github.yuan.picture_take.utils.ValueOf;
import com.github.yuan.picture_take.widget.BottomNavBar;
import com.github.yuan.picture_take.widget.CompleteSelectView;
import com.github.yuan.picture_take.widget.RecyclerPreloadView;
import com.github.yuan.picture_take.widget.SlideSelectTouchListener;
import com.github.yuan.picture_take.widget.SlideSelectionHandler;
import com.github.yuan.picture_take.widget.TitleBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author：luck
 * @date：2021/11/17 10:24 上午
 * @describe：PictureSelectorFragment
 */
public class PictureSelectorFragment extends PictureCommonFragment
        implements OnRecyclerViewPreloadMoreListener, IPictureSelectorEvent {
    public static final String TAG = PictureSelectorFragment.class.getSimpleName();
    /**
     * 这个时间对应的是R.anim.ps_anim_modal_in里面的
     */
    private static int SELECT_ANIM_DURATION = 135;

    private RecyclerPreloadView mRecycler;

    private TextView tvDataEmpty;

    private TitleBar titleBar;

    private BottomNavBar bottomNarBar;

    private CompleteSelectView completeSelectView;

    private TextView tvCurrentDataTime;

    private long intervalClickTime = 0;

    private int allFolderSize;

    private int currentPosition = -1;

    /**
     * Use camera to callback
     */
    private boolean isCameraCallback;

    /**
     * memory recycling
     */
    private boolean isMemoryRecycling;

    private static final Object LOCK = new Object();

    private boolean isDisplayCamera;

    private PictureImageGridAdapter mAdapter;

    private AlbumListPopWindow albumListPopWindow;

    private SlideSelectTouchListener mDragSelectTouchListener;

    public static PictureSelectorFragment newInstance() {
        PictureSelectorFragment fragment = new PictureSelectorFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getResourceId() {
        int layoutResourceId = InjectResourceSource.getLayoutResource(getContext(), InjectResourceSource.MAIN_SELECTOR_LAYOUT_RESOURCE);
        if (layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) {
            return layoutResourceId;
        }
        return R.layout.ps_fragment_selector;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSelectedChange(boolean isAddRemove, LocalMedia currentMedia) {
        bottomNarBar.setSelectedChange();
        completeSelectView.setSelectedChange(false);
        // 刷新列表数据
        if (checkNotifyStrategy(isAddRemove)) {
            mAdapter.notifyItemPositionChanged(currentMedia.position);
            mRecycler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            }, SELECT_ANIM_DURATION);
        } else {
            mAdapter.notifyItemPositionChanged(currentMedia.position);
        }
        if (!isAddRemove) {
            sendChangeSubSelectPositionEvent(true);
        }
    }

    @Override
    public void onFixedSelectedChange(LocalMedia oldLocalMedia) {
        mAdapter.notifyItemPositionChanged(oldLocalMedia.position);
    }

    @Override
    public void sendChangeSubSelectPositionEvent(boolean adapterChange) {
        if (PictureSelectionConfig.selectorStyle.getSelectMainStyle().isSelectNumberStyle()) {
            for (int index = 0; index < SelectedManager.getSelectCount(); index++) {
                LocalMedia media = SelectedManager.getSelectedResult().get(index);
                media.setNum(index + 1);
                if (adapterChange) {
                    mAdapter.notifyItemPositionChanged(media.position);
                }
            }
        }
    }

    @Override
    public void onCheckOriginalChange() {
        bottomNarBar.setOriginalCheck();
    }

    /**
     * 刷新列表策略
     *
     * @param isAddRemove
     * @return
     */
    private boolean checkNotifyStrategy(boolean isAddRemove) {
        boolean isNotifyAll = false;
        if (config.isMaxSelectEnabledMask) {
            if (config.isWithVideoImage) {
                if (config.selectionMode == SelectModeConfig.SINGLE) {
                    // ignore
                } else {
                    isNotifyAll = SelectedManager.getSelectCount() == config.maxSelectNum
                            || (!isAddRemove && SelectedManager.getSelectCount() == config.maxSelectNum - 1);
                }
            } else {
                if (SelectedManager.getSelectCount() == 0 || (isAddRemove && SelectedManager.getSelectCount() == 1)) {
                    // 首次添加或单选，选择数量变为0了，都notifyDataSetChanged
                    isNotifyAll = true;
                } else {
                    if (PictureMimeType.isHasVideo(SelectedManager.getTopResultMimeType())) {
                        int maxSelectNum = config.maxVideoSelectNum > 0
                                ? config.maxVideoSelectNum : config.maxSelectNum;
                        isNotifyAll = SelectedManager.getSelectCount() == maxSelectNum
                                || (!isAddRemove && SelectedManager.getSelectCount() == maxSelectNum - 1);
                    } else {
                        isNotifyAll = SelectedManager.getSelectCount() == config.maxSelectNum
                                || (!isAddRemove && SelectedManager.getSelectCount() == config.maxSelectNum - 1);
                    }
                }
            }
        }
        return isNotifyAll;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PictureConfig.EXTRA_ALL_FOLDER_SIZE, allFolderSize);
        outState.putInt(PictureConfig.EXTRA_CURRENT_PAGE, mPage);
        outState.putInt(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, mRecycler.getLastVisiblePosition());
        outState.putBoolean(PictureConfig.EXTRA_DISPLAY_CAMERA, mAdapter.isDisplayCamera());
        SelectedManager.setCurrentLocalMediaFolder(SelectedManager.getCurrentLocalMediaFolder());
        SelectedManager.addAlbumDataSource(albumListPopWindow.getAlbumList());
        SelectedManager.addDataSource(mAdapter.getData());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reStartSavedInstance(savedInstanceState);
        isMemoryRecycling = savedInstanceState != null;
        tvDataEmpty = view.findViewById(R.id.tv_data_empty);
        completeSelectView = view.findViewById(R.id.ps_complete_select);
        titleBar = view.findViewById(R.id.title_bar);
        bottomNarBar = view.findViewById(R.id.bottom_nar_bar);
        tvCurrentDataTime = view.findViewById(R.id.tv_current_data_time);
        onCreateLoader();
        initAlbumListPopWindow();
        initTitleBar();
        initComplete();
        initRecycler(view);
        initBottomNavBar();
        if (isMemoryRecycling) {
            recoverSaveInstanceData();
        } else {
            requestLoadData();
        }
    }


    @Override
    public void onFragmentResume() {
        setRootViewKeyListener(requireView());
    }

    @Override
    public void reStartSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            allFolderSize = savedInstanceState.getInt(PictureConfig.EXTRA_ALL_FOLDER_SIZE);
            mPage = savedInstanceState.getInt(PictureConfig.EXTRA_CURRENT_PAGE, mPage);
            currentPosition = savedInstanceState.getInt(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, currentPosition);
            isDisplayCamera = savedInstanceState.getBoolean(PictureConfig.EXTRA_DISPLAY_CAMERA, config.isDisplayCamera);
        } else {
            isDisplayCamera = config.isDisplayCamera;
        }
    }


    /**
     * 完成按钮
     */
    private void initComplete() {
        if (config.selectionMode == SelectModeConfig.SINGLE && config.isDirectReturnSingle) {
            PictureSelectionConfig.selectorStyle.getTitleBarStyle().setHideCancelButton(false);
            titleBar.getTitleCancelView().setVisibility(View.VISIBLE);
            completeSelectView.setVisibility(View.GONE);
        } else {
            completeSelectView.setCompleteSelectViewStyle();
            completeSelectView.setSelectedChange(false);
            SelectMainStyle selectMainStyle = PictureSelectionConfig.selectorStyle.getSelectMainStyle();
            if (selectMainStyle.isCompleteSelectRelativeTop()) {
                if (completeSelectView.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
                    ((ConstraintLayout.LayoutParams)
                            completeSelectView.getLayoutParams()).topToTop = R.id.title_bar;
                    ((ConstraintLayout.LayoutParams)
                            completeSelectView.getLayoutParams()).bottomToBottom = R.id.title_bar;
                    if (config.isPreviewFullScreenMode) {
                        ((ConstraintLayout.LayoutParams) completeSelectView
                                .getLayoutParams()).topMargin = DensityUtil.getStatusBarHeight(getContext());
                    }
                } else if (completeSelectView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                    if (config.isPreviewFullScreenMode) {
                        ((RelativeLayout.LayoutParams) completeSelectView
                                .getLayoutParams()).topMargin = DensityUtil.getStatusBarHeight(getContext());
                    }
                }
            }
            completeSelectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (config.isEmptyResultReturn && SelectedManager.getSelectCount() == 0) {
                        onExitPictureSelector();
                    } else {
                        dispatchTransformResult();
                    }
                }
            });
        }
    }


    @Override
    public void onCreateLoader() {
        if (PictureSelectionConfig.loaderFactory != null) {
            mLoader = PictureSelectionConfig.loaderFactory.onCreateLoader();
            if (mLoader == null) {
                throw new NullPointerException("No available " + IBridgeMediaLoader.class + " loader found");
            }
        } else {
            mLoader = config.isPageStrategy ? new LocalMediaPageLoader() : new LocalMediaLoader();
        }
        mLoader.initConfig(getContext(), config);
    }

    private void initTitleBar() {
        if (PictureSelectionConfig.selectorStyle.getTitleBarStyle().isHideTitleBar()) {
            titleBar.setVisibility(View.GONE);
        }
        titleBar.setTitleBarStyle();
        titleBar.setOnTitleBarListener(new TitleBar.OnTitleBarListener() {
            @Override
            public void onTitleDoubleClick() {
                if (config.isAutomaticTitleRecyclerTop) {
                    int intervalTime = 500;
                    if (SystemClock.uptimeMillis() - intervalClickTime < intervalTime && mAdapter.getItemCount() > 0) {
                        mRecycler.scrollToPosition(0);
                    } else {
                        intervalClickTime = SystemClock.uptimeMillis();
                    }
                }
            }

            @Override
            public void onBackPressed() {
                if (albumListPopWindow.isShowing()) {
                    albumListPopWindow.dismiss();
                } else {
                    onKeyBackFragmentFinish();
                }
            }

            @Override
            public void onShowAlbumPopWindow(View anchor) {
                albumListPopWindow.showAsDropDown(anchor);
            }
        });
    }

    /**
     * initAlbumListPopWindow
     */
    private void initAlbumListPopWindow() {
        albumListPopWindow = AlbumListPopWindow.buildPopWindow(getContext());
        albumListPopWindow.setOnPopupWindowStatusListener(new AlbumListPopWindow.OnPopupWindowStatusListener() {
            @Override
            public void onShowPopupWindow() {
                if (!config.isOnlySandboxDir) {
                    AnimUtils.rotateArrow(titleBar.getImageArrow(), true);
                }
            }

            @Override
            public void onDismissPopupWindow() {
                if (!config.isOnlySandboxDir) {
                    AnimUtils.rotateArrow(titleBar.getImageArrow(), false);
                }
            }
        });
        addAlbumPopWindowAction();
    }

    private void recoverSaveInstanceData(){
        mAdapter.setDisplayCamera(isDisplayCamera);
        setEnterAnimationDuration(0);
        if (config.isOnlySandboxDir) {
            handleInAppDirAllMedia(SelectedManager.getCurrentLocalMediaFolder());
        } else {
            handleRecoverAlbumData(new ArrayList<>(SelectedManager.getAlbumDataSource()));
        }
    }


    private void handleRecoverAlbumData(List<LocalMediaFolder> albumData) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        if (albumData.size() > 0) {
            LocalMediaFolder firstFolder;
            if (SelectedManager.getCurrentLocalMediaFolder() != null) {
                firstFolder = SelectedManager.getCurrentLocalMediaFolder();
            } else {
                firstFolder = albumData.get(0);
                SelectedManager.setCurrentLocalMediaFolder(firstFolder);
            }
            titleBar.setTitle(firstFolder.getFolderName());
            albumListPopWindow.bindAlbumData(albumData);
            if (config.isPageStrategy) {
                handleFirstPageMedia(new ArrayList<>(SelectedManager.getDataSource()), true);
            } else {
                setAdapterData(firstFolder.getData());
            }
        } else {
            showDataNull();
        }
    }


    private void requestLoadData() {
        mAdapter.setDisplayCamera(isDisplayCamera);
        if (PermissionChecker.isCheckReadStorage(config.chooseMode, getContext())) {
            beginLoadData();
        } else {
            String[] readPermissionArray = PermissionConfig.getReadPermissionArray(config.chooseMode);
            onPermissionExplainEvent(true, readPermissionArray);
            if (PictureSelectionConfig.onPermissionsEventListener != null) {
                onApplyPermissionsEvent(PermissionEvent.EVENT_SOURCE_DATA, readPermissionArray);
            } else {
                PermissionChecker.getInstance().requestPermissions(this, readPermissionArray, new PermissionResultCallback() {
                    @Override
                    public void onGranted() {
                        beginLoadData();
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
        if (event != PermissionEvent.EVENT_SOURCE_DATA) {
            super.onApplyPermissionsEvent(event, permissionArray);
        } else {
            PictureSelectionConfig.onPermissionsEventListener.requestPermission(this,
                    permissionArray, new OnRequestPermissionListener() {
                @Override
                public void onCall(String[] permissionArray, boolean isResult) {
                    if (isResult) {
                        beginLoadData();
                    } else {
                        handlePermissionDenied(permissionArray);
                    }
                }
            });
        }
    }

    /**
     * 开始获取数据
     */
    private void beginLoadData() {
        onPermissionExplainEvent(false, null);
        if (config.isOnlySandboxDir) {
            loadOnlyInAppDirectoryAllMediaData();
        } else {
            loadAllAlbumData();
        }
    }

    @Override
    public void handlePermissionSettingResult(String[] permissions) {
        if (permissions == null){
            return;
        }
        onPermissionExplainEvent(false, null);
        boolean isHasCamera = permissions.length > 0 && TextUtils.equals(permissions[0], PermissionConfig.CAMERA[0]);
        boolean isHasPermissions;
        if (PictureSelectionConfig.onPermissionsEventListener != null) {
            isHasPermissions = PictureSelectionConfig.onPermissionsEventListener.hasPermissions(this, permissions);
        } else {
            isHasPermissions = PermissionChecker.isCheckSelfPermission(getContext(), permissions);
        }
        if (isHasPermissions) {
            if (isHasCamera) {
                openSelectedCamera();
            } else {
                beginLoadData();
            }
        } else {
            if (isHasCamera) {
                ToastUtils.showToast(getContext(), getString(R.string.ps_camera));
            } else {
                ToastUtils.showToast(getContext(), getString(R.string.ps_jurisdiction));
                onKeyBackFragmentFinish();
            }
        }
        PermissionConfig.CURRENT_REQUEST_PERMISSION = new String[]{};
    }

    /**
     * 给AlbumListPopWindow添加事件
     */
    private void addAlbumPopWindowAction() {
        albumListPopWindow.setOnIBridgeAlbumWidget(new OnAlbumItemClickListener() {

            @Override
            public void onItemClick(int position, LocalMediaFolder curFolder) {
                isDisplayCamera = config.isDisplayCamera && curFolder.getBucketId() == PictureConfig.ALL;
                mAdapter.setDisplayCamera(isDisplayCamera);
                titleBar.setTitle(curFolder.getFolderName());
                LocalMediaFolder lastFolder = SelectedManager.getCurrentLocalMediaFolder();
                long lastBucketId = lastFolder.getBucketId();
                if (config.isPageStrategy) {
                    if (curFolder.getBucketId() != lastBucketId) {
                        // 1、记录一下上一次相册数据加载到哪了，到时候切回来的时候要续上
                        lastFolder.setData(mAdapter.getData());
                        lastFolder.setCurrentDataPage(mPage);
                        lastFolder.setHasMore(mRecycler.isEnabledLoadMore());

                        // 2、判断当前相册是否请求过，如果请求过则不从MediaStore去拉取了
                        if (curFolder.getData().size() > 0 && !curFolder.isHasMore()) {
                            setAdapterData(curFolder.getData());
                            mPage = curFolder.getCurrentDataPage();
                            mRecycler.setEnabledLoadMore(curFolder.isHasMore());
                            mRecycler.smoothScrollToPosition(0);
                        } else {
                            // 3、从MediaStore拉取数据
                            mPage = 1;
                            if (PictureSelectionConfig.loaderDataEngine != null) {
                                PictureSelectionConfig.loaderDataEngine.loadFirstPageMediaData(getContext(),
                                        curFolder.getBucketId(), mPage, config.pageSize,
                                        new OnQueryDataResultListener<LocalMedia>() {
                                            public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                                                handleSwitchAlbum(result, isHasMore);
                                            }
                                        });
                            } else {
                                mLoader.loadPageMediaData(curFolder.getBucketId(), mPage, config.pageSize,
                                        new OnQueryDataResultListener<LocalMedia>() {
                                            @Override
                                            public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                                                handleSwitchAlbum(result, isHasMore);
                                            }
                                        });
                            }
                        }
                    }
                } else {
                    // 非分页模式直接导入该相册下的所有资源
                    if (curFolder.getBucketId() != lastBucketId) {
                        setAdapterData(curFolder.getData());
                        mRecycler.smoothScrollToPosition(0);
                    }
                }
                SelectedManager.setCurrentLocalMediaFolder(curFolder);
                albumListPopWindow.dismiss();
                if (mDragSelectTouchListener != null && config.isFastSlidingSelect) {
                    mDragSelectTouchListener.setRecyclerViewHeaderCount(mAdapter.isDisplayCamera() ? 1 : 0);
                }
            }
        });
    }

    private void handleSwitchAlbum(ArrayList<LocalMedia> result, boolean isHasMore) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        mRecycler.setEnabledLoadMore(isHasMore);
        if (result.size() == 0) {
            // 如果从MediaStore拉取都没有数据了，adapter里的可能是缓存所以也清除
            mAdapter.getData().clear();
        }
        setAdapterData(result);
        mRecycler.onScrolled(0, 0);
        mRecycler.smoothScrollToPosition(0);
    }


    private void initBottomNavBar() {
        bottomNarBar.setBottomNavBarStyle();
        bottomNarBar.setOnBottomNavBarListener(new BottomNavBar.OnBottomNavBarListener() {
            @Override
            public void onPreview() {
                onStartPreview(0, true);
            }

            @Override
            public void onCheckOriginalChange() {
                sendSelectedOriginalChangeEvent();
            }
        });
        bottomNarBar.setSelectedChange();
    }


    @Override
    public void loadAllAlbumData() {
        if (PictureSelectionConfig.loaderDataEngine != null) {
            PictureSelectionConfig.loaderDataEngine.loadAllAlbumData(getContext(),
                    new OnQueryAllAlbumListener<LocalMediaFolder>() {
                        @Override
                        public void onComplete(List<LocalMediaFolder> result) {
                            handleAllAlbumData(false, result);
                        }
                    });
        } else {
            boolean isPreload = preloadPageFirstData();
            mLoader.loadAllAlbum(new OnQueryAllAlbumListener<LocalMediaFolder>() {

                @Override
                public void onComplete(List<LocalMediaFolder> result) {
                    handleAllAlbumData(isPreload, result);
                }
            });
        }
    }

    private boolean preloadPageFirstData() {
        boolean isPreload = false;
        if (config.isPageStrategy && config.isPreloadFirst) {
            LocalMediaFolder firstFolder = new LocalMediaFolder();
            firstFolder.setBucketId(PictureConfig.ALL);
            if (TextUtils.isEmpty(config.defaultAlbumName)) {
                titleBar.setTitle(config.chooseMode == SelectMimeType.ofAudio() ? requireContext().getString(R.string.ps_all_audio) : requireContext().getString(R.string.ps_camera_roll));
            } else {
                titleBar.setTitle(config.defaultAlbumName);
            }
            firstFolder.setFolderName(titleBar.getTitleText());
            SelectedManager.setCurrentLocalMediaFolder(firstFolder);
            loadFirstPageMediaData(firstFolder.getBucketId());
            isPreload = true;
        }
        return isPreload;
    }

    private void handleAllAlbumData(boolean isPreload, List<LocalMediaFolder> result) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        if (result.size() > 0) {
            LocalMediaFolder firstFolder;
            if (isPreload) {
                firstFolder = result.get(0);
                SelectedManager.setCurrentLocalMediaFolder(firstFolder);
            } else {
                if (SelectedManager.getCurrentLocalMediaFolder() != null) {
                    firstFolder = SelectedManager.getCurrentLocalMediaFolder();
                } else {
                    firstFolder = result.get(0);
                    SelectedManager.setCurrentLocalMediaFolder(firstFolder);
                }
            }
            titleBar.setTitle(firstFolder.getFolderName());
            albumListPopWindow.bindAlbumData(result);
            if (config.isPageStrategy) {
                if (config.isPreloadFirst) {
                    mRecycler.setEnabledLoadMore(true);
                } else {
                    loadFirstPageMediaData(firstFolder.getBucketId());
                }
            } else {
                setAdapterData(firstFolder.getData());
            }
        } else {
            showDataNull();
        }
    }

    @Override
    public void loadFirstPageMediaData(long firstBucketId) {
        mPage = 1;
        mRecycler.setEnabledLoadMore(true);
        if (PictureSelectionConfig.loaderDataEngine != null) {
            PictureSelectionConfig.loaderDataEngine.loadFirstPageMediaData(getContext(), firstBucketId,
                    mPage, mPage * config.pageSize, new OnQueryDataResultListener<LocalMedia>() {

                        @Override
                        public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                            handleFirstPageMedia(result, isHasMore);
                        }
                    });
        } else {
            mLoader.loadPageMediaData(firstBucketId, mPage, mPage * config.pageSize,
                    new OnQueryDataResultListener<LocalMedia>() {
                        @Override
                        public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                            handleFirstPageMedia(result, isHasMore);
                        }
                    });
        }
    }

    private void handleFirstPageMedia(ArrayList<LocalMedia> result, boolean isHasMore) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        mRecycler.setEnabledLoadMore(isHasMore);
        if (mRecycler.isEnabledLoadMore() && result.size() == 0) {
            // 如果isHasMore为true但result.size() = 0;
            // 那么有可能是开启了某些条件过滤，实际上是还有更多资源的再强制请求
            onRecyclerViewPreloadMore();
        } else {
            setAdapterData(result);
        }
    }

    @Override
    public void loadOnlyInAppDirectoryAllMediaData() {
        if (PictureSelectionConfig.loaderDataEngine != null) {
            PictureSelectionConfig.loaderDataEngine.loadOnlyInAppDirAllMediaData(getContext(),
                    new OnQueryAlbumListener<LocalMediaFolder>() {
                        @Override
                        public void onComplete(LocalMediaFolder folder) {
                            handleInAppDirAllMedia(folder);
                        }
                    });
        } else {
            mLoader.loadOnlyInAppDirAllMedia(new OnQueryAlbumListener<LocalMediaFolder>() {
                @Override
                public void onComplete(LocalMediaFolder folder) {
                    handleInAppDirAllMedia(folder);
                }
            });
        }
    }

    private void handleInAppDirAllMedia(LocalMediaFolder folder) {
        if (!ActivityCompatHelper.isDestroy(getActivity())) {
            String sandboxDir = config.sandboxDir;
            boolean isNonNull = folder != null;
            String folderName = isNonNull ? folder.getFolderName() : new File(sandboxDir).getName();
            titleBar.setTitle(folderName);
            if (isNonNull) {
                SelectedManager.setCurrentLocalMediaFolder(folder);
                setAdapterData(folder.getData());
            } else {
                showDataNull();
            }
        }
    }

    /**
     * 内存不足时，恢复RecyclerView定位位置
     */
    private void recoveryRecyclerPosition() {
        if (currentPosition > 0) {
            mRecycler.post(new Runnable() {
                @Override
                public void run() {
                    mRecycler.scrollToPosition(currentPosition);
                    mRecycler.setLastVisiblePosition(currentPosition);
                }
            });
        }
    }

    private void initRecycler(View view) {
        mRecycler = view.findViewById(R.id.recycler);
        PictureSelectorStyle selectorStyle = PictureSelectionConfig.selectorStyle;
        SelectMainStyle selectMainStyle = selectorStyle.getSelectMainStyle();
        int listBackgroundColor = selectMainStyle.getMainListBackgroundColor();
        if (StyleUtils.checkStyleValidity(listBackgroundColor)) {
            mRecycler.setBackgroundColor(listBackgroundColor);
        } else {
            mRecycler.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ps_color_black));
        }
        int imageSpanCount = config.imageSpanCount <= 0 ? PictureConfig.DEFAULT_SPAN_COUNT : config.imageSpanCount;
        if (mRecycler.getItemDecorationCount() == 0) {
            if (StyleUtils.checkSizeValidity(selectMainStyle.getAdapterItemSpacingSize())) {
                mRecycler.addItemDecoration(new GridSpacingItemDecoration(imageSpanCount,
                        selectMainStyle.getAdapterItemSpacingSize(), selectMainStyle.isAdapterItemIncludeEdge()));
            } else {
                mRecycler.addItemDecoration(new GridSpacingItemDecoration(imageSpanCount,
                        DensityUtil.dip2px(view.getContext(), 1), selectMainStyle.isAdapterItemIncludeEdge()));
            }
        }
        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), imageSpanCount));
        RecyclerView.ItemAnimator itemAnimator = mRecycler.getItemAnimator();
        if (itemAnimator != null) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
            mRecycler.setItemAnimator(null);
        }
        if (config.isPageStrategy) {
            mRecycler.setReachBottomRow(RecyclerPreloadView.BOTTOM_PRELOAD);
            mRecycler.setOnRecyclerViewPreloadListener(this);
        } else {
            mRecycler.setHasFixedSize(true);
        }
        mAdapter = new PictureImageGridAdapter(getContext(), config);
        mAdapter.setDisplayCamera(isDisplayCamera);
        switch (config.animationMode) {
            case AnimationType.ALPHA_IN_ANIMATION:
                mRecycler.setAdapter(new AlphaInAnimationAdapter(mAdapter));
                break;
            case AnimationType.SLIDE_IN_BOTTOM_ANIMATION:
                mRecycler.setAdapter(new SlideInBottomAnimationAdapter(mAdapter));
                break;
            default:
                mRecycler.setAdapter(mAdapter);
                break;
        }

        addRecyclerAction();
    }


    private void addRecyclerAction() {
        mAdapter.setOnItemClickListener(new PictureImageGridAdapter.OnItemClickListener() {

            @Override
            public void openCameraClick() {
                if (DoubleUtils.isFastDoubleClick()) {
                    return;
                }
                openSelectedCamera();
            }

            @Override
            public int onSelected(View selectedView, int position, LocalMedia media) {
                int selectResultCode = confirmSelect(media, selectedView.isSelected());
                if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                    if (PictureSelectionConfig.onSelectAnimListener != null) {
                        long duration = PictureSelectionConfig.onSelectAnimListener.onSelectAnim(selectedView);
                        if (duration > 0) {
                            SELECT_ANIM_DURATION = (int) duration;
                        }
                    } else {
                        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.ps_anim_modal_in);
                        SELECT_ANIM_DURATION = (int) animation.getDuration();
                        selectedView.startAnimation(animation);
                    }
                }
                return selectResultCode;
            }

            @Override
            public void onItemClick(View selectedView, int position, LocalMedia media) {
                if (config.selectionMode == SelectModeConfig.SINGLE && config.isDirectReturnSingle) {
                    SelectedManager.clearSelectResult();
                    int selectResultCode = confirmSelect(media, false);
                    if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                        dispatchTransformResult();
                    }
                } else {
                    if (DoubleUtils.isFastDoubleClick()) {
                        return;
                    }
                    onStartPreview(position, false);
                }
            }

            @Override
            public void onItemLongClick(View itemView, int position) {
                if (mDragSelectTouchListener != null && config.isFastSlidingSelect) {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
                    vibrator.vibrate(50);
                    mDragSelectTouchListener.startSlideSelection(position);
                }
            }
        });

        mRecycler.setOnRecyclerViewScrollStateListener(new OnRecyclerViewScrollStateListener() {
            @Override
            public void onScrollFast() {
                if (PictureSelectionConfig.imageEngine != null) {
                    PictureSelectionConfig.imageEngine.pauseRequests(getContext());
                }
            }

            @Override
            public void onScrollSlow() {
                if (PictureSelectionConfig.imageEngine != null) {
                    PictureSelectionConfig.imageEngine.resumeRequests(getContext());
                }
            }
        });
        mRecycler.setOnRecyclerViewScrollListener(new OnRecyclerViewScrollListener() {
            @Override
            public void onScrolled(int dx, int dy) {
                setCurrentMediaCreateTimeText();
            }

            @Override
            public void onScrollStateChanged(int state) {
                if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
                    showCurrentMediaCreateTimeUI();
                } else if (state == RecyclerView.SCROLL_STATE_IDLE) {
                    hideCurrentMediaCreateTimeUI();
                }
            }
        });

        if (config.isFastSlidingSelect) {
            HashSet<Integer> selectedPosition = new HashSet<>();
            SlideSelectionHandler slideSelectionHandler = new SlideSelectionHandler(new SlideSelectionHandler.ISelectionHandler() {
                @Override
                public HashSet<Integer> getSelection() {
                    for (int i = 0; i < SelectedManager.getSelectCount(); i++) {
                        LocalMedia media = SelectedManager.getSelectedResult().get(i);
                        selectedPosition.add(media.position);
                    }
                    return selectedPosition;
                }

                @Override
                public void changeSelection(int start, int end, boolean isSelected, boolean calledFromOnStart) {
                    ArrayList<LocalMedia> adapterData = mAdapter.getData();
                    if (adapterData.size() == 0 || start > adapterData.size()) {
                        return;
                    }
                    LocalMedia media = adapterData.get(start);
                    int selectResultCode = confirmSelect(media, SelectedManager.getSelectedResult().contains(media));
                    mDragSelectTouchListener.setActive(selectResultCode != SelectedManager.INVALID);
                }
            });
            mDragSelectTouchListener = new SlideSelectTouchListener()
                    .setRecyclerViewHeaderCount(mAdapter.isDisplayCamera() ? 1 : 0)
                    .withSelectListener(slideSelectionHandler);
            mRecycler.addOnItemTouchListener(mDragSelectTouchListener);
        }
    }

    /**
     * 显示当前资源时间轴
     */
    private void setCurrentMediaCreateTimeText() {
        if (config.isDisplayTimeAxis) {
            int position = mRecycler.getFirstVisiblePosition();
            if (position != RecyclerView.NO_POSITION) {
                ArrayList<LocalMedia> data = mAdapter.getData();
                if (data.size() > position && data.get(position).getDateAddedTime() > 0) {
                    tvCurrentDataTime.setText(DateUtils.getDataFormat(getContext(),
                            data.get(position).getDateAddedTime()));
                }
            }
        }
    }

    /**
     * 显示当前资源时间轴
     */
    private void showCurrentMediaCreateTimeUI() {
        if (config.isDisplayTimeAxis && mAdapter.getData().size() > 0) {
            if (tvCurrentDataTime.getAlpha() == 0F) {
                tvCurrentDataTime.animate().setDuration(150).alphaBy(1.0F).start();
            }
        }
    }

    /**
     * 隐藏当前资源时间轴
     */
    private void hideCurrentMediaCreateTimeUI() {
        if (config.isDisplayTimeAxis && mAdapter.getData().size() > 0) {
            tvCurrentDataTime.animate().setDuration(250).alpha(0.0F).start();
        }
    }

    /**
     * 预览图片
     *
     * @param position        预览图片下标
     * @param isBottomPreview true 底部预览模式 false列表预览模式
     */
    private void onStartPreview(int position, boolean isBottomPreview) {
        if (ActivityCompatHelper.checkFragmentNonExits(getActivity(), PictureSelectorPreviewFragment.TAG)) {
            ArrayList<LocalMedia> data;
            int totalNum;
            long currentBucketId = 0;
            if (isBottomPreview) {
                data = new ArrayList<>(SelectedManager.getSelectedResult());
                totalNum = data.size();
            } else {
                data = new ArrayList<>(mAdapter.getData());
                LocalMediaFolder currentLocalMediaFolder = SelectedManager.getCurrentLocalMediaFolder();
                totalNum = currentLocalMediaFolder != null ? currentLocalMediaFolder.getFolderTotalNum() : data.size();
                currentBucketId = currentLocalMediaFolder != null ? currentLocalMediaFolder.getBucketId()
                        : data.size() > 0 ? data.get(0).getBucketId() : PictureConfig.ALL;
            }
            if (!isBottomPreview && config.isPreviewZoomEffect) {
                BuildRecycleItemViewParams.generateViewParams(mRecycler,
                        config.isPreviewFullScreenMode ? 0 : DensityUtil.getStatusBarHeight(getContext()));
            }
            if (PictureSelectionConfig.onPreviewInterceptListener != null) {
                PictureSelectionConfig.onPreviewInterceptListener
                        .onPreview(getContext(), position, totalNum, mPage, currentBucketId, titleBar.getTitleText(),
                                mAdapter.isDisplayCamera(), data, isBottomPreview);
            } else {
                if (ActivityCompatHelper.checkFragmentNonExits(getActivity(), PictureSelectorPreviewFragment.TAG)) {
                    PictureSelectorPreviewFragment previewFragment = PictureSelectorPreviewFragment.newInstance();
                    previewFragment.setInternalPreviewData(isBottomPreview, titleBar.getTitleText(), mAdapter.isDisplayCamera(),
                            position, totalNum, mPage, currentBucketId, data);
                    FragmentInjectManager.injectFragment(getActivity(), PictureSelectorPreviewFragment.TAG, previewFragment);
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapterData(ArrayList<LocalMedia> result) {
        // 这个地方有个时间差，主要是解决进场动画和查询数据同时进行导致动画有点卡顿问题，
        // 主要是针对添加PictureSelectorFragment方式下
        long enterAnimationDuration = getEnterAnimationDuration();
        if (enterAnimationDuration > 0) {
            requireView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setAdapterDataComplete(result);
                }
            }, enterAnimationDuration);
        } else {
            setAdapterDataComplete(result);
        }
    }

    private void setAdapterDataComplete(ArrayList<LocalMedia> result) {
        setEnterAnimationDuration(0);
        sendChangeSubSelectPositionEvent(false);
        mAdapter.setDataAndDataSetChanged(result);
        SelectedManager.clearAlbumDataSource();
        SelectedManager.clearDataSource();
        recoveryRecyclerPosition();
        if (mAdapter.isDataEmpty()) {
            showDataNull();
        } else {
            hideDataNull();
        }
    }

    @Override
    public void onRecyclerViewPreloadMore() {
        if (isMemoryRecycling) {
            // 这里延迟是拍照导致的页面被回收，Fragment的重创会快于相机的onActivityResult的
            requireView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadMoreMediaData();
                }
            }, 350);
        } else {
            loadMoreMediaData();
        }
    }

    /**
     * 加载更多
     */
    @Override
    public void loadMoreMediaData() {
        if (mRecycler.isEnabledLoadMore()) {
            mPage++;
            LocalMediaFolder localMediaFolder = SelectedManager.getCurrentLocalMediaFolder();
            long bucketId = localMediaFolder != null ? localMediaFolder.getBucketId() : 0;
            if (PictureSelectionConfig.loaderDataEngine != null) {
                PictureSelectionConfig.loaderDataEngine.loadMoreMediaData(getContext(), bucketId, mPage,
                        config.pageSize, config.pageSize, new OnQueryDataResultListener<LocalMedia>() {
                            @Override
                            public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                                handleMoreMediaData(result, isHasMore);
                            }
                        });
            } else {
                mLoader.loadPageMediaData(bucketId, mPage, config.pageSize,
                        new OnQueryDataResultListener<LocalMedia>() {
                            @Override
                            public void onComplete(ArrayList<LocalMedia> result, boolean isHasMore) {
                                handleMoreMediaData(result, isHasMore);
                            }
                        });
            }
        }
    }

    private void handleMoreMediaData(List<LocalMedia> result, boolean isHasMore) {
        if (ActivityCompatHelper.isDestroy(getActivity())) {
            return;
        }
        mRecycler.setEnabledLoadMore(isHasMore);
        if (mRecycler.isEnabledLoadMore()) {
            removePageCameraRepeatData(result);
            if (result.size() > 0) {
                int positionStart = mAdapter.getData().size();
                mAdapter.getData().addAll(result);
                mAdapter.notifyItemRangeChanged(positionStart, mAdapter.getItemCount());
                hideDataNull();
            } else {
                // 如果没数据这里在强制调用一下上拉加载更多，防止是因为某些条件过滤导致的假为0的情况
                onRecyclerViewPreloadMore();
            }
            if (result.size() < PictureConfig.MIN_PAGE_SIZE) {
                // 当数据量过少时强制触发一下上拉加载更多，防止没有自动触发加载更多
                mRecycler.onScrolled(mRecycler.getScrollX(), mRecycler.getScrollY());
            }
        }
    }

    private void removePageCameraRepeatData(List<LocalMedia> result) {
        try {
            if (config.isPageStrategy && isCameraCallback) {
                synchronized (LOCK) {
                    Iterator<LocalMedia> iterator = result.iterator();
                    while (iterator.hasNext()) {
                        if (mAdapter.getData().contains(iterator.next())) {
                            iterator.remove();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isCameraCallback = false;
        }
    }


    @Override
    public void dispatchCameraMediaResult(LocalMedia media) {
        int exitsTotalNum = albumListPopWindow.getFirstAlbumImageCount();
        if (!isAddSameImp(exitsTotalNum)) {
            mAdapter.getData().add(0, media);
            isCameraCallback = true;
        }
        if (config.selectionMode == SelectModeConfig.SINGLE && config.isDirectReturnSingle) {
            SelectedManager.clearSelectResult();
            int selectResultCode = confirmSelect(media, false);
            if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                dispatchTransformResult();
            }
        } else {
            confirmSelect(media, false);
        }
        mAdapter.notifyItemInserted(config.isDisplayCamera ? 1 : 0);
        mAdapter.notifyItemRangeChanged(config.isDisplayCamera ? 1 : 0, mAdapter.getData().size());
        if (config.isOnlySandboxDir) {
            LocalMediaFolder currentLocalMediaFolder = SelectedManager.getCurrentLocalMediaFolder();
            if (currentLocalMediaFolder == null) {
                currentLocalMediaFolder = new LocalMediaFolder();
            }
            currentLocalMediaFolder.setBucketId(ValueOf.toLong(media.getParentFolderName().hashCode()));
            currentLocalMediaFolder.setFolderName(media.getParentFolderName());
            currentLocalMediaFolder.setFirstMimeType(media.getMimeType());
            currentLocalMediaFolder.setFirstImagePath(media.getPath());
            currentLocalMediaFolder.setFolderTotalNum(mAdapter.getData().size());
            currentLocalMediaFolder.setCurrentDataPage(mPage);
            currentLocalMediaFolder.setHasMore(false);
            currentLocalMediaFolder.setData(mAdapter.getData());
            mRecycler.setEnabledLoadMore(false);
            SelectedManager.setCurrentLocalMediaFolder(currentLocalMediaFolder);
        } else {
            mergeFolder(media);
        }
        allFolderSize = 0;
        if (mAdapter.getData().size() > 0 || config.isDirectReturnSingle) {
            hideDataNull();
        } else {
            showDataNull();
        }
    }

    /**
     * 拍照出来的合并到相应的专辑目录中去
     *
     * @param media
     */
    private void mergeFolder(LocalMedia media) {
        LocalMediaFolder allFolder;
        List<LocalMediaFolder> albumList = albumListPopWindow.getAlbumList();
        if (albumListPopWindow.getFolderCount() == 0) {
            // 1、没有相册时需要手动创建相机胶卷
            allFolder = new LocalMediaFolder();
            String folderName;
            if (TextUtils.isEmpty(config.defaultAlbumName)) {
                folderName = config.chooseMode == SelectMimeType.ofAudio() ? getString(R.string.ps_all_audio) : getString(R.string.ps_camera_roll);
            } else {
                folderName = config.defaultAlbumName;
            }
            allFolder.setFolderName(folderName);
            allFolder.setFirstImagePath("");
            allFolder.setBucketId(PictureConfig.ALL);
            albumList.add(0, allFolder);
        } else {
            // 2、有相册就找到对应的相册把数据加进去
            allFolder = albumListPopWindow.getFolder(0);
        }
        allFolder.setFirstImagePath(media.getPath());
        allFolder.setFirstMimeType(media.getMimeType());
        allFolder.setData(mAdapter.getData());
        allFolder.setBucketId(PictureConfig.ALL);
        allFolder.setFolderTotalNum(isAddSameImp(allFolder.getFolderTotalNum()) ? allFolder.getFolderTotalNum() : allFolder.getFolderTotalNum() + 1);
        LocalMediaFolder currentLocalMediaFolder = SelectedManager.getCurrentLocalMediaFolder();
        if (currentLocalMediaFolder == null || currentLocalMediaFolder.getFolderTotalNum() == 0) {
            SelectedManager.setCurrentLocalMediaFolder(allFolder);
        }
        // 先查找Camera目录，没有找到则创建一个Camera目录
        LocalMediaFolder cameraFolder = null;
        for (int i = 0; i < albumList.size(); i++) {
            LocalMediaFolder exitsFolder = albumList.get(i);
            if (TextUtils.equals(exitsFolder.getFolderName(), media.getParentFolderName())) {
                cameraFolder = exitsFolder;
                break;
            }
        }
        if (cameraFolder == null) {
            // 还没有这个目录，创建一个
            cameraFolder = new LocalMediaFolder();
            albumList.add(cameraFolder);
        }
        cameraFolder.setFolderName(media.getParentFolderName());
        if (cameraFolder.getBucketId() == -1 || cameraFolder.getBucketId() == 0) {
            cameraFolder.setBucketId(media.getBucketId());
        }
        // 分页模式下，切换到Camera目录下时，会直接从MediaStore拉取
        if (config.isPageStrategy) {
            cameraFolder.setHasMore(true);
        } else {
            // 非分页模式数据都是存在目录的data下，所以直接添加进去就行
            if (!isAddSameImp(allFolder.getFolderTotalNum())
                    || !TextUtils.isEmpty(config.outPutCameraDir)
                    || !TextUtils.isEmpty(config.outPutAudioDir)) {
                cameraFolder.getData().add(0, media);
            }
        }
        cameraFolder.setFolderTotalNum(isAddSameImp(allFolder.getFolderTotalNum())
                ? cameraFolder.getFolderTotalNum() : cameraFolder.getFolderTotalNum() + 1);
        cameraFolder.setFirstImagePath(config.cameraPath);
        cameraFolder.setFirstMimeType(media.getMimeType());
        albumListPopWindow.bindAlbumData(albumList);
    }

    /**
     * 数量是否一致
     */
    private boolean isAddSameImp(int totalNum) {
        if (totalNum == 0) {
            return false;
        }
        return allFolderSize > 0 && allFolderSize < totalNum;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDragSelectTouchListener != null) {
            mDragSelectTouchListener.stopAutoScroll();
        }
    }

    /**
     * 显示数据为空提示
     */
    private void showDataNull() {
        if (SelectedManager.getCurrentLocalMediaFolder() == null
                || SelectedManager.getCurrentLocalMediaFolder().getBucketId() == PictureConfig.ALL) {
            if (tvDataEmpty.getVisibility() == View.GONE) {
                tvDataEmpty.setVisibility(View.VISIBLE);
            }
            tvDataEmpty.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ps_ic_no_data, 0, 0);
            String tips = config.chooseMode == SelectMimeType.ofAudio() ? getString(R.string.ps_audio_empty) : getString(R.string.ps_empty);
            tvDataEmpty.setText(tips);
        }
    }

    /**
     * 隐藏数据为空提示
     */
    private void hideDataNull() {
        if (tvDataEmpty.getVisibility() == View.VISIBLE) {
            tvDataEmpty.setVisibility(View.GONE);
        }
    }
}
