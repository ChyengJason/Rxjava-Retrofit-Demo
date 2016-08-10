package com.jscheng.retrofitapplication.API;

import com.jscheng.retrofitapplication.Model.ZhuanLanAuthor;

import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by dell on 2016/8/10.
 */
public interface ZhuanlanApi2 {
    @GET("/api/columns/{user} ")
    public void getAuthor(@Path("user") String user, Callback<ZhuanLanAuthor> call);
}