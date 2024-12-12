package com.example.smartcity.util;

import com.example.smartcity.entity.Music;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
/**
 * @Author he sheng yuan
 * @Date 2024/12/2 13:49
 * @Version 1.0
 */
public class ApiService {
    // 使用免费音乐API
    private static final String SEARCH_API = "https://api.uomg.com/api/rand.music?sort=热歌榜&format=json";
    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface OnSearchResultListener {
        void onSuccess(List<Music> musicList);
        void onError(String error);
    }

    public static void searchMusic(String keyword, OnSearchResultListener listener) {
        new Thread(() -> {
            try {
                URL url = new URL(SEARCH_API);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                Log.d("ApiService", "Response: " + response.toString());
                
                List<Music> musicList = parseJson(response.toString());
                mainHandler.post(() -> listener.onSuccess(musicList));
                
            } catch (Exception e) {
                Log.e("ApiService", "Error: " + e.getMessage());
                mainHandler.post(() -> listener.onError(e.getMessage()));
            }
        }).start();
    }

    private static List<Music> parseJson(String json) throws Exception {
        List<Music> musicList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        
        if (jsonObject.getInt("code") == 1) {  // 成功状态码
            JSONObject data = jsonObject.getJSONObject("data");
            
            Music music = new Music();
            music.setTitle(data.getString("name"));
            music.setArtist(data.getString("artistsname"));
            music.setUrl(data.getString("url"));
            music.setDuration("3:30");  // API没有提供时长，使用默认值
            
            musicList.add(music);
        }
        
        return musicList;
    }
}