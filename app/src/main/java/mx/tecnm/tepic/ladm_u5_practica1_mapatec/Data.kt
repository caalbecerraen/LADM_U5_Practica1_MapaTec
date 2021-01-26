package mx.tecnm.tepic.ladm_u5_practica1_mapatec

import com.google.firebase.firestore.GeoPoint

class Data{
    var nombre : String = ""
    var posicion1 : GeoPoint = GeoPoint(0.0,0.0)
    var posicion2 : GeoPoint = GeoPoint(0.0,0.0)
    var ubicacionMapa : GeoPoint = GeoPoint(0.0,0.0)

    override fun toString(): String {
        return nombre + "\n" + posicion1.latitude + "," + posicion1.longitude + "\n" +
                posicion2.latitude + "," + posicion2.longitude + "\n" + ubicacionMapa.latitude + "," + ubicacionMapa.longitude
    }

    fun estoyEn(posicionActual: GeoPoint) : Boolean{
        //logica es similar a la clase figuraGeometrica de canvas
        //estoyEn es similar a estaEnArea
        if (posicionActual.latitude >= posicion1.latitude && posicionActual.latitude <= posicion2.latitude){
            if (invertir(posicionActual.longitude) >= invertir(posicion1.longitude) && invertir(posicionActual.longitude) <= invertir(posicion2.longitude)){
                return true
            }
        }

        return false
    }
    //Quitar el signo de menos de longitud que esta guardado en bd remota
    private fun  invertir(valor:Double) :Double {
        return valor*-1
    }
}