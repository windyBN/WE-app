package com.example.smartcity.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * @Author he sheng yuan
 * @Date 2024/10/5 9:04
 * @Version 1.0
 */
public class GuideAdapter extends PagerAdapter {
    private final List<ImageView> imageViews;

    public GuideAdapter(List<ImageView> imageViews) {
        this.imageViews = imageViews;
    }

    /*
    * 获取当前要显示对象的数量*/
    @Override
    public int getCount() {
        return imageViews.size();
    }

    /*
    * 判断是否用对象生成界面*/
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    /*
    * 从ViewGroup中移除当前对象*/
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView(imageViews.get(position));

    }

    // 当前要显示的对象
    public Object instantiateItem(ViewGroup container, int position){
        container.addView(imageViews.get(position));
        return imageViews.get(position);
    }
}
