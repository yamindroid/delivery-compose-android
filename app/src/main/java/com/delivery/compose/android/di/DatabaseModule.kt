package com.delivery.compose.android.di

import android.content.Context
import androidx.room.Room
import com.delivery.compose.android.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "delivery_db").build()

    @Provides
    @Singleton
    fun provideDeliveryDao(db: AppDatabase) = db.deliveryDao()
}