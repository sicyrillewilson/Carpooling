package tg.eplcoursandroid.carpooling.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import tg.eplcoursandroid.carpooling.R

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var nGoogleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Trouvez le SupportMapFragment à l'intérieur du fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_maps_id) as? SupportMapFragment

        // Obtenez la carte de manière asynchrone
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        nGoogleMap = googleMap

        // Exemple : Ajoutez un marqueur ou configurez la carte ici
        //val location = LatLng(-34.0, 151.0)
        //nGoogleMap?.addMarker(MarkerOptions().position(location).title("Marker in Sydney"))
        //nGoogleMap?.moveCamera(CameraUpdateFactory.newLatLng(location))
    }

}