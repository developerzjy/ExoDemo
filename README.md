# ExoDemo
仿抖音短视频播放Demo，实现了预缓存，使用EXO旧版本的CacheUtil实现

//1.预缓存<\br>
PlayerCacheManager.getInstance().preCache(videoUrl);

//2.播放视频的时候第三个参数传true表示使用缓存播放<\br>
SimplePlayerView.setVideoPath(videoUrl, true, true);
