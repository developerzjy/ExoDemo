package com.example.exodemo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exodemo.bean.VideoBean;
import com.example.exodemo.widget.VideoListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        int visibility = window.getDecorView().getSystemUiVisibility();
        visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        visibility |=View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        window.getDecorView().setSystemUiVisibility(visibility);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initVideoListView();
    }

    private void initVideoListView() {
        List<VideoBean> data = new ArrayList<>();

        data.add(new VideoBean("TestVideo-0", "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=10400358,3341701483&fm=26&gp=0.jpg", "https://storage.googleapis.com/exoplayer-test-media-1/mp4/frame-counter-one-hour.mp4"));
        data.add(new VideoBean("TestVideo-1", "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1974728075,625919050&fm=26&gp=0.jpg", "https://storage.googleapis.com/exoplayer-test-media-1/gen-3/screens/dash-vod-single-segment/video-137.mp4"));
        data.add(new VideoBean("TestVideo-2", "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3545561584,783374626&fm=26&gp=0.jpg", "https://storage.googleapis.com/exoplayer-test-media-1/mp4/dizzy-with-tx3g.mp4"));
        data.add(new VideoBean("TestVideo-3", "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=10400358,3341701483&fm=26&gp=0.jpg", "https://storage.googleapis.com/exoplayer-test-media-1/mp4/frame-counter-one-hour.mp4"));
        data.add(new VideoBean("TestVideo-4", "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1974728075,625919050&fm=26&gp=0.jpg", "https://storage.googleapis.com/exoplayer-test-media-1/gen-3/screens/dash-vod-single-segment/video-137.mp4"));
        data.add(new VideoBean("TestVideo-5", "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3545561584,783374626&fm=26&gp=0.jpg", "https://storage.googleapis.com/exoplayer-test-media-1/mp4/dizzy-with-tx3g.mp4"));
        data.add(new VideoBean("TestVideo-6", "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=10400358,3341701483&fm=26&gp=0.jpg", "https://storage.googleapis.com/exoplayer-test-media-1/mp4/frame-counter-one-hour.mp4"));
        data.add(new VideoBean("TestVideo-7", "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1974728075,625919050&fm=26&gp=0.jpg", "https://storage.googleapis.com/exoplayer-test-media-1/gen-3/screens/dash-vod-single-segment/video-137.mp4"));
        data.add(new VideoBean("TestVideo-8", "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3545561584,783374626&fm=26&gp=0.jpg", "https://storage.googleapis.com/exoplayer-test-media-1/mp4/frame-counter-one-hour.mp4"));
        data.add(new VideoBean("TestVideo-9", "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=10400358,3341701483&fm=26&gp=0.jpg", "https://storage.googleapis.com/exoplayer-test-media-1/mp4/dizzy-with-tx3g.mp4"));

        VideoListView videoListView = findViewById(R.id.video_list_view);
        videoListView.setData(data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoListView videoListView = findViewById(R.id.video_list_view);
        videoListView.release();
    }
}
























