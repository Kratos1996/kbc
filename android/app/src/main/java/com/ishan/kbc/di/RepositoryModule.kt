package com.ishan.kbc.di

import com.ishan.kbc.data.repository.AchievementRepositoryImpl
import com.ishan.kbc.data.repository.AuthRepositoryImpl
import com.ishan.kbc.data.repository.DailyRepositoryImpl
import com.ishan.kbc.data.repository.FffRepositoryImpl
import com.ishan.kbc.data.repository.LeaderboardRepositoryImpl
import com.ishan.kbc.data.repository.MatchHistoryRepositoryImpl
import com.ishan.kbc.data.repository.MultiplayerRepositoryImpl
import com.ishan.kbc.data.repository.ProfileRepositoryImpl
import com.ishan.kbc.data.repository.QuestionRepositoryImpl
import com.ishan.kbc.data.repository.ShopRepositoryImpl
import com.ishan.kbc.domain.repository.AchievementRepository
import com.ishan.kbc.domain.repository.AuthRepository
import com.ishan.kbc.domain.repository.DailyRepository
import com.ishan.kbc.domain.repository.FffRepository
import com.ishan.kbc.domain.repository.LeaderboardRepository
import com.ishan.kbc.domain.repository.MatchHistoryRepository
import com.ishan.kbc.domain.repository.MultiplayerRepository
import com.ishan.kbc.domain.repository.ProfileRepository
import com.ishan.kbc.domain.repository.QuestionRepository
import com.ishan.kbc.domain.repository.ShopRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository

    @Binds @Singleton
    abstract fun bindDailyRepository(impl: DailyRepositoryImpl): DailyRepository

    @Binds @Singleton
    abstract fun bindLeaderboardRepository(impl: LeaderboardRepositoryImpl): LeaderboardRepository

    @Binds @Singleton
    abstract fun bindMultiplayerRepository(impl: MultiplayerRepositoryImpl): MultiplayerRepository

    @Binds @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds @Singleton
    abstract fun bindShopRepository(impl: ShopRepositoryImpl): ShopRepository

    @Binds @Singleton
    abstract fun bindFffRepository(impl: FffRepositoryImpl): FffRepository

    @Binds @Singleton
    abstract fun bindAchievementRepository(impl: AchievementRepositoryImpl): AchievementRepository

    @Binds @Singleton
    abstract fun bindMatchHistoryRepository(impl: MatchHistoryRepositoryImpl): MatchHistoryRepository
}
