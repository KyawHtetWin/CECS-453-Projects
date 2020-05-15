package com.example.runningmate;


import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDate;

@IgnoreExtraProperties
public class RunData {

    public String elapsedTime;
    public String pace;
    public String distance;
    public String dateTime;
    public String imageUrl;

    public RunData(){
    }

    public RunData(String elapsedTime, String pace, String distance, String dateTime, String imageUrl) {
        this.elapsedTime = elapsedTime;
        this.pace = pace;
        this.distance = distance;
        this.dateTime = dateTime;
        this.imageUrl = imageUrl;
    }
}
