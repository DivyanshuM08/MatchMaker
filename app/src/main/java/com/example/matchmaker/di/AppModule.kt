package com.example.matchmaker.di

import android.app.Application
import com.example.matchmaker.data.api.Location
import com.example.matchmaker.data.api.LocationDeserializer
import com.example.matchmaker.data.api.RandomUserApi
import com.example.matchmaker.data.db.AppDatabase
import com.example.matchmaker.data.db.MatchProfileDao
import com.example.matchmaker.data.repository.MatchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import androidx.room.Room
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.google.gson.Gson
import com.google.gson.GsonBuilder

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://randomuser.me/"

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "matchmate.db").build()
    }

    @Provides
    @Singleton
    fun provideMatchProfileDao(database: AppDatabase): MatchProfileDao {
        return database.matchProfileDao()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Location::class.java, LocationDeserializer())
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideRandomUserApi(retrofit: Retrofit): RandomUserApi {
        return retrofit.create(RandomUserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMatchRepository(dao: MatchProfileDao, api: RandomUserApi): MatchRepository {
        return MatchRepository(dao, api)
    }
}
