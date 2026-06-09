package com.ishan.kbc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ishan.kbc.data.billing.BillingManager
import com.ishan.kbc.ui.KbcRoot
import com.ishan.kbc.ui.theme.KbcTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        billingManager.currentActivity = this
        setContent {
            KbcTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    KbcRoot()
                }
            }
        }
    }

    override fun onDestroy() {
        billingManager.currentActivity = null
        super.onDestroy()
    }
}
