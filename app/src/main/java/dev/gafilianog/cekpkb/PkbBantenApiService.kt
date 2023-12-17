package dev.gafilianog.cekpkb

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

private const val BASE_URL = "https://samsatsleman.jogjaprov.go.id"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface PkbBantenService {

    @FormUrlEncoded
    @POST("cek/pages/getpajak")
    suspend fun getPkbData(
        @Field("nomer") nomor: String,
        @Field("kode_belakang") kodeBelakang: String
    ): Response<String>
}

object PkbBantenApi {

    val retrofitService: PkbBantenService by lazy {
        retrofit.create(PkbBantenService::class.java)
    }
}