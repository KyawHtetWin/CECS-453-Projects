// CECS 453 Mobile Development
// Homework 2
// Due date: Feb 23, 2020

// Team members:
// Ben Do
// Kyaw Htet Win

package com.example.homework2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

// interface for API calls
public interface ApiManager {

    @GET("carmakes")
    Call<List<Vehicle.Make>> getMakes();

    @GET("carmodelmakes/{id}")
    Call<List<Vehicle.Model>> getModels(@Path("id") int makeId);

    @GET("cars/{make_id}/{model_id}/{zipcode}")
    Call<Vehicle.ListingResponse> getListings(@Path("make_id") int makeId,
                                          @Path("model_id") int modelId,
                                          @Path("zipcode") int zipcode);


    //https://thawing-beach-68207.herokuapp.com/cars/10/20/92603



}
