package app.khealth.sample.cmpkoin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.khealth.KHealth
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
    private val kHealth = KHealth(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        kHealth.initialise()
        setContent {
            App(
                module {
                    single { kHealth }
                }
            )
        }
    }
}
