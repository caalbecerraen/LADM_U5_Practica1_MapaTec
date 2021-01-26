package mx.tecnm.tepic.ladm_u5_practica1_mapatec

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.google.android.gms.location.LocationServices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.type.LatLng
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    /*--------- OBTENCION BD REMOTA-------*/
    var baseRemota = FirebaseFirestore.getInstance()
    /*-------------------- VARIABLES OBTECION DE DATA-------*/
    var listaLugares = ArrayList<String>()
    var posicion = ArrayList<Data>()
    var seleccion = ""
    var coordLat = 0.0
    var coordLon = 0.0
    var namePlace = ""
    /*-------------- LOCALIZACION-------*/
    lateinit var locacion : LocationManager




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*-------------------------- VERIFICAR EL PERMISO DE UBICACION-------------------*/
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }

        /*------------------------------------- OBTENER UNA LISTA DE LOS LUGARES DISPONIBLES / VERIFICAR POSICION */
        baseRemota.collection("tecnologico")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    textView.setText("ERROR: " + firebaseFirestoreException.message)
                    return@addSnapshotListener
                }
                var resultado = ""
                posicion.clear()
                for (document in querySnapshot!!){
                    var data = Data()
                    data.nombre = document.getString("nombre").toString()
                    data.posicion1 = document.getGeoPoint("posicion1")!!
                    data.posicion2 = document.getGeoPoint("posicion2")!!
                    listaLugares.add(document.getString("nombre").toString())
                    resultado += data.toString() + "\n\n"
                    posicion.add(data)
                }
                /*--------------------- LLENAR EL SPINNER DE LUGARES-----------------------------*/
                var adaptador = ArrayAdapter(this,android.R.layout.simple_spinner_item,listaLugares)
                spinner.adapter = adaptador
                /*--------------------------------- ITEM SELECCIONADO DEL SPINNER----------------------------------------------------------*/
                spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        seleccion = listaLugares[position] //obtener el item seleccionado del spinner
                        /*---------------- BUSCAR LAS COORDENADAS DEL ELEMENTO SELECCIONADO----------------*/
                        for (item in posicion){
                            if (item.nombre == seleccion){
                                coordLat = item.posicion1.latitude
                                coordLon = item.posicion1.longitude
                                namePlace = item.nombre
                            }
                        }
                    }
                }
                /*------------------------------------------------------------------------------------------------------------------------------------*/
            }//snap

        /*----------------------------- BUSCAR UBICACION (MANDAR LLAMAR MapsActivity)----------------------------------------*/
        btnBuscar.setOnClickListener {
            /*------------- BUSCAR CON COORDENADAS DEL LUGAR SELECCIONADO----------------------*/
            var intent = Intent(this,MapsActivity :: class.java)
            intent.putExtra("ubi-latitud",coordLat)
            intent.putExtra("ubi-longitud",coordLon)
            intent.putExtra("namePlace", namePlace)
            startActivity(intent)
        }
        /*------------------------ SERVICIO DE GPS-------------------------------------*/
        //Para utilizar el servicio de ubicacion (GPS)
        locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var oyente = Oyente(this)
        //Especificar el servicio de ubicacion por medio de gps
        locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,01f,oyente)

    }
}//class


class Oyente(puntero :MainActivity) : LocationListener {
    var p = puntero
    override fun onLocationChanged(location: Location) {
        p.txtviewCordActuales.setText("${location.latitude}, ${location.longitude}")
        p.txtviewLugarActual.setText("")
        var geoPosicionGPS = GeoPoint(location.latitude, location.longitude)
        for (item in p.posicion){
            if (item.estoyEn(geoPosicionGPS)){
                p.txtviewLugarActual.setText("${item.nombre}" )
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }
}