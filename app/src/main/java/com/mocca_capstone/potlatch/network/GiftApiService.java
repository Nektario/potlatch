package com.mocca_capstone.potlatch.network;

import com.mocca_capstone.potlatch.models.Gift;
import com.mocca_capstone.potlatch.models.GiftChainListResponse;
import com.mocca_capstone.potlatch.models.RegistrationUser;
import com.mocca_capstone.potlatch.models.UserAccount;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by nektario on 10/13/2014.
 */
public interface GiftApiService {

    @GET("/giftChains")
    void getGiftChainList(@Query("nextPageToken") int nextPageToken,
                          @Query("limit") int limit,
                          @Query("sort") String sort,
                          Callback<GiftChainListResponse> callback);

    @GET("/gifts")
    void getGiftsNewerThanGiftId(@Query("nextPageToken") int nextPageToken,
                                 @Query("limit") int limit,
                                 @Query("sort") String sort,
                                 @Query("greaterThanId") long giftId,
                                 Callback<GiftChainListResponse> callback);

    @GET("/gifts/newest")
    void getNewestGift(Callback<Gift> callback);

    @Multipart
    @POST("/gifts")
    Gift uploadNewGift(@Part("gift") Gift gift, @Part("file") TypedFile file);

    @POST("/users")
    void registerUser(@Body RegistrationUser user, Callback<UserAccount> callback);

    @FormUrlEncoded
    @POST("/gifts/{id}/touch")
    Response touch(@Path("id") long touchableId, @Field("direction") int direction);

    @FormUrlEncoded
    @POST("/gifts/{id}/flag")
    Response flag(@Path("id") long flaggableId, @Field("direction") int direction);
}
