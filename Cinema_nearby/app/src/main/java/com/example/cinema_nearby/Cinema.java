package com.example.cinema_nearby;

import com.google.android.gms.maps.model.LatLng;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Cinema {

    public final double latitute;
    public final double longitute;
    public final LatLng koord;
    public final String name;
    public final String website;
    public final String id;


    public Map<String, ArrayList<LocalTime>> showtimes = new HashMap<String, ArrayList<LocalTime>>();

    public Cinema(double latitute, double longitute, String name, String website, String id){
        this.latitute = latitute;
        this.longitute = longitute;
        this.website = website;
        this.id = id;
        this.koord = new LatLng(latitute,longitute);
        this.name = name;
    }

    public void setSingleShowtime (String key, LocalTime value){
        if (showtimes.keySet().contains(key)){
            ArrayList<LocalTime> temp = showtimes.get(key);
            temp.add(value);
            showtimes.put(key, temp);
        } else {
            showtimes.put(key,new ArrayList<LocalTime>(Arrays.asList(value)));
        }
    }

    public Map<String, ArrayList<LocalTime>> getShowtimes() {
        return showtimes;
    }

    public double getLatitute (){
        return this.latitute;
    }

    public double getLongitute (){
        return this.longitute;
    }

    public LatLng getkoord (){
        return this.koord;
    }

    public String getName(){
        return this.name;
    }

    public String getWebsite() {
        return this.website;
    }

    public String getId() {
        return id;
    }
}
