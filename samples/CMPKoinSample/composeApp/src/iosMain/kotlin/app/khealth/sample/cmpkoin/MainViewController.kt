package app.khealth.sample.cmpkoin

import androidx.compose.ui.window.ComposeUIViewController
import com.khealth.KHealth
import org.koin.dsl.module

fun MainViewController() = ComposeUIViewController {
    App(
        module {
            single { KHealth() }
        }
    )
}
