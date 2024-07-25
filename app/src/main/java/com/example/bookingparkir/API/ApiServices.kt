package com.example.bookingparkir.API

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/users")
    fun getUserById(@Body request: UserRequest): Call<UserResponse>

    @GET("/tempat_parkir")
    fun getTempatParkir(): Call<List<TempatParkir>>

    @GET("/tempat_parkir/{id}")
    fun getDetailTempatParkir(@Path("id") id: Int): Call<TempatParkirDetail>

    @POST("/reservasi")
    fun bookReservation(@Body reservasiData: Reservasi): Call<CheckResponse>

    @GET("/getReservasiStatus")
    fun getStatusReservasi(@Query("userId") id: String?): Call<ReservasiResponse>

    @PUT("/updateUser")
    fun updateUser(@Body updateUserRequest: UpdateUserRequest): Call<CheckResponse>

    @PUT("/changePassword")
    fun changePassword(@Body request: ChangePasswordRequest): Call<CheckResponse>

    @POST("/signup")
    fun signUp(@Body signUpRequest: SignUpRequest): Call<CheckResponse>

    @GET("/ongoing-reservations/{user_id}")
    fun getOngoingReservations(@Path("user_id") userId: String?): Call<OngoingReservationsResponse>

    @GET("/finished-reservations/{user_id}")
    fun getFinishedReservations(@Path("user_id") userId: String?): Call<FinishedReservationsResponse>

    @PUT("/cancel_reservasi/{user_id}")
    fun cancelReservasi(@Path("user_id") userId: String?): Call<CancelReservasiResponse>

    @POST("/payment")
    fun payment(@Body paymentRequest: PaymentRequest): Call<CheckResponse>

    @POST("/topUp")
    fun topUp(@Body request: TopupRequest): Call<TopupResponse>

    @GET("/getExpiredTime/{user_id}")
    fun getExpiredTime(@Path("user_id") userId: String?): Call<ExpiredTimeResponse>
}