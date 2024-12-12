package com.example.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.MainActivity;
import com.example.smartcity.R;

import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
/**
 * @Author he sheng yuan
 * @Date 2024/10/5 8:06
 * @Version 1.0
 */
public class LoginActivity extends AppCompatActivity {

    private EditText edt_name;
    private EditText edt_psw;
    private Button btn_login;
    private Button btn_register;
    private Button btn_forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        edt_name = findViewById(R.id.edt_name);
        edt_psw = findViewById(R.id.edt_psw);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(v -> login());
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        // 初始化忘记密码按钮
        btn_forgot_password = findViewById(R.id.btn_forgot_password);
        btn_forgot_password.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class)));
    }

    private void login() {
        final String username = edt_name.getText().toString().trim();
        final String password = edt_psw.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 使用 LeanCloud 的 logIn 方法进行登录
        AVUser.logIn(username, password).subscribe(new Observer<AVUser>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                // 可以在这里添加进度条等操作
                Toast.makeText(getApplicationContext(), "登录中...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(AVUser user) {
                // 登录成功
                Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
                // 跳转到主界面
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish(); // 结束当前活动
            }

            @Override
            public void onError(Throwable throwable) {
                // 登录失败（可能是密码错误）
                Log.e("登录失败", throwable.getMessage());
                Toast.makeText(getApplicationContext(), "登录失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                // 可以在这里添加完成后的操作
                Toast.makeText(getApplicationContext(), "登录完成！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
