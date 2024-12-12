package com.example.smartcity.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.smartcity.R;
import com.example.smartcity.adapter.GuideAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author he sheng yuan
 * @Date 2024/10/5 8:09
 * @Version 1.0
 */
public class GuideActivity extends AppCompatActivity {

    private ViewPager guide_vp;
    private Button guide_btn;
    private List<ImageView> imageViews;
    private ImageView[] dotViews;
    private int[] imgs= {R.drawable.g1,R.drawable.g2,R.drawable.g3,R.drawable.g4};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        guide_vp = findViewById(R.id.guide_vp);
        guide_btn = findViewById(R.id.guide_btn);


        // 初始化图片
        initImage();
        // 初始化底部圆点指示器
        initPoint();
        GuideAdapter adapter = new GuideAdapter(imageViews);
        guide_vp.setAdapter(adapter);

        guide_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putString("isFirst","1");
                editor.commit();
                Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        guide_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i= 0; i< dotViews.length; i++){
                    if (position== i){
                        dotViews[i].setImageResource(R.drawable.guide_selector);
                    }else {
                        dotViews[i].setImageResource(R.drawable.guide_white);
                    }

                    if (position== dotViews.length- 1){
                        guide_btn.setVisibility(View.VISIBLE);
                    }else {
                        guide_btn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state){

            }
        });

    }


    private void initPoint() {
        LinearLayout layout= findViewById(R.id.guide_ll);
        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(20, 20);
        params.setMargins(10, 0, 10, 0);
        dotViews= new ImageView[imgs.length];
        for (int i= 0; i< imageViews.size(); i++){
            ImageView imageView= new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.guide_white);
            if (i== 0){
                imageView.setImageResource(R.drawable.guide_selector);
            }else{
                imageView.setImageResource(R.drawable.guide_white);
            }
            dotViews[i]= imageView;
            final int finalI = i;
            dotViews[i].setOnClickListener(view -> guide_vp.setCurrentItem(finalI));
            layout.addView(imageView);
        }
    }

    private void initImage() {
        ViewPager.LayoutParams params= new ViewPager.LayoutParams();
        imageViews= new ArrayList<ImageView>();
        for (int i= 0; i< imgs.length; i++){
            ImageView imageView= new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setImageResource(imgs[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViews.add(imageView);
        }
    }

}
