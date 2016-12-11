package com.yyg.photoselect.photoselector.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yyg.photoselect.R;

/**
 * Created by liaoyong on 2016/11/22.
 */
public class LoadUtils {

    public static void loadImage(Context context, String path, ImageView imageView) {
        Glide.with(context).load(path)
                .placeholder(R.drawable.ic_picture_loading)//未加载
                .error(R.drawable.ic_picture_loadfailed)//加载失败
                //去除grid默认动画，否则可能显示有问题
                .dontAnimate()
                .into(imageView);
    }
}
