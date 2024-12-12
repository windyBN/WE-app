package com.example.smartcity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.smartcity.MainActivity;
import com.example.smartcity.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import cn.leancloud.AVFile;
import cn.leancloud.AVObject;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
/**
 * @Author he sheng yuan
 * @Date 2024/10/5 14:06
 * @Version 1.0
 */

public class UserFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView userImageView;
    private TextInputEditText userNameEditText;
    private Button uploadImageButton, saveButton;
    private Uri imageUri;
    private AVUser currentUser;
    private View userContent;
    private FrameLayout musicContainer;
    

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // 初始化视图
        initViews(view);
        
        // 设置点击事件
        setClickListeners();

        return view;
    }

    private void initViews(View view) {
        try {
            userImageView = view.findViewById(R.id.userImageView);
            userNameEditText = view.findViewById(R.id.userNameEditText);
            uploadImageButton = view.findViewById(R.id.uploadImageButton);
            saveButton = view.findViewById(R.id.saveButton);
            userContent = view.findViewById(R.id.user_content);
            musicContainer = view.findViewById(R.id.music_container);
            
            MaterialCardView cardMusic = view.findViewById(R.id.card_music);
            if (cardMusic != null) {
                cardMusic.setOnClickListener(v -> navigateToMusic());
            }
            
            currentUser = AVUser.getCurrentUser();
            
            // 如果有用户数据，加载用户信息
            if (currentUser != null) {
                loadUserInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null && isAdded()) {
                Toast.makeText(getContext(), "初始化失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setClickListeners() {
        try {
            if (uploadImageButton != null) {
                uploadImageButton.setOnClickListener(v -> openImageChooser());
            }
            
            if (saveButton != null) {
                saveButton.setOnClickListener(v -> saveUserInfo());
            }
            

        } catch (Exception e) {
            e.printStackTrace();
            showToast("设置点击事件失败：" + e.getMessage());
        }
    }

    private void navigateToMusic() {
        try {
            // 隐藏用户界面内容
            userContent.setVisibility(View.GONE);
            // 显示音乐容器
            musicContainer.setVisibility(View.VISIBLE);
            
            // 创建并添加 MusicFragment
            MusicFragment musicFragment = new MusicFragment();
            getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.music_container, musicFragment)
                .addToBackStack(null)
                .commit();
                
            // 处理返回键
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), 
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // 如果可以弹出返回栈
                        if (getChildFragmentManager().popBackStackImmediate()) {
                            // 显示用户界面内容
                            userContent.setVisibility(View.VISIBLE);
                            // 隐藏音乐容器
                            musicContainer.setVisibility(View.GONE);
                            // 移除这个回调
                            remove();
                        }
                    }
                });
                
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(getContext(), "跳转失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showToast(String message) {
        if (getContext() != null && isAdded()) {
            try {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            // 清理资源
            if (imageUri != null) {
                imageUri = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                userImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void saveUserInfo() {
        String userName = userNameEditText.getText().toString().trim();
        if (userName.isEmpty() || imageUri == null) {
            Toast.makeText(getContext(), "请填写昵称并选择头像", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            // 创建 LeanCloud 文件
            AVFile file = new AVFile("avatar.jpg", byteArray);
            file.saveInBackground().subscribe(new Observer<AVFile>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(AVFile avFile) {
                    // 保存用户信息
                    currentUser.put("nickname", userName);
                    currentUser.put("avatar", avFile.getUrl()); // 保存头像URL
                    currentUser.saveInBackground().subscribe(new Observer<AVObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(AVObject avObject) {
                            Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), "保存失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getContext(), "上传头像失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserInfo() {
        // 加载当前用户的昵称和头像
        if (currentUser != null) {
            String userName = currentUser.getString("nickname");
            String avatarUrl = currentUser.getString("avatar");

            userNameEditText.setText(userName);
            if (avatarUrl != null) {
                // 使用 Picasso 加载头像
                Picasso.get()
                        .load(avatarUrl)
                        .placeholder(R.drawable.m5) // 可选的占位符图片
                        .error(R.drawable.h5)      // 可选的加载错误时显示的图片
                        .into(userImageView);
            }
        }
    }
}
