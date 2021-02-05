package com.example.exodemo.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: zhangjianyang
 * Date: 2020/11/21
 */
public abstract class AbsPlayerView extends FrameLayout {

    final protected OnProgressListener mInnerProgressListener;
    final protected OnPlayerStateListener mInnerPlayerStateListener;

    private List<OnProgressListener> progressListeners = new ArrayList<>();
    private List<OnPlayerStateListener> playerStateListeners = new ArrayList<>();
    protected List<OnVideoListener> videoListeners = new ArrayList<>();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mInnerProgressListener != null) {
                mInnerProgressListener.onProgress(getDuration(), getCurrentPosition(), getBufferedPosition());
            }
            postDelayed(runnable, 500);
        }
    };

    public AbsPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public AbsPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mInnerProgressListener = (duration, currentPosition, bufferedPosition) -> {
            for (OnProgressListener listener : progressListeners) {
                listener.onProgress(duration, currentPosition, bufferedPosition);
            }
        };

        mInnerPlayerStateListener = new OnPlayerStateListener() {
            @Override
            public void onStart() {
                for (OnPlayerStateListener listener : playerStateListeners) {
                    listener.onStart();
                }
            }

            @Override
            public void onPause() {
                for (OnPlayerStateListener listener : playerStateListeners) {
                    listener.onPause();
                }
            }

            @Override
            public void onEnd() {
                for (OnPlayerStateListener listener : playerStateListeners) {
                    listener.onEnd();
                }
            }

            @Override
            public void onError() {
                for (OnPlayerStateListener listener : playerStateListeners) {
                    listener.onError();
                }
            }
        };
    }

    protected void startProgressRunnable() {
        removeCallbacks(runnable);
        post(runnable);
    }

    protected void stopProgressRunnable() {
        removeCallbacks(runnable);
    }

    public abstract void showLoading();

    public abstract void hideLoading();

    public abstract String getVideoPath();

    public abstract void setVideoPath(String path);

    public abstract void setVideoPath(String path, boolean isPlayWhenReady);

    /**
     * 设置视频路径
     * @param path 可为本地路径或者在线url
     * @param isPlayWhenReady 视频准备完成后是否立即播放（如果为false，需要主动调用{@link #start()}进行播放）
     * @param useCache 是否使用缓存
     */
    public abstract void setVideoPath(String path, boolean isPlayWhenReady, boolean useCache);

    public abstract boolean isPlayWhenReady();

    public abstract void start();

    public abstract void pause();

    public abstract int getDuration();

    public abstract int getCurrentPosition();

    public abstract int getBufferedPosition();

    public abstract void seekTo(int pos);

    public abstract boolean isPlaying();

    public abstract void release();

    public void addProgressListener(OnProgressListener listener) {
        progressListeners.add(listener);
    }

    public void removeProgressListener(OnProgressListener listener) {
        progressListeners.remove(listener);
    }

    public void addPlayerStateListener(OnPlayerStateListener listener) {
        playerStateListeners.add(listener);
    }

    public void removePlayerStateListener(OnPlayerStateListener listener) {
        playerStateListeners.remove(listener);
    }

    public void addVideoListener(OnVideoListener listener) {
        videoListeners.add(listener);
    }

    public void removeVideoListener(OnVideoListener listener) {
        videoListeners.remove(listener);
    }

    public interface OnProgressListener {
        void onProgress(int duration, int currentPosition, int bufferedPosition);
    }

    public interface OnPlayerStateListener {
        default void onStart() {
        }

        default void onPause() {
        }

        default void onEnd() {
        }

        default void onError() {
        }
    }

    public interface OnVideoListener {
        default void onRenderedFirstFrame() {
        }
    }
}
