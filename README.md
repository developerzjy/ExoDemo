# ExoDemo
Exo实现短视频播放的Demo，使用旧版本Exo的CacheUtil实现了预缓存（新版本的Exo已经弃用了CacheUtil）

1.在合适的时机调用下面的代码预缓存视频
`PlayerCacheManager.getInstance().preCache(videoUrl);`

2.播放视频的时候第三个参数传true表示使用缓存播放
`SimplePlayerView.setVideoPath(videoUrl, true, true);`
