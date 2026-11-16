package com.flipcoin.di

import com.flipcoin.core.domain.RandomSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
object RandomModule {
    @Provides
    fun provideRandomSource(): RandomSource = RandomSource { Random.nextBoolean() }
}
