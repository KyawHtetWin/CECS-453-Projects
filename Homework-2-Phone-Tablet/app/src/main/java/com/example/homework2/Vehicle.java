package com.example.homework2;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vehicle {

    // Vehicle has make
    static class Make {
        private String vehicle_make;
        private int id;

        public String getVehicle_make() {
            return vehicle_make;
        }

        public int getId() {
            return id;
        }
    }

    // Vehicle has model
    static class Model {
        private int id;
        private String model;

        public int getId() {
            return id;
        }

        public String getModel() {
            return model;
        }

    }

    // The Listing of a particular Vehicle information
    static class Listing implements Serializable{
        private String vehicle_make;
        private String model;
        private String image_url;
        private String vin_number;
        private String veh_description;

        private double price;
        private int mileage;

        private String created_at;


        public String getVehicle_make() {
            return vehicle_make;
        }

        public String getVeh_description() {
            return veh_description;
        }

        public int getMileage() {
            return mileage;
        }

        public String getModel() {
            return model;
        }

        public String getImage_url() {
            return image_url;
        }

        public String getVin_number() {
            return vin_number;
        }

        public double getPrice() {
            return price;
        }

        public String getDate() {
            return created_at;
        }
    }

    // Will save the api return with an array of vehicle listing
    static class ListingResponse {

        @SerializedName("lists")
        private ArrayList<Vehicle.Listing> listings;

        public ArrayList<Listing> getListings() {
            return listings;
        }
    }

    // extract vehicle year from vehicle description
    public static String getYear(String text) {
        String year = "";

        Pattern pattern = Pattern.compile("[1][9][0-9]{2}|[2][0][0|1|2][0-9]");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            year = matcher.group();
        }

        return year;
    }

    // extract location from vehicle description
    public static String getLocation(String text) {
        String location = "";

        Pattern pattern = Pattern.compile("([A-Za-z]+(?: [A-Za-z]+)*),? ([A-Za-z]{2})");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            location = matcher.group();
        }

        return location;
    }

}
