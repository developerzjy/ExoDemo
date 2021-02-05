package com.example.exodemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.exodemo.R;
import com.example.exodemo.bean.VideoBean;
import com.example.exodemo.player.AbsPlayerView;
import com.example.exodemo.player.PlayerCacheManager;
import com.example.exodemo.player.SimplePlayerView;
import com.example.exodemo.util.ImageLoader;
import com.example.exodemo.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: zhangjianyang
 * Date: 2020/12/22
 */
public class VideoListView extends FrameLayout {

    private static final String TAG = "VideoListView";

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private PagerSnapHelper snapHelper;
    private RecyclerViewPageChangeListenerHelper mPageChangeListener;
    private SimplePlayerView mPlayerView;
    private OnPageChangeListener mPlayerPageListener;

    private View mCurItemView;

    public VideoListView(@NonNull Context context) {
        this(context, null);
    }

    public VideoListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_video_list, this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);

        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        mPageChangeListener = new RecyclerViewPageChangeListenerHelper(snapHelper);
        recyclerView.addOnScrollListener(mPageChangeListener);

        initPlayerView();
    }

    public void initPlayerView() {
        mPlayerView = new SimplePlayerView(getContext());

        mPlayerView.addProgressListener((duration, currentPosition, bufferedPosition) -> {
            Log.d(TAG, "ProgressListener: currentPosition=" + currentPosition + " duration=" + duration);
        });

        mPlayerView.addVideoListener(new AbsPlayerView.OnVideoListener() {
            @Override
            public void onRenderedFirstFrame() {
                mCurItemView.findViewById(R.id.cover).setVisibility(GONE);
            }
        });

        if (mPlayerPageListener != null) {
            mPageChangeListener.removePageChangeListener(mPlayerPageListener);
        }
        mPlayerPageListener = new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position, View itemView, boolean isNext) {
                changePlayerContainer(itemView);
                if (!mPlayerView.isPlayWhenReady()) {
                    mPlayerView.start();
                }
            }
        };
        mPageChangeListener.addOnPageChangeListener(mPlayerPageListener);
    }

    private class MyAdapter extends BaseQuickAdapter<VideoBean, BaseViewHolder> {

        private boolean isNewData = false;

        public MyAdapter() {
            super(R.layout.item_video);
        }

        @Override
        protected void convert(BaseViewHolder helper, VideoBean item) {
            helper.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtil.getRealScreenHeight(helper.itemView.getContext())));
            ImageLoader.load(helper.getView(R.id.cover), item.getCoverUrl());
            helper.itemView.setTag(R.id.tag_note_book, item);

            //预缓存视频
            PlayerCacheManager.getInstance().preCache(item.getVideoUrl());

            if (isNewData && helper.getAdapterPosition() == 0) {
                isNewData = false;

                helper.itemView.post(() -> {
                    changePlayerContainer(helper.itemView);
                });
            }
        }

        @Override
        public void setNewData(@Nullable List<VideoBean> data) {
            isNewData = true;
            super.setNewData(data);
        }
    }

    private void changePlayerContainer(View itemView) {
        ViewParent oldParent = mPlayerView.getParent();
        if (oldParent instanceof ViewGroup) {
            ((ViewGroup) oldParent).removeView(mPlayerView);
            ViewGroup oldRoot = (ViewGroup) oldParent.getParent();
            oldRoot.findViewById(R.id.cover).setVisibility(VISIBLE);
        }

        mCurItemView = itemView;
        ViewGroup newParent = itemView.findViewById(R.id.player_container);
        newParent.addView(mPlayerView, 0);

        VideoBean videoBean = (VideoBean) itemView.getTag(R.id.tag_note_book);

        mPlayerView.setVideoPath(videoBean.getVideoUrl(), true, true);
    }

    public void setData(List<VideoBean> data) {
        adapter.setNewData(data);
    }

    public void onResume() {
        mPlayerView.start();
    }

    public void onPause() {
        mPlayerView.pause();
    }

    public void release() {
        mPlayerView.release();
    }

    public void addOnPageChangeListener(OnPageChangeListener listener) {
        mPageChangeListener.addOnPageChangeListener(listener);
    }

    public static class RecyclerViewPageChangeListenerHelper extends RecyclerView.OnScrollListener {
        private SnapHelper snapHelper;
        private int oldPosition = -1;
        List<OnPageChangeListener> pageChangeListeners = new ArrayList<>();

        public RecyclerViewPageChangeListenerHelper(SnapHelper snapHelper) {
            this.snapHelper = snapHelper;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            for (OnPageChangeListener listener : pageChangeListeners) {
                listener.onScrolled(recyclerView, dx, dy);
            }
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            int position;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            View view = snapHelper.findSnapView(layoutManager);
            if (view != null && layoutManager != null) {
                position = layoutManager.getPosition(view);

                for (OnPageChangeListener listener : pageChangeListeners) {
                    listener.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && oldPosition != position) {
                        boolean isNext = oldPosition < position;
                        oldPosition = position;
                        listener.onPageSelected(position, view, isNext);
                    }
                }
            }
        }

        public void addOnPageChangeListener(OnPageChangeListener listener) {
            pageChangeListeners.add(listener);
        }

        public void removePageChangeListener(OnPageChangeListener listener) {
            pageChangeListeners.remove(listener);
        }

        public void clearPageChangeListener() {
            pageChangeListeners.clear();
        }
    }

    public interface OnPageChangeListener {
        default void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        }

        default void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }

        default void onPageSelected(int position, View itemView, boolean isNext) {
        }
    }

}
