package com.example.smartcity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.smartcity.fragment.HomeFragment;
import com.example.smartcity.fragment.NewFragment;
import com.example.smartcity.fragment.PostFragment;
import com.example.smartcity.fragment.ServiceFragment;
import com.example.smartcity.fragment.UserFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author he sheng yuan
 * @Date 2024/10/5 8:06
 * @Version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MainTabAdapter mainTabAdapter;
    private TabLayout tabLayout;
    private List<Fragment> fragmentList;
    private String[] titles = {"广场", "AIbot", "发布", "聊天", "我的"};
    private int[] unSele = {R.drawable.m1, R.drawable.m2, R.drawable.m3, R.drawable.m4, R.drawable.m5};
    private int[] onSele = {R.drawable.h1, R.drawable.h2, R.drawable.h3, R.drawable.h4, R.drawable.h5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tablayout); // 确保使用正确的 TabLayout
        initData();
    }

    public void initData() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new ServiceFragment());
        fragmentList.add(new PostFragment());
        fragmentList.add(new NewFragment());
        fragmentList.add(new UserFragment());

        MainTabAdapter mainTabAdapter = new MainTabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainTabAdapter);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(mainTabAdapter.getView(i));
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) {
                    ImageView img = view.findViewById(R.id.img);
                    TextView tv = view.findViewById(R.id.tv);
                    String title = tv.getText().toString();

                    if (title.equals("广场")) {
                        img.setImageResource(onSele[0]);
                    } else if (title.equals("AIbot")) {
                        img.setImageResource(onSele[1]);
                    } else if (title.equals("发布")) {
                        img.setImageResource(onSele[2]);
                    } else if (title.equals("聊天")) {
                        img.setImageResource(onSele[3]);
                    } else if (title.equals("我的")) {
                        img.setImageResource(onSele[4]);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) {
                    ImageView img = view.findViewById(R.id.img);
                    TextView tv = view.findViewById(R.id.tv);
                    String title = tv.getText().toString();

                    if (title.equals("广场")) {
                        img.setImageResource(unSele[0]);
                    } else if (title.equals("AIbot")) {
                        img.setImageResource(unSele[1]);
                    } else if (title.equals("发布")) {
                        img.setImageResource(unSele[2]);
                    } else if (title.equals("聊天")) {
                        img.setImageResource(unSele[3]);
                    } else if (title.equals("我的")) {
                        img.setImageResource(unSele[4]);
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 处理重新选择的情况
            }
        });
    }

    public class MainTabAdapter extends FragmentPagerAdapter {

        public MainTabAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        public View getView(int position) {
            View view = View.inflate(MainActivity.this, R.layout.main_tab_item, null);
            ImageView img = view.findViewById(R.id.img);
            TextView tv = view.findViewById(R.id.tv);

            // 设置初始图标
            if (tabLayout.getTabAt(position) != null && tabLayout.getTabAt(position).isSelected()) {
                img.setImageResource(onSele[position]);
            } else {
                img.setImageResource(unSele[position]);
            }

            tv.setText(titles[position]);
            return view;
        }

        public void replaceFragment(int position, Fragment newFragment) {
            if (position >= 0 && position < fragmentList.size()) {
                fragmentList.set(position, newFragment);
                notifyDataSetChanged();
            }
        }
    }

    // 添加公共方法用于Fragment替换
    public void replaceFragment(Fragment newFragment, int position) {
        if (mainTabAdapter != null) {
            mainTabAdapter.replaceFragment(position, newFragment);
        }
    }

    // 添加 getter 方法
    public List<Fragment> getFragmentList() {
        return fragmentList;
    }
}
