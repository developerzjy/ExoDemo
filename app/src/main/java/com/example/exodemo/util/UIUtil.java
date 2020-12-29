package com.example.exodemo.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class UIUtil {

    public static int getRealScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

}
