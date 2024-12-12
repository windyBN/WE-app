package com.example.smartcity.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcity.R;
import com.example.smartcity.adapter.CommentsAdapter;
import com.example.smartcity.utils.LeanCloudUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @Author he sheng yuan
 * @Date 2024/10/11 11:49
 * @Version 1.0
 */
public class DetailFragment extends Fragment {

    private TextView noteTitle, noteContent;
    private ImageView noteImage;
    private Button likeButton, favoriteButton, submitCommentButton;
    private EditText commentEditText;
    private RecyclerView commentsRecyclerView;
    private String noteId;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // 获取传入的笔记ID
        if (getArguments() != null) {
            noteId = getArguments().getString("noteId");
        }

        // 绑定视图
        noteTitle = view.findViewById(R.id.note_title);
        noteContent = view.findViewById(R.id.note_content);
        noteImage = view.findViewById(R.id.note_image);
        likeButton = view.findViewById(R.id.btn_like);
        favoriteButton = view.findViewById(R.id.btn_favorite);
        commentEditText = view.findViewById(R.id.comment_edit_text);
        submitCommentButton = view.findViewById(R.id.btn_submit_comment);
        commentsRecyclerView = view.findViewById(R.id.comments_recycler_view);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // 设置布局管理器

        // 加载笔记详情
        loadNoteDetails();

        // 提交评论按钮监听
        submitCommentButton.setOnClickListener(v -> submitComment());

        return view;
    }

    // 加载笔记详情
    private void loadNoteDetails() {
        AVQuery<AVObject> query = AVQuery.getQuery("Note");
        query.getInBackground(noteId).subscribe(new Observer<AVObject>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onNext(AVObject note) {
                noteTitle.setText(note.getString("title"));
                noteContent.setText(note.getString("content"));
                LeanCloudUtils.loadImage(noteImage, note.getAVFile("image"));
                loadComments();  // 加载评论
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getContext(), "加载笔记失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {}
        });
    }

    // 提交评论
    private void submitComment() {
        String commentText = commentEditText.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(getContext(), "评论不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        submitCommentButton.setEnabled(false);  // 禁用按钮防止重复点击

        AVObject comment = new AVObject("Comment");
        comment.put("noteId", noteId);
        comment.put("text", commentText);

        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            comment.put("user", currentUser);
        }

        comment.saveInBackground().subscribe(new Observer<AVObject>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onNext(AVObject avObject) {
                commentEditText.setText("");  // 清空输入框
                loadComments();  // 重新加载评论
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getContext(), "评论提交失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                submitCommentButton.setEnabled(true);  // 恢复按钮
            }
        });
    }

    // 加载评论
    private void loadComments() {
        AVQuery<AVObject> query = AVQuery.getQuery("Comment");
        query.include("author");
        query.whereEqualTo("noteId", noteId);
        query.include("user");  // 确保关联加载用户对象
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onNext(List<AVObject> comments) {
                CommentsAdapter commentsAdapter = new CommentsAdapter(comments); // 创建适配器
                commentsRecyclerView.setAdapter(commentsAdapter); // 设置适配器

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getContext(), "加载评论失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {}
        });
    }

    // 格式化日期
    private String formatDate(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
}
