package com.ishan.kbc.di

import android.content.Context
import androidx.room.Room
import com.ishan.kbc.data.local.KbcDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KbcDatabase =
        Room.databaseBuilder(context, KbcDatabase::class.java, KbcDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
}
