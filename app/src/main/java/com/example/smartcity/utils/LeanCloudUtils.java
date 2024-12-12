package com.example.smartcity.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.smartcity.R;
import com.squareup.picasso.Picasso;

import cn.leancloud.AVFile;
import cn.leancloud.AVObject;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.io.ByteArrayOutputStream;

/**
 * @Author he sheng yuan
 * @Date 2024/10/7 22:08
 * @Version 1.0
 */
public class LeanCloudUtils {

    // 将 Bitmap 转换为字节数组
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    // 上传图片并保存笔记
    public static void uploadNote(String title, String content, Bitmap bitmap) {
        byte[] imageData = getBytesFromBitmap(bitmap);

        // 上传图片文件
        AVFile file = new AVFile("note_image.jpg", imageData);
        file.saveInBackground().subscribe(new Observer<AVFile>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onNext(AVFile avFile) {
                // 图片上传成功后，保存笔记
                AVObject note = new AVObject("Note");
                note.put("title", title);
                note.put("content", content);
                note.put("image", avFile);

                note.saveInBackground().subscribe(new Observer<AVObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(AVObject avObject) {
                        // 笔记保存成功
                        System.out.println("笔记保存成功！");
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 笔记保存失败
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {}
                });
            }

            @Override
            public void onError(Throwable e) {
                // 图片上传失败
                e.printStackTrace();
            }

            @Override
            public void onComplete() {}
        });
    }

    // 使用 Picasso 加载图片
    public static void loadImage(ImageView imageView, AVFile avFile) {
        if (avFile != null) {
            // 使用 Picasso 加载 AVFile 的 URL
            Picasso.get()
                    .load(avFile.getUrl()) // 获取 AVFile 的 URL
                    .placeholder(R.drawable.y0) // 可选：占位图
                    .error(R.drawable.y1) // 可选：错误图
                    .into(imageView);
        } else {
            // 如果 AVFile 为空，可以设置一个默认图像
            imageView.setImageResource(R.drawable.y3);
        }
    }
}
