package com.example.smartcity.utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author he sheng yuan
 * @Date 2024/10/15 16:58
 * @Version 1.0
 */
public class WenXinUtil {

    private static final String API_KEY = "QEF10qQXFEixgqEbRSEM52qP";
    private static final String SECRET_KEY = "aiiWkPvvUYX9RRNltxkzHmnfX2vam0yH";

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private final JSONArray dialogueContent;
    private String accessToken;

    public WenXinUtil() {
        dialogueContent = new JSONArray();
    }

    public String getAnswer(String userMsg) throws IOException, JSONException {
        // 获取有效的 Access Token
        String token = getAccessToken();
        if (token.isEmpty()) {
            return "获取 Access Token 失败。";
        }

        // 构建用户输入的 JSON 对象
        JSONObject userInput = new JSONObject();
        userInput.put("role", "user");
        userInput.put("content", userMsg);
        dialogueContent.put(userInput);

        // 构建请求体 JSON
        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("messages", dialogueContent);
        requestBodyJson.put("system", "你是一个智能助手，可以进行多语言对话。");
        requestBodyJson.put("disable_search", false);
        requestBodyJson.put("enable_citation", false);

        // 构建请求体
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"), requestBodyJson.toString()
        );

        // 构建请求
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token="
                        + token) // 确保使用新的 token
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        // 执行请求并解析响应
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();

                // 打印完整的 JSON 响应
                Log.d("WenXin Response", responseBody);
                System.out.println("Response JSON: " + responseBody);

                JSONObject jsonResponse = new JSONObject(responseBody);
                String result = jsonResponse.optString("result", "No result found");

                // 将 AI 的回答添加到对话内容中
                JSONObject assistantResponse = new JSONObject();
                assistantResponse.put("role", "assistant");
                assistantResponse.put("content", result);
                dialogueContent.put(assistantResponse);

                return result;
            } else {
                Log.e("WenXin", "请求失败: " + response);
                String errorBody = response.body() != null ? response.body().string() : "无响应体";
                Log.e("WenXin", "错误详情: " + errorBody);
                return "AI 无法处理您的请求，请稍后再试。";
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();  // 打印异常的详细信息
            return "出现错误: " + e.getMessage();
        }
    }


    //private String accessToken;
    private long tokenExpiryTime; // 存储令牌的过期时间

    private String getAccessToken() throws IOException, JSONException {
        long currentTime = System.currentTimeMillis() / 1000; // 当前时间（单位：秒）

        if (accessToken != null && currentTime < tokenExpiryTime) {
            return accessToken; // 使用缓存的有效令牌
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/x-www-form-urlencoded"),
                "grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + SECRET_KEY
        );

        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                accessToken = jsonResponse.optString("access_token", "");
                int expiresIn = jsonResponse.optInt("expires_in", 0); // 获取令牌的有效期（单位：秒）

                if (!accessToken.isEmpty() && expiresIn > 0) {
                    tokenExpiryTime = currentTime + expiresIn; // 设置令牌过期时间
                    Log.d("WenXin", "访问令牌: " + accessToken + ", 有效期: " + expiresIn + "秒");
                } else {
                    Log.e("WenXin", "获取令牌失败: " + responseBody);
                }
                return accessToken;
            } else {
                Log.e("WenXin", "请求失败: " + response);
            }
        }

        return ""; // 在失败时返回空字符串
    }

}
