package com.example.xialc.weatherforecast;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter{
    private List<View> views;
    private Context context;

    public ViewPagerAdapter(List<View> views, Context context){
        //建立构造方法,传入views以及context;
        this.views = views;
        this.context = context;

    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //当view不再使用时,销毁view;
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //添加view;
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public int getCount() {
        //返回view的总数量;
        return views.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
