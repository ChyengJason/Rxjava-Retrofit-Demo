package com.jscheng.retrofitapplication.API;

import com.jscheng.retrofitapplication.Model.ZhuanLanAuthor;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by dell on 2016/8/10.
 */
public interface ZhuanLanApi {
    @GET("/api/columns/{user} ")
    Call<ZhuanLanAuthor> getAuthor(@Path("user") String user);
}