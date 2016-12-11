package com.yyg.photoselect.photoselector.model;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by liaoyong on 2016/11/21.
 */
public class GlideModelConfig implements GlideModule{

        //在这里创建设置内容,之前文章所提及的图片质量就可以在这里设置
        //还可以设置缓存池参数什么的
        @Override
        public void applyOptions(Context context, GlideBuilder builder) {
            builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
        }

        //在这里注册ModelLoaders
        //可以在这里清除缓存什么的
        //需要在子线程中操作
        @Override
        public void registerComponents(Context context, Glide glide) {
//            glide.clearDiskCache();
        }

}
