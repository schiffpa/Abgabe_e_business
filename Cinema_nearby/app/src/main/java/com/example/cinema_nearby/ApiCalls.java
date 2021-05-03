package com.example.cinema_nearby;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import android.os.Build;
import androidx.annotation.RequiresApi;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.*;

public class ApiCalls {

    static String resp = "";
    static String resp2 = "";
    static String resp3 = "";
    static String resp4 = "";

    private final static String API_KEY = "censored_API_KEY";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Cinema> get_cinemas_50(int radius, FilterSettings settings) throws IOException, CustomException, JSONException, InterruptedException {

        OkHttpClient client = new OkHttpClient();
        ArrayList<Cinema> cinemaList = new ArrayList<>();

        //Create request
        Request request = new Request.Builder()
                .url("https://api.internationalshowtimes.com/v4/cinemas/?location=" + settings.getStartingLocString() + "&distance=" + radius)
                .addHeader("X-API-KEY", API_KEY)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    countDownLatch.countDown();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()){
                        final String myResponse = response.body().string();
                        resp = myResponse;
                        countDownLatch.countDown();
                    }
                }
            });

        countDownLatch.await();

        JSONObject answer = new JSONObject(resp);
        JSONArray arr = answer.getJSONArray("cinemas");

        for (int i = 0; i < arr.length(); i++){
            cinemaList.add(new Cinema(
                    arr.getJSONObject(i).getJSONObject("location").getDouble("lat"),
                    arr.getJSONObject(i).getJSONObject("location").getDouble("lon"),
                    arr.getJSONObject(i).getString("name"),
                    arr.getJSONObject(i).getString("website"),
                    arr.getJSONObject(i).getString("id")));
        }

        return cinemaList;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Movie> get_movies(FilterSettings settings) throws IOException, CustomException, JSONException, InterruptedException {

        OkHttpClient client = new OkHttpClient();
        ArrayList<Movie> movieList = new ArrayList<>();

        //Create request
        Request request = new Request.Builder()
                .url("https://api.internationalshowtimes.com/v4/movies?location=" + settings.getStartingLocString() + "&distance=50&time_from=" + settings.getStartingTimeISO() + "+01:00&time_to=" + settings.getFinalTimeISO() + "+01:00&fields=id,title,genres")
                .addHeader("X-API-KEY", API_KEY)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    resp2 = myResponse;
                    countDownLatch.countDown();
                }
            }
        });

        countDownLatch.await();

        JSONObject answer = new JSONObject(resp2);
        JSONArray arr = answer.getJSONArray("movies");

        for (int i = 0; i < arr.length(); i++){

            movieList.add(new Movie(
                    arr.getJSONObject(i).getString("id"),
                    arr.getJSONObject(i).getString("title")
            ));

            JSONArray arr2 = arr.getJSONObject(i).getJSONArray("genres");
            for (int j = 0; j < arr2.length();j++){
                movieList.get(i).setSingleGenreDict(
                        arr2.getJSONObject(j).getString("id"),
                        arr2.getJSONObject(j).getString("name")
                );
            }

        }

        return movieList;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void get_showtimes(FilterSettings settings, ArrayList<Cinema> cinemas, ArrayList<Movie> movies) throws IOException, CustomException, JSONException, InterruptedException {

        OkHttpClient client = new OkHttpClient();
        ArrayList<Movie> movieList = new ArrayList<>();

        //Create request
        Request request = new Request.Builder()
                .url("https://api.internationalshowtimes.com/v4/showtimes?location=" + settings.getStartingLocString() + "&distance=50&time_from=" + settings.getStartingTimeISO() + "+01:00&time_to=" + settings.getFinalTimeISO() + "+01:00&fields=cinema_id,movie_id,start_at")
                .addHeader("X-API-KEY", API_KEY)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    resp3 = myResponse;
                    countDownLatch.countDown();
                }
            }
        });

        countDownLatch.await();

        JSONObject answer = new JSONObject(resp3);
        JSONArray arr = answer.getJSONArray("showtimes");

        for (Cinema cinema: cinemas){
            for (int i = 0; i < arr.length(); i++){
                if (!arr.getJSONObject(i).getString("cinema_id").equals("null") && !arr.getJSONObject(i).getString("movie_id").equals("null")){
                    if (cinema.getId().equals(arr.getJSONObject(i).getString("cinema_id"))){
                        String tempTime = arr.getJSONObject(i).getString("start_at").split("\\+")[0];
                        cinema.setSingleShowtime(arr.getJSONObject(i).getString("movie_id"),LocalDateTime.parse(tempTime).toLocalTime());
                    }
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<String> get_cinemaInRadius(FilterSettings settings) throws InterruptedException, JSONException {
        OkHttpClient client = new OkHttpClient();
        ArrayList<String> cinemaList = new ArrayList<>();

        //Create request
        Request request = new Request.Builder()
                .url("https://api.internationalshowtimes.com/v4/cinemas/?location=" + settings.getStartingLocString() + "&distance=" + settings.getRadius() + "&fields=id")
                .addHeader("X-API-KEY", API_KEY)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();
                    resp4 = myResponse;
                    countDownLatch.countDown();
                }
            }
        });

        countDownLatch.await();

        JSONObject answer = new JSONObject(resp4);
        JSONArray arr = answer.getJSONArray("cinemas");

        for (int i = 0; i < arr.length(); i++){
            cinemaList.add(arr.getJSONObject(i).getString("id"));
        }

        return cinemaList;
    }

}
