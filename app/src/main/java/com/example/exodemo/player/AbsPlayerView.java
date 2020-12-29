package com.example.exodemo.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Author: zhangjianyang
 * Date: 2020/11/21
 */
public abstract class AbsPlayerView extends FrameLayout {

    protected OnProgressListener mProgressListener;
    protected OnPlayerStateListener mPlayerStateListener;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mProgressListener != null) {
                mProgressListener.onProgress(getDuration(), getCurrentPosition(), getBufferedPosition());
            }
            postDelayed(runnable, 1000);
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
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startRunnable();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRunnable();
    }

    private void startRunnable() {
        removeCallbacks(runnable);
        post(runnable);
    }

    private void stopRunnable() {
        removeCallbacks(runnable);
    }

    public abstract void showLoading();

    public abstract void hideLoading();

    public abstract String getVideoPath();

    public abstract void setVideoPath(String path);

    public abstract void setVideoPath(String path, boolean isPlay);

    public abstract void start();

    public abstract void pause();

    public abstract int getDuration();

    public abstract int getCurrentPosition();

    public abstract int getBufferedPosition();

    public abstract void seekTo(int pos);

    public abstract boolean isPlaying();

    public abstract void release();

    public void setOnProgressListener(OnProgressListener listener) {
        mProgressListener = listener;
    }

    public void setOnPlayerStateListener(OnPlayerStateListener listener) {
        mPlayerStateListener = listener;
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
}
