package com.delivery.compose.android.di

import com.delivery.compose.android.data.repository.DeliveryRepositoryImpl
import com.delivery.compose.android.domain.repository.DeliveryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDeliveryRepository(deliveryRepositoryImpl: DeliveryRepositoryImpl): DeliveryRepository
} 