package com.example.cinema_nearby;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONException;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    public FilterSettings settings;
    public ArrayList<String> genresList = new ArrayList<>();

    public ArrayList<Cinema> cinemaList_50;
    public ArrayList<Movie> movieList;
    public ArrayList<String> cinemaInRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (getIntent().getSerializableExtra("FilterSettings") != null){
            settings = (FilterSettings) getIntent().getSerializableExtra("FilterSettings");
        } else {
            settings = new FilterSettings();
        }


        try {
            if (cinemaList_50 == null){
                cinemaList_50 = ApiCalls.get_cinemas_50(50, settings);
            }

            movieList = ApiCalls.get_movies(settings);
            ApiCalls.get_showtimes(settings,cinemaList_50,movieList);

            for (int i = 0; i < movieList.size(); i++){
                Object[] temp = movieList.get(i).getGenresDict().values().toArray();
                for (int j = 0; j < temp.length; j++){
                    if(!genresList.contains(temp[j].toString())){
                        if (!(temp[j].toString() == "null")){
                            genresList.add(temp[j].toString());
                        }
                    }
                }
            }

        } catch (Exception e) {
            return;
        }

    }

    protected void onStart (){
        super.onStart();

        try {
            cinemaInRadius = ApiCalls.get_cinemaInRadius(settings);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    protected void onRestart (){
        super.onRestart();
        Log.d("THBTHBTHB","Hups");
        settings = (FilterSettings) getIntent().getSerializableExtra("FilterSettings");
    }

    protected void onStop (){
        super.onStop();

    }

    public void startActivity (View view){
        Intent intent = new Intent(MapsActivity.this, FilterActivity.class);
        intent.putExtra("FilterSettings", settings);
        intent.putExtra("genresList", genresList);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);

        double latHigh = Double.NEGATIVE_INFINITY;
        double latLow = Double.POSITIVE_INFINITY;
        double lonHigh = Double.NEGATIVE_INFINITY;
        double lonLow = Double.POSITIVE_INFINITY;

        mMap.clear();
        int counter = 0;
        for (Cinema kino : cinemaList_50) {

            if (cinemaInRadius.contains(kino.getId())){
                StringBuilder markerString = new StringBuilder();
                for (Movie movie: movieList){
                        if (settings.getGenres().size() != 0){
                            for (int j = 0; j < settings.getGenres().size(); j++) {
                                if (movie.genresDict.containsValue(settings.getGenres().get(j))) {
                                    if (kino.showtimes.get(movie.getId()) != null && kino.showtimes.get(movie.getId()).get(kino.showtimes.get(movie.getId()).size() - 1).compareTo(settings.getStartingTime().toLocalTime().minus(1, ChronoUnit.MINUTES)) > 0) {
                                        if (movie.getTitle() != "null") {
                                            StringBuilder showtimesString = new StringBuilder();
                                            for (LocalTime l : kino.showtimes.get(movie.getId())){
                                                if (l.compareTo(settings.getStartingTime().toLocalTime().minus(1, ChronoUnit.MINUTES)) > 0){
                                                    if (showtimesString.toString().equals("")){
                                                        showtimesString.append(l.toString());
                                                    } else {
                                                        showtimesString.append(", ").append(l.toString());
                                                    }
                                                }
                                            }
                                            markerString.append(movie.getTitle()).append(" - ").append(showtimesString.toString()).append("\n");
                                            counter++;
                                        }
                                    }
                                    break;
                                }
                            }
                        } else {
                            if (kino.showtimes.get(movie.getId()) != null && kino.showtimes.get(movie.getId()).get(kino.showtimes.get(movie.getId()).size()-1).compareTo(settings.getStartingTime().toLocalTime().minus(1, ChronoUnit.MINUTES)) > 0){
                                if (movie.getTitle() != "null"){
                                    StringBuilder showtimesString = new StringBuilder();
                                    for (LocalTime l : kino.showtimes.get(movie.getId())){
                                        if (l.compareTo(settings.getStartingTime().toLocalTime().minus(1, ChronoUnit.MINUTES)) > 0){
                                            if (showtimesString.toString().equals("")){
                                                showtimesString.append(l.toString());
                                            } else {
                                                showtimesString.append(", ").append(l.toString());
                                            }
                                        }
                                    }
                                    markerString.append(movie.getTitle()).append(" - ").append(showtimesString.toString()).append("\n");
                                    counter++;
                                }
                            }
                        }
                }

                if (!markerString.toString().equals("")){
                    Log.d("WRITINGMAP", "mapsd");
                    Marker bsp;
                    bsp = mMap.addMarker(new MarkerOptions().position(kino.getkoord()).title(kino.getName()).snippet(markerString.toString()));
                    bsp.setTag(kino);
                    bsp.showInfoWindow();
                    if (kino.getLongitute() > lonHigh) {
                        lonHigh = kino.getLongitute();
                    }
                    if (kino.getLongitute() < lonLow) {
                        lonLow = kino.getLongitute();
                    }
                    if (kino.getLatitute() > latHigh) {
                        latHigh = kino.getLatitute();
                    }
                    if (kino.getLatitute() < latLow) {
                        latLow = kino.getLatitute();
                    }
                }

            }
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                Context mContext = MapsActivity.this;
                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);
                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                info.addView(title);
                info.addView(snippet);
                return info;
            }
        });
        if (counter == 0){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cinemaList_50.get(0).getkoord()));
            Toast.makeText(this, "No Movies Found",Toast.LENGTH_SHORT).show();
        } else{
            LatLngBounds viewfield = new LatLngBounds(
                    new LatLng(latLow, lonLow),
                    new LatLng(latHigh, lonHigh)
            );

            Log.d("THBTHBTHB4234",viewfield.toString());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(viewfield.getCenter(), 10));
        }

    }


    @Override
    public void onInfoWindowClick(Marker bsp) {

        Cinema a = (Cinema) bsp.getTag();
        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(a.getWebsite()));
        startActivity(browserIntent);

        Toast.makeText(this, "Opening Browser",Toast.LENGTH_SHORT).show();
    }
}