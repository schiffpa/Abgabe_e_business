package com.example.cinema_nearby;

import java.util.HashMap;
import java.util.Map;

public class Movie {

    public Movie(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String id;
    public String title;
    public Map<String, String> genresDict = new HashMap<String, String>();

    public Map<String, String> getGenresDict() {
        return genresDict;
    }

    public void setGenresDict(Map<String, String> genresDict) {
        this.genresDict = genresDict;
    }

    public void setSingleGenreDict(String key, String value){
        this.genresDict.put(key, value);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
