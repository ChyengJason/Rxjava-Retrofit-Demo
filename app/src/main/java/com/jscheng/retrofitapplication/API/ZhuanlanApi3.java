package com.jscheng.retrofitapplication.API;

import com.jscheng.retrofitapplication.Model.ZhuanLanAuthor;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by dell on 2016/8/10.
 */
public interface ZhuanlanApi3 {
    @GET("/api/columns/{user} ")
    Observable<ZhuanLanAuthor> getAuthor(@Path("user") String user);
}
