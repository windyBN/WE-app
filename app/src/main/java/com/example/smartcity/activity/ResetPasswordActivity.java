package com.example.smartcity.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.R;

import java.util.List;

import cn.leancloud.AVUser;
import cn.leancloud.types.AVNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @Author he sheng yuan
 * @Date 2024/10/13 13:23
 * @Version 1.0
 */
public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtEmail;
    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
    }

    private void initView() {
        edtUsername = findViewById(R.id.edt_username);
        edtEmail = findViewById(R.id.edt_email);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
            return;
        }

        // 查询用户匹配
        AVUser.getQuery().whereEqualTo("username", username).whereEqualTo("email", email)
                .findInBackground().subscribe(new Observer<List<AVUser>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Toast.makeText(getApplicationContext(), "验证中...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<AVUser> users) {
                        if (!users.isEmpty()) {
                            // 发送重置密码邮件
                            sendPasswordResetEmail(email);
                        } else {
                            Toast.makeText(getApplicationContext(), "用户名或邮箱不匹配", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("验证失败", e.getMessage());
                        Toast.makeText(getApplicationContext(), "验证失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    // 发送重置密码邮件
    private void sendPasswordResetEmail(String email) {
        AVUser.requestPasswordResetInBackground(email).subscribe(new Observer<AVNull>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(AVNull avNull) {
                Toast.makeText(getApplicationContext(), "重置邮件已发送，请查收", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("重置失败", e.getMessage());
                Toast.makeText(getApplicationContext(), "重置失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
            }
        });
    }
}
