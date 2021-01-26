package mx.tecnm.tepic.ladm_u5_practica1_mapatec

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity () : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var  ubiclat = 0.0
    var ubiclon = 0.0
    var nombreLugar = ""

    //var valores = Array<>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        var extras = intent.extras
        ubiclat = extras?.getDouble("ubi-latitud")!!.toDouble()
        ubiclon = extras?.getDouble("ubi-longitud")!!.toDouble()
        nombreLugar = extras?.getString("namePlace").toString()


        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 700){
            setTitle("SE OTORGO PERMISO");
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val posicion = LatLng(ubiclat, ubiclon)
        mMap.addMarker(MarkerOptions().position(posicion).title("Aquí está: ${nombreLugar}"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(posicion))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion,20f))
        //controles del zoom
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }
}