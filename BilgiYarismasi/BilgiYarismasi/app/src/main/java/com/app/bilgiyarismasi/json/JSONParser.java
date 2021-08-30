package com.app.bilgiyarismasi.json;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JSONParser {

    public static GsonBuilder gsonBuilder = new GsonBuilder();
    public static Gson gson = gsonBuilder.create();
    public static OkHttpClient client = new OkHttpClient();

    public static <T> T getDataFromService(String url, Class<T> classOfT) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return gson.fromJson(response.body().string(), classOfT);
    }

    /*public static String getDataCount(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }*/

    public static JSONArray getDataCount(String _url) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(_url)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONArray(response.body().string());
        } catch (@NonNull IOException | JSONException e) {
            Log.e("TAG", "" + e.getLocalizedMessage());
        }
        return null;
    }

}
