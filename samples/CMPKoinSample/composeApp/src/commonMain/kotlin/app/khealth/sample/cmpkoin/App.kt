package app.khealth.sample.cmpkoin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

@Composable
@Preview
fun App(module: Module) {
    MaterialTheme {
        KoinApplication(
            application = {
                modules(
                    module,
                    module {
                        viewModel { MainViewModel(kHealth = get()) }
                    }
                )
            }
        ) {
            val mainViewModel = koinViewModel<MainViewModel>()
            Column(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = mainViewModel::requestPermission) {
                    Text("Request Permission")
                }
            }
        }
    }
}
