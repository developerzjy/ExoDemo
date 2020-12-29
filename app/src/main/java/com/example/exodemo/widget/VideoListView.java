package com.example.exodemo.widget;

import android.content.Context;
import android.util.AttributeSet;
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

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private PagerSnapHelper snapHelper;
    private RecyclerViewPageChangeListenerHelper mPageChangeListener;
    private SimplePlayerView mPlayerView;
    private OnPageChangeListener mPlayerPageListener;

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
        if (mPlayerPageListener != null) {
            mPageChangeListener.removePageChangeListener(mPlayerPageListener);
        }
        mPlayerPageListener = new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position, View itemView, boolean isNext) {
                changePlayerContainer(itemView);
                if (isNext) {
                    mPlayerView.playNext();
                } else {
                    mPlayerView.playPrevious();
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

            if (isNewData && helper.getAdapterPosition() == 0) {
                isNewData = false;

                helper.itemView.post(() -> {
                    changePlayerContainer(helper.itemView);
                    mPlayerView.start();
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
        ViewParent playerParent = mPlayerView.getParent();
        if (playerParent instanceof ViewGroup) {
            ((ViewGroup) playerParent).removeView(mPlayerView);
        }
        ViewGroup newContainer = itemView.findViewById(R.id.player_container);
        newContainer.addView(mPlayerView);
    }

    public void setData(List<VideoBean> data) {
        mPlayerView.clearVideoPath();
        for (VideoBean video : data) {
            mPlayerView.addVideoPath(video.getVideoUrl());
        }

        adapter.setNewData(data);
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
