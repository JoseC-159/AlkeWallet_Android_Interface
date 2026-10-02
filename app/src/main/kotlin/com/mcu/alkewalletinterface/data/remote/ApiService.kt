package com.mcu.alkewalletinterface.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

data class ExchangeRateResponse(
    val base: String,
    val rates: Map<String, Double>
)

interface ApiService {
    @GET("v4/latest/{base}")
    suspend fun getExchangeRates(@Path("base") base: String = "CLP"): ExchangeRateResponse

    companion object {
        const val BASE_URL = "https://api.exchangerate-api.com/"
    }
}
