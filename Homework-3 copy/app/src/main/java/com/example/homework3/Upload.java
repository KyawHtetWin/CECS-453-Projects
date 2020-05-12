package com.example.homework3;

/*****
 *
 * This class represents a single upload of an image to the database
 *
 */

public class Upload {
    private String mName;
    private String mImageUrl;

    public Upload() {
        //empty constructor needed
    }
    public Upload(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        mName = name;
        mImageUrl = imageUrl;
    }

    // Getters and Setters
    public String getName() { return mName; }
    public void setName(String name) { mName = name; }
    public String getImageUrl() { return mImageUrl; }
    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }
}