package com.hfad.homework2;

public class Car {

    private String make;
    private String model;
    private String name;
    private String location;
    private int imageResourceId;
    private double price;
    private String lastUpdated;


    // Array of cars
    public static final Car[] cars = {
        new Car("BMake", "B999", "BMW", "Cerritos", R.drawable.bmw, 1000,
                "Last Updated on May 1 2020"),

        new Car("FMake", "F777", "Ferrari", "Long Beach", R.drawable.ferrari,
                4000, "Last Updated on Jan 1 2020"),

        new Car("LMake", "L007", "Lamborghini", "Santa Ana", R.drawable.lambo,
                5000, "Last Updated on Feb 30 2020")
    };


    // Constructor
    public Car(String make, String model, String name, String location,
               int imageResourceId, double price, String lastUpdated) {
        this.make = make;
        this.model = model;
        this.name = name;
        this.location = location;
        this.imageResourceId = imageResourceId;
        this.price = price;
        this.lastUpdated = lastUpdated;
    }

    // Getters
    public String getMake() { return make; }
    public String getModel() { return model; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getImageResourceId() { return imageResourceId; }
    public double getPrice() { return price; }
    public String getLastUpdated() { return lastUpdated; }
}
