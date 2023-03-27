package com.github.yuan.picture_take.engine;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;


import com.github.yuan.picture_take.config.PictureSelectionConfig;
import com.github.yuan.picture_take.entity.LocalMedia;
import com.github.yuan.picture_take.interfaces.OnPlayerListener;
import com.github.yuan.picture_take.widget.MediaPlayerView;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author：luck
 * @date：2022/7/1 22:09 下午
 * @describe：MediaPlayerEngine
 */
public class MediaPlayerEngine implements VideoPlayerEngine<MediaPlayerView> {
    /**
     * 播放状态监听器集
     */
    private final CopyOnWriteArrayList<OnPlayerListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public View onCreateVideoPlayer(Context context) {
        return new MediaPlayerView(context);
    }

    @Override
    public void onStarPlayer(MediaPlayerView player, LocalMedia media) {
        MediaPlayer mediaPlayer = player.getMediaPlayer();
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        mediaPlayer.setLooping(config.isLoopAutoPlay);
        player.start(media.getAvailablePath());
    }

    @Override
    public void onResume(MediaPlayerView player) {
        MediaPlayer mediaPlayer = player.getMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onPause(MediaPlayerView player) {
        MediaPlayer mediaPlayer = player.getMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public boolean isPlaying(MediaPlayerView player) {
        MediaPlayer mediaPlayer = player.getMediaPlayer();
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    @Override
    public void addPlayListener(OnPlayerListener playerListener) {
        if (!listeners.contains(playerListener)) {
            listeners.add(playerListener);
        }
    }

    @Override
    public void removePlayListener(OnPlayerListener playerListener) {
        if (playerListener != null) {
            listeners.remove(playerListener);
        } else {
            listeners.clear();
        }
    }

    @Override
    public void onPlayerAttachedToWindow(MediaPlayerView player) {
        MediaPlayer mediaPlayer = player.initMediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                for (int i = 0; i < listeners.size(); i++) {
                    OnPlayerListener playerListener = listeners.get(i);
                    playerListener.onPlayerReady();
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                for (int i = 0; i < listeners.size(); i++) {
                    OnPlayerListener playerListener = listeners.get(i);
                    playerListener.onPlayerEnd();
                }
                player.clearCanvas();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                for (int i = 0; i < listeners.size(); i++) {
                    OnPlayerListener playerListener = listeners.get(i);
                    playerListener.onPlayerError();
                }
                return false;
            }
        });
    }

    @Override
    public void onPlayerDetachedFromWindow(MediaPlayerView player) {
        player.release();
    }

    @Override
    public void destroy(MediaPlayerView player) {
        player.release();
    }
}
