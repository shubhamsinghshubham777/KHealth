package app.khealth.sample.cmpkoin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khealth.KHPermission
import com.khealth.KHealth
import kotlinx.coroutines.launch

class MainViewModel(private val kHealth: KHealth) : ViewModel() {
    fun requestPermission() {
        viewModelScope.launch {
            val response = kHealth.requestPermissions(KHPermission.HeartRate(read = true, write = true))
            println("Response is: $response")
        }
    }
}
