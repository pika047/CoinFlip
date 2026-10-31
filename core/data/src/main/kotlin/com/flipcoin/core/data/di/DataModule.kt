package com.flipcoin.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.flipcoin.core.data.preferences.DataStoreUserPreferencesRepository
import com.flipcoin.core.data.topic.AndroidTopicAssetSource
import com.flipcoin.core.data.topic.AssetTopicRepository
import com.flipcoin.core.data.topic.IoDispatcher
import com.flipcoin.core.data.topic.TopicAssetSource
import com.flipcoin.core.domain.repository.TopicRepository
import com.flipcoin.core.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataBindingsModule {
    @Binds
    abstract fun bindTopicRepository(impl: AssetTopicRepository): TopicRepository

    @Binds
    abstract fun bindUserPreferencesRepository(impl: DataStoreUserPreferencesRepository): UserPreferencesRepository

    @Binds
    abstract fun bindTopicAssetSource(impl: AndroidTopicAssetSource): TopicAssetSource
}

@Module
@InstallIn(SingletonComponent::class)
internal object DataProvidersModule {
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile("user_preferences") }

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
