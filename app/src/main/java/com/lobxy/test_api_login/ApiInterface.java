package com.lobxy.test_api_login;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("login")
    Call<Message> getData(@Query("uname") String username, @Query("password") String password);

}
