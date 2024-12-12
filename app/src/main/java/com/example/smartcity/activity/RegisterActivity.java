package com.example.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.R;

import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
/**
 * @Author he sheng yuan
 * @Date 2024/10/5 9:04
 * @Version 1.0
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_name;
    private EditText et_nicke;
    private EditText et_phonr;
    private EditText et_password;
    private RadioButton rb_man;
    private RadioButton rb_woman;
    private RadioGroup rg_sex;
    private Button btn_register;
    private Button btn_login;

    String sex = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        et_name = findViewById(R.id.et_name);
        et_nicke = findViewById(R.id.et_nicke);
        et_phonr = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        rb_man = findViewById(R.id.rb_man);
        rb_woman = findViewById(R.id.rb_woman);
        rg_sex = findViewById(R.id.rg_sex);
        btn_register = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);

        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        rg_sex.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == rb_man.getId()) {
                sex = "male"; // 或者 "1"
            } else {
                sex = "female"; // 或者 "0"
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_register) {
            Register();
        } else if (id == R.id.btn_login) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private void Register() {
        // validate
        String username = et_name.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        String nicke = et_nicke.getText().toString().trim();
        if (TextUtils.isEmpty(nicke)) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }

        String phone = et_phonr.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入你的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建用户实例
        AVUser user = new AVUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(nicke ); // 设置邮箱；默认qq邮箱
        user.setMobilePhoneNumber(phone); // 设置手机号码

        // 可选：设置其他属性
        user.put("gender", sex);

        // 注册用户
        user.signUpInBackground().subscribe(new Observer<AVUser>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                // 可以在这里添加进度条等操作
            }


            @Override
            public void onNext(AVUser user) {
                // 注册成功
                Toast.makeText(getApplicationContext(), "注册成功。objectId：" + user.getObjectId(), Toast.LENGTH_LONG).show();
                // 这里可以添加跳转到登录界面的代码
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish(); // 结束注册界面
            }

            @Override
            public void onError(Throwable throwable) {
                // 注册失败
                Log.e("注册失败", throwable.getMessage());
                Toast.makeText(getApplicationContext(), "注册失败：" + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {
                // 可以在这里添加完成后的操作
            }
        });
    }
}
