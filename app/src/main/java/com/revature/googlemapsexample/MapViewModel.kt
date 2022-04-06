package com.revature.googlemapsexample

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel:ViewModel() {

    var userPos: LatLng? by mutableStateOf(null)
    var bUpdate:Boolean by mutableStateOf(false)
}