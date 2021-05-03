package com.example.cinema_nearby;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

public class FilterActivity extends AppCompatActivity {

    public FilterSettings settings;
    public ArrayList<String> genresListSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        settings = (FilterSettings) getIntent().getSerializableExtra("FilterSettings");
        genresListSet = (ArrayList<String>) getIntent().getSerializableExtra("genresList");

        Calendar c = Calendar.getInstance();

        TextView timeTv = findViewById(R.id.timeTextView);
        timeTv.setText(settings.getStartingTime().getHour() + " : " + settings.getStartingTime().getMinute());
        SeekBar radiusSb = findViewById(R.id.radiusSeekBar);
        radiusSb.setProgress(settings.getRadius());
        TextView progressTv = findViewById(R.id.progressTextView);
        progressTv.setText(settings.radius + " km");

        TextView genresTv = findViewById(R.id.genresTextView);

        if (settings.getGenres().size() != 0){
            genresTv.setText(settings.genres.toString());
        }

        String[] genreArray = new String[genresListSet.size()];
        boolean[] selectedGenre = new boolean[genresListSet.size()];
        ArrayList<Integer> genresList = new ArrayList<>();

        for (int l = 0; l < genresListSet.size(); l++){
            genreArray[l] = genresListSet.get(l).toString();
            if (settings.getGenres().contains(genresListSet.get(l))){
                selectedGenre[l] = true;
                genresList.add(l);
            }
        }

        ArrayList<String> resultGen = new ArrayList<>();

        genresTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initilize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(FilterActivity.this);

                //Set title
                builder.setTitle("Select Genre");

                //Set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(genreArray, selectedGenre, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        //Check condition
                        if (isChecked){
                            //When checkbox selected
                            //Add position in genre list
                            genresList.add(which);
                        } else {
                            //When checkbox unselected
                            //Remove position from genre list

                            genresList.remove(which);
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        //Use for loop
                        for (int j = 0; j<genresList.size(); j++){
                            //Concat array value
                            stringBuilder.append(genreArray[genresList.get(j)]);
                            resultGen.add(genreArray[genresList.get(j)]);
                            //Check conditon
                            if (j != genresList.size()-1){
                                //When j value not equal to genre list size -1
                                //Add comma
                                stringBuilder.append(", ");
                            }
                        }
                        //Set text on text view

                        genresTv.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dismiss dialog
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Use for loop
                        for (int j = 0; j < selectedGenre.length; j++){
                            //Remove all selection
                            selectedGenre[j] = false;
                            //Clear genres list
                            genresList.clear();
                            //Clear text view value
                            genresTv.setText("");
                        }
                    }
                });
                //Show dialog
                builder.show();
            }
        });

        radiusSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0){
                    progressTv.setText("1 km");
                } else {
                    progressTv.setText(progress + " km");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final Button button = findViewById(R.id.approveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String test = (String) timeTv.getText();
                String[] startTimeArray = test.split(":");
                LocalDateTime startTimeLocalDateTime = LocalDateTime.of(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),Integer.parseInt(startTimeArray[0].trim()),Integer.parseInt(startTimeArray[1].trim()));
                settings.setStartingTime(startTimeLocalDateTime);
                settings.setGenres(resultGen);

                if (radiusSb.getProgress() == 0) {
                    settings.setRadius(1);
                } else {
                    settings.setRadius(radiusSb.getProgress());
                }

                // TODO: test if time is past

                Intent intent = new Intent(FilterActivity.this,  MapsActivity.class);
                intent.putExtra("FilterSettings", settings);
                startActivity(intent);

            }
        });

    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

}