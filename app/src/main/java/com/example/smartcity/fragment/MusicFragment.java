package com.example.smartcity.fragment;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcity.R;
import com.example.smartcity.adapter.MusicAdapter;
import com.example.smartcity.entity.Music;
import com.example.smartcity.util.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
/**
 * @Author he sheng yuan
 * @Date 2024/12/2 13:49
 * @Version 1.0
 */
public class MusicFragment extends Fragment implements MusicAdapter.OnMusicClickListener {
    private RecyclerView musicRecyclerView;
    private TextInputEditText searchEditText;
    private ImageButton btnPlayPause, btnPrevious, btnNext, btnBack;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private MusicAdapter adapter;
    private List<Music> musicList;
    private int currentPlayingPosition = -1;
    private Handler handler = new Handler();
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY = 1000; // 1秒延迟

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        initViews(view);
        setupMusicList();
        setupClickListeners();
        return view;
    }

    private void initViews(View view) {
        musicRecyclerView = view.findViewById(R.id.music_recycler_view);
        searchEditText = view.findViewById(R.id.search_edit_text);
        btnPlayPause = view.findViewById(R.id.btn_play_pause);
        btnPrevious = view.findViewById(R.id.btn_previous);
        btnNext = view.findViewById(R.id.btn_next);
        btnBack = view.findViewById(R.id.btn_back);
        seekBar = view.findViewById(R.id.seek_bar);

        musicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupMusicList() {
        // 默认音乐列表只保留一首歌
        musicList = new ArrayList<>();
        musicList.add(new Music("春庭雪", "等什么君", "4:35", "sample"));  // url设为"sample"用于标识本地音乐

        if (adapter == null) {
            adapter = new MusicAdapter(musicList, this);
            musicRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    pauseMusic();
                } else {
                    playMusic();
                }
            }
        });

        btnPrevious.setOnClickListener(v -> playPrevious());
        btnNext.setOnClickListener(v -> playNext());

        // 修改搜索框的监听器
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 取消之前的搜索任务
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                
                // 如果搜索框为空，显示默认列表
                if (s.toString().trim().isEmpty()) {
                    setupMusicList(); // 显示默认列表
                    return;
                }
                
                // 创建新的搜索任务
                searchRunnable = () -> performSearch(s.toString().trim());
                
                // 延迟执行搜索
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 添加搜索按钮的点击事件
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    // 立即执行搜索
                    if (searchRunnable != null) {
                        searchHandler.removeCallbacks(searchRunnable);
                    }
                    performSearch(query);
                }
                return true;
            }
            return false;
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void performSearch(String query) {
        if (getContext() == null) return;
        
        // 显示加载提示
        Toast.makeText(getContext(), "获取音乐中...", Toast.LENGTH_SHORT).show();
        
        ApiService.searchMusic(query, new ApiService.OnSearchResultListener() {
            @Override
            public void onSuccess(List<Music> results) {
                if (getContext() == null) return;
                
                if (results.isEmpty()) {
                    Toast.makeText(getContext(), "没有找到音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // 更新列表
                musicList.clear();
                musicList.addAll(results);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                if (getContext() == null) return;
                Toast.makeText(getContext(), "获取失败: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playOnlineMusic(String url) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            
            // 显示加载提示
            Toast.makeText(getContext(), "加载中...", Toast.LENGTH_SHORT).show();
            
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            
            mediaPlayer.setOnPreparedListener(mp -> {
                Toast.makeText(getContext(), "开始播放", Toast.LENGTH_SHORT).show();
                playMusic();
                updateSeekBar();
            });
            
            mediaPlayer.setOnCompletionListener(mp -> {
                Toast.makeText(getContext(), "播放完成", Toast.LENGTH_SHORT).show();
                playNext();
            });
            
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(getContext(), "播放失败，请尝试其他歌曲", Toast.LENGTH_SHORT).show();
                return false;
            });
            
        } catch (Exception e) {
            Toast.makeText(getContext(), "播放出错: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMusicClick(Music music, int position) {
        currentPlayingPosition = position;
        
        if ("sample".equals(music.getUrl())) {
            // 播放本地示例音乐
            playLocalMusic();
        } else {
            // 播放在线音乐
            playOnlineMusic(music.getUrl());
        }
        
        // 显示当前播放的歌曲信息
        Toast.makeText(getContext(), 
            "正在播放: " + music.getTitle() + " - " + music.getArtist(), 
            Toast.LENGTH_SHORT).show();
    }

    private void playLocalMusic() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(getContext(), R.raw.sample_music);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(mp -> {
                    Toast.makeText(getContext(), "播放完成", Toast.LENGTH_SHORT).show();
                    playNext();
                });
                playMusic();
                updateSeekBar();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "播放出错: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            btnPlayPause.setImageResource(R.drawable.ic_pause);
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void playNext() {
        if (currentPlayingPosition < musicList.size() - 1) {
            currentPlayingPosition++;
            onMusicClick(musicList.get(currentPlayingPosition), currentPlayingPosition);
        }
    }

    private void playPrevious() {
        if (currentPlayingPosition > 0) {
            currentPlayingPosition--;
            onMusicClick(musicList.get(currentPlayingPosition), currentPlayingPosition);
        }
    }

    private void updateSeekBar() {
        if (getActivity() == null || mediaPlayer == null) return;
        
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 100);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
        // 清理搜索相关的资源
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}