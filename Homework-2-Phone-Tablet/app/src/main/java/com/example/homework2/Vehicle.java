package com.example.homework2;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle {


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

    static class ListingResponse {

        @SerializedName("lists")
        private ArrayList<Vehicle.Listing> listings;

        public ArrayList<Listing> getListings() {
            return listings;
        }
    }

}
