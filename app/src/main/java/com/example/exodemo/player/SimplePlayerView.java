package com.example.exodemo.player;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.exodemo.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import static com.google.android.exoplayer2.Player.REPEAT_MODE_OFF;

/**
 * Author: zhangjianyang
 * Date: 2020/11/21
 */
public class SimplePlayerView extends AbsPlayerView {

    private SimpleExoPlayer player;
    private View loadingView;

    private String videoPath;

    private ConcatenatingMediaSource mediaSource;

    public SimplePlayerView(@NonNull Context context) {
        this(context, null);
    }

    public SimplePlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimplePlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_simple_player, this);
        loadingView = findViewById(R.id.loading_view);
        loadingView.setVisibility(VISIBLE);

        player = ExoPlayerFactory.newSimpleInstance(context,
                new DefaultRenderersFactory(context),
                new DefaultTrackSelector(),
                new DefaultLoadControl());
        player.setRepeatMode(REPEAT_MODE_OFF);

        PlayerView playerView = findViewById(R.id.player_view);
        playerView.setPlayer(player);
        playerView.setUseController(false);
        playerView.setControllerVisibilityListener(null);
        playerView.setErrorMessageProvider(null);

        player.addListener(new Player.EventListener() {
            @Override
            public void onLoadingChanged(boolean isLoading) {
                if (isLoading && player.getPlaybackState() != Player.STATE_READY) {
                    showLoading();
                } else {
                    hideLoading();
                }
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_ENDED:
                        //end
                        if (mPlayerStateListener != null) {
                            mPlayerStateListener.onEnd();
                        }
                        hideLoading();
                        break;
                    case Player.STATE_READY:
                        hideLoading();
                        if (playWhenReady) { //start
                            if (mPlayerStateListener != null) {
                                mPlayerStateListener.onStart();
                            }
                        } else { //stop
                            if (mPlayerStateListener != null) {
                                mPlayerStateListener.onPause();
                            }
                        }
                        break;
                    case Player.STATE_BUFFERING:
                        showLoading();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (mPlayerStateListener != null) {
                    mPlayerStateListener.onError();
                }
            }
        });
    }

    private int inferContentType(Uri uri) {
        String fileName = Util.toLowerInvariant(uri.toString());
        if (fileName.endsWith(".m3u8")) {
            return C.TYPE_HLS;
        }
        return Util.inferContentType(uri);
    }

    @Override
    public void showLoading() {
        loadingView.setVisibility(VISIBLE);
    }

    @Override
    public void hideLoading() {
        loadingView.setVisibility(GONE);
    }

    @Override
    public String getVideoPath() {
        return videoPath;
    }

    @Override
    public void setVideoPath(String path) {
        setVideoPath(path, true);
    }

    @Override
    public void setVideoPath(String path, boolean isPlay) {
        Log.d("SimplePlayerView", "setVideoPath: path = " + path);
        videoPath = path;
        mediaSource = new ConcatenatingMediaSource();
        addVideoPath(path);

        player.prepare(mediaSource);
        player.setPlayWhenReady(isPlay);
    }

    private MediaSource createMediaSource(String path) {
        DataSource.Factory dataSourceFactory;
        if (new File(path).exists()) {
            dataSourceFactory = new FileDataSourceFactory();
        } else {
            dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer-codelab");
        }

        Uri uri = Uri.parse(path);
        int type = inferContentType(uri);
        MediaSource videoSource = null;
        switch (type) {
            case C.TYPE_DASH:
                videoSource = new DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                break;
            case C.TYPE_SS:
                videoSource = new SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                break;
            case C.TYPE_HLS:
                videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                break;
            case C.TYPE_OTHER:
                videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                break;
        }
        return videoSource;
    }

    @Override
    public void start() {
        player.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        player.setPlayWhenReady(false);
    }

    @Override
    public int getDuration() {
        return (int) player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return (int) player.getCurrentPosition();
    }

    @Override
    public int getBufferedPosition() {
        return (int) player.getBufferedPosition();
    }

    @Override
    public void seekTo(int pos) {
        player.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return player.getPlayWhenReady();
    }


    public void clearVideoPath() {
        mediaSource = null;
        player.stop();
    }

    public void addVideoPath(String path) {
        boolean needPrepare = false;
        if (mediaSource == null) {
            mediaSource = new ConcatenatingMediaSource();
            needPrepare = true;
        }

        MediaSource ms = createMediaSource(path);
        mediaSource.addMediaSource(ms);

        if (needPrepare) {
            player.prepare(mediaSource);
            player.setPlayWhenReady(false);
        }
    }

    public void playPrevious() {
        player.previous();
        if (!isPlaying()) {
            start();
        }
    }

    public void playNext() {
        player.next();
        if (!isPlaying()) {
            start();
        }
    }

    @Override
    public void release() {
        if (player != null) {
            player.release();
            player = null;
            removeAllViews();
        }
    }
}
