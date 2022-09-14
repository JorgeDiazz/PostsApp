package com.zemoga.app.di.modules

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ZemogaAppModule {
    @Provides
    @Singleton
    fun providesContentResolver(context: Context): ContentResolver {
        return context.contentResolver
    }
}
