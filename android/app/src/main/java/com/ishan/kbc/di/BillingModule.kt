package com.ishan.kbc.di

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PendingPurchasesParams
import com.ishan.kbc.data.billing.BillingManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides @Singleton
    fun provideBillingManager(@ApplicationContext context: Context): BillingManager {
        val manager = BillingManager()
        val client = BillingClient.newBuilder(context)
            .setListener(manager.purchasesUpdatedListener())
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .build()
        manager.connect(client)
        return manager
    }
}
