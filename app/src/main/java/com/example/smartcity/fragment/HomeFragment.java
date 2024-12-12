package com.example.smartcity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView; // 导入 TextView 类

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcity.R;
import com.example.smartcity.adapter.NoteAdapter;
import com.example.smartcity.entity.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import cn.leancloud.AVFile;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @Author he sheng yuan
 * @Date 2024/10/6 11:49
 * @Version 1.0
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> notes;
    private TextView anniversaryTextView;  // 修改为 TextView 类型

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notes = new ArrayList<>();

        // 绑定 TextView 来显示恋爱纪念日
        anniversaryTextView = view.findViewById(R.id.anniversaryText); // 绑定布局中的 TextView
        Button scrollToBottomButton = view.findViewById(R.id.scroll_to_bottom);

        // 从数据库中加载笔记数据
        loadNotes();

        // 计算恋爱时间
        calculateAnniversary();

        noteAdapter = new NoteAdapter(notes, (note, position) -> {
            // 点击事件处理，跳转到 DetailFragment
            DetailFragment detailFragment = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("noteId", note.getId());
            detailFragment.setArguments(bundle);

            // 跳转到 DetailFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(noteAdapter);
        // 设置按钮点击事件：滑动到 RecyclerView 的底部
        scrollToBottomButton.setOnClickListener(v -> {
            if (notes.size() > 0) {
                recyclerView.smoothScrollToPosition(notes.size() - 1);
            }
        });
        return view;
    }

    // 计算从 2022年6月12日 到今天的天数
    private void calculateAnniversary() {
        // 设定恋爱开始日期
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(2022, Calendar.JUNE, 12);  // 2022年6月12日

        // 获取当前日期
        Calendar currentCalendar = Calendar.getInstance();

        // 计算天数差
        long diffInMillis = currentCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        long daysDifference = TimeUnit.MILLISECONDS.toDays(diffInMillis);

        // 更新 UI 显示
        String anniversaryText = "恋爱：hsy∑=♡♡→wsj " + daysDifference + " 天";
        anniversaryTextView.setText(anniversaryText); // 显示恋爱天数
    }

    private void loadNotes() {
        AVQuery<AVObject> query = AVQuery.getQuery("Note");
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onNext(List<AVObject> avObjects) {
                notes.clear(); // 清空原有数据
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

                for (AVObject avObject : avObjects) {
                    String id = avObject.getObjectId();
                    String title = avObject.getString("title");
                    String content = avObject.getString("content");
                    AVFile imageFile = avObject.getAVFile("image"); // 获取 AVFile 对象
                    String imageUrl = (imageFile != null) ? imageFile.getUrl() : null;

                    Date createdAt = avObject.getCreatedAt(); // 获取创建时间
                    String formattedTime = sdf.format(createdAt); // 格式化时间

                    // 确保 Note 类包含时间字段
                    notes.add(new Note(id, title, content, imageUrl, formattedTime));
                }
                noteAdapter.notifyDataSetChanged(); // 刷新 RecyclerView
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace(); // 打印错误信息
            }

            @Override
            public void onComplete() {}
        });
    }
}
