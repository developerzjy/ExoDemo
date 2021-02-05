package com.example.exodemo.player;

import android.net.Uri;

import com.example.exodemo.App;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: zhangjianyang
 * Date: 2021/1/6
 */
public class PlayerCacheManager {

    //缓存路径
    private static final String CACHE_DIR = App.sContext.getExternalCacheDir() + "/ExoVideoCache";
    //最大缓存大小
    private static final long CACHE_MAX_SIZE = 512 * 1024 * 1024;
    //单个视频缓存大小
    private static final long CACHE_SIZE_EACH_VIDEO = 512 * 1024;

    private static final PlayerCacheManager sInstance = new PlayerCacheManager();

    private DataSource.Factory dataSourceFactory;
    private SimpleCache simpleCache;
    private DataSource.Factory cachedDataSourceFactory;

    private ExecutorService executor;

    public static PlayerCacheManager getInstance() {
        return sInstance;
    }

    private PlayerCacheManager() {
        dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer-codelab");
        File cacheFile = new File(CACHE_DIR);
        simpleCache = new SimpleCache(cacheFile, new LeastRecentlyUsedCacheEvictor(CACHE_MAX_SIZE));
        cachedDataSourceFactory = new CacheDataSourceFactory(simpleCache, dataSourceFactory);

        executor = Executors.newCachedThreadPool();
    }

    public void preCache(String url) {
        executor.execute(() -> {
            DataSpec dataSpec = new DataSpec(Uri.parse(url), 0, CACHE_SIZE_EACH_VIDEO, null);
            try {
                CacheUtil.cache(dataSpec, simpleCache, dataSourceFactory.createDataSource(), null, null);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public MediaSource getCacheMediaSource(String url) {
        return createCachedMediaSource(url);
    }

    private MediaSource createCachedMediaSource(String url) {
        Uri uri = Uri.parse(url);
        int type = inferContentType(uri);
        MediaSource videoSource = null;
        switch (type) {
            case C.TYPE_DASH:
                videoSource = new DashMediaSource.Factory(cachedDataSourceFactory).createMediaSource(uri);
                break;
            case C.TYPE_SS:
                videoSource = new SsMediaSource.Factory(cachedDataSourceFactory).createMediaSource(uri);
                break;
            case C.TYPE_HLS:
                videoSource = new HlsMediaSource.Factory(cachedDataSourceFactory).createMediaSource(uri);
                break;
            case C.TYPE_OTHER:
                videoSource = new ExtractorMediaSource.Factory(cachedDataSourceFactory).createMediaSource(uri);
                break;
        }
        return videoSource;
    }

    private int inferContentType(Uri uri) {
        String fileName = Util.toLowerInvariant(uri.toString());
        if (fileName.endsWith(".m3u8")) {
            return C.TYPE_HLS;
        }
        return Util.inferContentType(uri);
    }
}
