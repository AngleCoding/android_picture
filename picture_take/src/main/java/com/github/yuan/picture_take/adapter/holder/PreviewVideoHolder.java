package com.github.yuan.picture_take.adapter.holder;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.github.yuan.picture_take.R;
import com.github.yuan.picture_take.config.PictureConfig;
import com.github.yuan.picture_take.config.PictureSelectionConfig;
import com.github.yuan.picture_take.engine.MediaPlayerEngine;
import com.github.yuan.picture_take.engine.VideoPlayerEngine;
import com.github.yuan.picture_take.entity.LocalMedia;
import com.github.yuan.picture_take.interfaces.OnPlayerListener;
import com.github.yuan.picture_take.photoview.OnViewTapListener;


/**
 * @author：luck
 * @date：2021/12/15 5:12 下午
 * @describe：PreviewVideoHolder
 */
public class PreviewVideoHolder extends BasePreviewHolder {
    public ImageView ivPlayButton;
    public ProgressBar progress;
    public View videoPlayer;
    private boolean isPlayed = false;

    public PreviewVideoHolder(@NonNull View itemView) {
        super(itemView);
        ivPlayButton = itemView.findViewById(R.id.iv_play_video);
        progress = itemView.findViewById(R.id.progress);
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        ivPlayButton.setVisibility(config.isPreviewZoomEffect ? View.GONE : View.VISIBLE);
        if (PictureSelectionConfig.videoPlayerEngine == null) {
            PictureSelectionConfig.videoPlayerEngine = new MediaPlayerEngine();
        }
        videoPlayer = PictureSelectionConfig.videoPlayerEngine.onCreateVideoPlayer(itemView.getContext());
        if (videoPlayer == null) {
            throw new NullPointerException("onCreateVideoPlayer cannot be empty,Please implement " + VideoPlayerEngine.class);
        }
        if (videoPlayer.getLayoutParams() == null) {
            videoPlayer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }
        ViewGroup viewGroup = (ViewGroup) itemView;
        if (viewGroup.indexOfChild(videoPlayer) != -1) {
            viewGroup.removeView(videoPlayer);
        }
        viewGroup.addView(videoPlayer, 0);
        videoPlayer.setVisibility(View.GONE);
    }

    @Override
    protected void findViews(View itemView) {

    }

    @Override
    protected void loadImage(LocalMedia media, int maxWidth, int maxHeight) {
        if (PictureSelectionConfig.imageEngine != null) {
            String availablePath = media.getAvailablePath();
            if (maxWidth == PictureConfig.UNSET && maxHeight == PictureConfig.UNSET) {
                PictureSelectionConfig.imageEngine.loadImage(itemView.getContext(), availablePath, coverImageView);
            } else {
                PictureSelectionConfig.imageEngine.loadImage(itemView.getContext(), coverImageView, availablePath, maxWidth, maxHeight);
            }
        }
    }

    @Override
    protected void onClickBackPressed() {
        coverImageView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onLongPressDownload(LocalMedia media) {
        coverImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener.onLongPressDownload(media);
                }
                return false;
            }
        });
    }

    @Override
    public void bindData(LocalMedia media, int position) {
        super.bindData(media, position);
        setScaleDisplaySize(media);
        ivPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (config.isPauseResumePlay) {
                    dispatchPlay();
                } else {
                    startPlay();
                }
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (config.isPauseResumePlay) {
                    dispatchPlay();
                } else {
                    if (mPreviewEventListener != null) {
                        mPreviewEventListener.onBackPressed();
                    }
                }
            }
        });
    }

    /**
     * 视频播放状态分发
     */
    private void dispatchPlay() {
        if (isPlayed) {
            if (isPlaying()) {
                onPause();
            } else {
                onResume();
            }
        } else {
            startPlay();
        }
    }

    /**
     * 恢复播放
     */
    private void onResume() {
        ivPlayButton.setVisibility(View.GONE);
        if (PictureSelectionConfig.videoPlayerEngine != null) {
            PictureSelectionConfig.videoPlayerEngine.onResume(videoPlayer);
        }
    }

    /**
     * 暂停播放
     */
    public void onPause() {
        ivPlayButton.setVisibility(View.VISIBLE);
        if (PictureSelectionConfig.videoPlayerEngine != null) {
            PictureSelectionConfig.videoPlayerEngine.onPause(videoPlayer);
        }
    }

    /**
     * 是否正在播放中
     */
    @Override
    public boolean isPlaying() {
        return PictureSelectionConfig.videoPlayerEngine != null
                && PictureSelectionConfig.videoPlayerEngine.isPlaying(videoPlayer);
    }

    /**
     * 外部播放状态监听回调
     */
    private final OnPlayerListener mPlayerListener = new OnPlayerListener() {
        @Override
        public void onPlayerError() {
            playerDefaultUI();
        }

        @Override
        public void onPlayerReady() {
            playerIngUI();
        }

        @Override
        public void onPlayerLoading() {
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPlayerEnd() {
            playerDefaultUI();
        }
    };

    /**
     * 开始播放视频
     */
    public void startPlay() {
        if (videoPlayer == null) {
            throw new NullPointerException("VideoPlayer cannot be empty,Please implement " + VideoPlayerEngine.class);
        }
        if (PictureSelectionConfig.videoPlayerEngine != null) {
            progress.setVisibility(View.VISIBLE);
            ivPlayButton.setVisibility(View.GONE);
            mPreviewEventListener.onPreviewVideoTitle(media.getFileName());
            isPlayed = true;
            PictureSelectionConfig.videoPlayerEngine.onStarPlayer(videoPlayer, media);
        }
    }

    @Override
    protected void setScaleDisplaySize(LocalMedia media) {
        super.setScaleDisplaySize(media);
        if (!config.isPreviewZoomEffect && screenWidth < screenHeight) {
            ViewGroup.LayoutParams layoutParams = videoPlayer.getLayoutParams();
            if (layoutParams instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams playerLayoutParams = (FrameLayout.LayoutParams) layoutParams;
                playerLayoutParams.width = screenWidth;
                playerLayoutParams.height = screenAppInHeight;
                playerLayoutParams.gravity = Gravity.CENTER;
            } else if (layoutParams instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams playerLayoutParams = (RelativeLayout.LayoutParams) layoutParams;
                playerLayoutParams.width = screenWidth;
                playerLayoutParams.height = screenAppInHeight;
                playerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            } else if (layoutParams instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams playerLayoutParams = (LinearLayout.LayoutParams) layoutParams;
                playerLayoutParams.width = screenWidth;
                playerLayoutParams.height = screenAppInHeight;
                playerLayoutParams.gravity = Gravity.CENTER;
            } else if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout.LayoutParams playerLayoutParams = (ConstraintLayout.LayoutParams) layoutParams;
                playerLayoutParams.width = screenWidth;
                playerLayoutParams.height = screenAppInHeight;
                playerLayoutParams.topToTop = ConstraintSet.PARENT_ID;
                playerLayoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
            }
        }
    }

    private void playerDefaultUI() {
        isPlayed = false;
        ivPlayButton.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        coverImageView.setVisibility(View.VISIBLE);
        videoPlayer.setVisibility(View.GONE);
        if (mPreviewEventListener != null) {
            mPreviewEventListener.onPreviewVideoTitle(null);
        }
    }

    private void playerIngUI() {
        progress.setVisibility(View.GONE);
        ivPlayButton.setVisibility(View.GONE);
        coverImageView.setVisibility(View.GONE);
        videoPlayer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewAttachedToWindow() {
        if (PictureSelectionConfig.videoPlayerEngine != null) {
            PictureSelectionConfig.videoPlayerEngine.onPlayerAttachedToWindow(videoPlayer);
            PictureSelectionConfig.videoPlayerEngine.addPlayListener(mPlayerListener);
        }
    }

    @Override
    public void onViewDetachedFromWindow() {
        if (PictureSelectionConfig.videoPlayerEngine != null) {
            PictureSelectionConfig.videoPlayerEngine.onPlayerDetachedFromWindow(videoPlayer);
            PictureSelectionConfig.videoPlayerEngine.removePlayListener(mPlayerListener);
        }
        playerDefaultUI();
    }

    /**
     * resume and pause play
     */
    @Override
    public void resumePausePlay() {
        if (isPlaying()) {
            onPause();
        } else {
            onResume();
        }
    }

    @Override
    public void release() {
        if (PictureSelectionConfig.videoPlayerEngine != null) {
            PictureSelectionConfig.videoPlayerEngine.removePlayListener(mPlayerListener);
            PictureSelectionConfig.videoPlayerEngine.destroy(videoPlayer);
        }
    }
}
