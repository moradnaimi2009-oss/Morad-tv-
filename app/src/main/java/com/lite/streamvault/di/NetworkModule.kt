package com.lite.streamvault.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lite.streamvault.data.remote.StreamVaultApi
import com.lite.streamvault.data.repository.StreamVaultRepository
import com.lite.streamvault.data.repository.StreamVaultRepositoryImpl
import com.lite.streamvault.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("User-Agent", Constants.USER_AGENT)
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(req)
            }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL + Constants.API_PATH)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): StreamVaultApi = retrofit.create(StreamVaultApi::class.java)

    @Provides
    @Singleton
    fun provideRepository(api: StreamVaultApi): StreamVaultRepository =
        StreamVaultRepositoryImpl(api)
}
