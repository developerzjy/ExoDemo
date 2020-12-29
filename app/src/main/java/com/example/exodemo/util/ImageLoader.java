package com.example.exodemo.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.exodemo.R;


public class ImageLoader {

    public static void load(final ImageView imageView, Object imageSource) {
        load(imageView, imageSource, R.color.gray);
    }

    public static void load(final ImageView imageView, Object imageSource, int placeHolderRes, Transformation<Bitmap> bitmapTransformations) {
        load(imageView, imageSource, placeHolderRes, false, false, null, bitmapTransformations);
    }

    public static void load(final ImageView imageView, Object imageSource, LoadRequestListener loadRequestListener) {
        load(imageView, imageSource, R.color.gray, false, false, loadRequestListener, null);
    }

    public static void load(final ImageView imageView, Object imageSource, int placeHolderRes) {
        load(imageView, imageSource, placeHolderRes, false, false, null, null);
    }

    public static void load(final ImageView imageView, Object imageSource, int placeHolderRes,
                            boolean crossFade, boolean centerCrop,
                            final LoadRequestListener loadRequestListener,
                            Transformation<Bitmap> bitmapTransformations) {

        RequestBuilder<Drawable> requestBuilder;

        requestBuilder = Glide
                .with(imageView.getContext())
                .load(imageSource);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

        if (centerCrop) {
            requestOptions.centerCrop();
        } else {
            requestOptions.fitCenter();
        }

        if (placeHolderRes != -1) {
            requestOptions.placeholder(placeHolderRes);
        }
        if (!crossFade) {
            requestOptions.dontAnimate();
        } else {
            requestBuilder.transition(new DrawableTransitionOptions().crossFade(200));
        }

        if (bitmapTransformations != null) {
            requestOptions.transform(bitmapTransformations);
        }

        requestBuilder.listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if (loadRequestListener != null)
                    loadRequestListener.onError(e);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (loadRequestListener != null)
                    loadRequestListener.onSuccess(resource);
                return false;
            }
        });

        requestBuilder.apply(requestOptions);

        requestBuilder.into(imageView);
    }

    public interface LoadRequestListener {
        void onSuccess(Drawable drawable);

        void onError(Exception e);
    }
}
