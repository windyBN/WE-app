package com.example.smartcity.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.R;
/**
 * @Author he sheng yuan
 * @Date 2024/10/5 7:04
 * @Version 1.0
 */
public class SplashActivity extends AppCompatActivity {

    private LinearLayout main_ll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        main_ll = findViewById(R.id.main_ll);
        // 设置渐变效果
        setAlphaAnimation();
    }

    private void setAlphaAnimation() {
        // 生成动画对象
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        // 生成持续时间3s
        alphaAnimation.setDuration(2000);
        // 给控件设置动画
        main_ll.setAnimation(alphaAnimation);
        // 设置动画监听
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                jumpToMainActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    // 跳转到登录界面
    private void jumpToMainActivity() {
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        String isFirst = sp.getString("isFirst", "0");
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        if (isFirst.equals("0")) {
            intent.setClass(this,GuideActivity.class);
        } else {
            intent.setClass(this,LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }


}