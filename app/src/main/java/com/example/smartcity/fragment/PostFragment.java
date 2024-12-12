package com.example.smartcity.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.smartcity.R;
import com.example.smartcity.utils.LeanCloudUtils;

import cn.leancloud.AVFile;
import cn.leancloud.AVObject;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.io.InputStream;

/**
 * @Author he sheng yuan
 * @Date 2024/10/11 11:49
 * @Version 1.0
 */
public class PostFragment extends Fragment {

    private static final int SELECT_IMAGE_REQUEST_CODE = 1;

    private EditText noteTitle, noteContent;
    private Button btnSelectImage, btnUploadNote;
    private ImageView selectedImage;
    private Uri imageUri;
    private Bitmap imageBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // 绑定视图
        noteTitle = view.findViewById(R.id.note_title);
        noteContent = view.findViewById(R.id.note_content);
        btnSelectImage = view.findViewById(R.id.btn_select_image);
        btnUploadNote = view.findViewById(R.id.btn_upload_note);
        selectedImage = view.findViewById(R.id.selected_image);

        // 选择图片
        btnSelectImage.setOnClickListener(v -> selectImage());

        // 上传笔记
        btnUploadNote.setOnClickListener(v -> uploadNote());

        return view;
    }

    // 选择图片
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                imageBitmap = BitmapFactory.decodeStream(inputStream);
                selectedImage.setImageBitmap(imageBitmap);
                selectedImage.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 上传笔记
    private void uploadNote() {
        // 对话框
        showSuccessDialog();

        String title = noteTitle.getText().toString().trim();
        String content = noteContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty() || imageUri == null) {
            Toast.makeText(getContext(), "标题、内容和图片都不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String fileName = imageUri.getLastPathSegment();
            byte[] imageData = LeanCloudUtils.getBytesFromBitmap(imageBitmap);

            AVFile file = new AVFile(fileName, imageData);
            file.saveInBackground().subscribe(new Observer<AVFile>() {
                @Override
                public void onSubscribe(Disposable d) {}

                @Override
                public void onNext(AVFile avFile) {
                    String fileUrl = avFile.getUrl();
                    AVObject note = new AVObject("Note");
                    note.put("title", title);
                    note.put("content", content);
                    note.put("image", avFile);
                    note.saveInBackground().subscribe(new Observer<AVObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {}

                        @Override
                        public void onNext(AVObject avObject) {
                            Toast.makeText(getContext(), "笔记发布成功！", Toast.LENGTH_SHORT).show();

                            // 发布成功后跳转到 HomeFragment
                            getParentFragmentManager().popBackStack();  // 回退到 HomeFragment
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), "发布失败，请重试", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {}
                    });
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getContext(), "图片上传失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {}
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "笔记上传失败", Toast.LENGTH_SHORT).show();
        }
    }
    // 显示成功提示的弹窗
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("上传成功")
                .setMessage("笔记发布成功，请不要重复点击！")
                .setCancelable(false)  // 禁止点击外部关闭弹窗
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 回退到 HomeFragment
                        getParentFragmentManager().popBackStack();
                    }
                });

        // 创建并显示弹窗
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // 防止按钮多次点击（禁用按钮，直到用户点击一次后弹窗关闭）
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        // 延迟解锁按钮，使其只能点击一次
        positiveButton.postDelayed(() -> positiveButton.setEnabled(true), 1000);
    }
}
