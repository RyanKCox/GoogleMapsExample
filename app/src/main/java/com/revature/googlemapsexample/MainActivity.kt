package com.revature.googlemapsexample

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.revature.googlemapsexample.ui.theme.GoogleMapsExampleTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.*


val omaha=LatLng(41.2565369,-96.0045412)
val tampa=LatLng(27.9947147,-82.5943685)
val chicago=LatLng(41.8339042,-88.0121574)

var latitude=0.0
var longitude=0.0

class MainActivity : ComponentActivity(), LocationListener {

    private lateinit var locationManager:LocationManager

    private val locationPermissionCode=2



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isMapLoaded by remember{mutableStateOf(false)}
            var cameraPositionState= rememberCameraPositionState{

                position= CameraPosition.fromLatLngZoom(tampa,11f)
            }
            GoogleMapsExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val context = this
                    Box(modifier = Modifier.fillMaxSize())
                    {

                        GoogleMapView(
                            modifier=Modifier.matchParentSize(),
                            cameraPositionState =cameraPositionState,
                            onMapLoaded={isMapLoaded=true},

                            )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Spacer(Modifier.fillMaxHeight(.8f))

                            Button(onClick = {

                                val viewModel =
                                    ViewModelProvider(context).get(MapViewModel::class.java)

                                getLocation()
                                viewModel.userPos = LatLng(latitude, longitude)
                                viewModel.bUpdate = true
                            }) {

                            Text(text = "Get My Location")
                        }

//                        Spacer(Modifier.size(15.dp))
//
//                            Button(onClick = {
//
//
//                            }) {
//
//                                Text(text = "Redraw")
//                            }
                        }



                    }

                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {

        latitude=location.latitude
        longitude=location.longitude

        Log.i("LatLong","Latitude ${location.latitude}, Longitude ${location.longitude}")

        Toast.makeText(this,"Latitude ${location.latitude}, Longitude ${location.longitude}",Toast.LENGTH_LONG).show()

    }



    private fun getLocation()   {

        locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),locationPermissionCode)
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5f, this)
    }
}


@Composable
fun GoogleMapView(
    modifier:Modifier,
    cameraPositionState: CameraPositionState,
    onMapLoaded:()->Unit,


    )
{

    val context= LocalContext.current
    var uiSettings by remember{mutableStateOf(MapUiSettings(compassEnabled = true))}
    var mapProperties by remember{ mutableStateOf(MapProperties(mapType= MapType.NORMAL))}
    val circleCenter by remember{mutableStateOf(tampa)}
    val viewModel =
        ViewModelProvider(context as MainActivity).get(MapViewModel::class.java)

    var bUpdate by remember { mutableStateOf(viewModel.bUpdate) }

    GoogleMap(

        modifier=modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapLoaded =onMapLoaded,
        onPOIClick = {
            Toast.makeText(context,"Map Clicked:${it.name}",Toast.LENGTH_LONG).show()
        }
    )
    {

        if (bUpdate && viewModel.userPos!= null){
            Marker(
                position = viewModel.userPos!!,
                title = "Your Location",
                snippet = "Lat: ${viewModel.userPos!!.latitude} \nLong: ${viewModel.userPos!!.longitude}"
            )
            viewModel.bUpdate = false
        }

        val currentLocation= com.google.maps.android.compose.Marker(position =LatLng(latitude,
            longitude) )

        Log.d("Lat Values","$latitude------$longitude")

        val current=Marker(position=LatLng(latitude,
            longitude))

        val markerClick:(Marker)->Boolean={

            Log.d("marker clicked","${it.title} was clicked")

            cameraPositionState.position.let{ projection->
                Log.d("marker clicked","The Current position is $projection")
            }

            false
        }

        Circle(
            center = circleCenter,
            fillColor=MaterialTheme.colors.secondary,
            strokeColor = MaterialTheme.colors.secondaryVariant,
            radius = 1000.0

        )





    }



}

@Composable
private fun DebugView(

    cameraPositionState: CameraPositionState,
    marketState:MarkerDragState
)
{

}
