package com.example.cinema_nearby;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FilterSettings implements Serializable {

    int radius = 20;
    LocalDateTime startingTime = LocalDateTime.now();
    ArrayList<String> genres = new ArrayList<>();

    public String getStartingLocString (){
        // Lat,Lon
        return "59.32833316427333" + "," + "18.069991503977562";
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public LocalDateTime getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(LocalDateTime startingTime) {
        this.startingTime = startingTime;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public String getStartingTimeISO(){
        return startingTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public String getFinalTimeISO(){
        LocalDateTime finalTime = LocalDateTime.of(LocalDate.now().plus(1, ChronoUnit.DAYS), LocalTime.NOON.minus(8, ChronoUnit.HOURS));
        return finalTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
