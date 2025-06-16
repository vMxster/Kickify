package it.unibo.kickify.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import it.unibo.kickify.utils.Coordinates
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OSMMapBox(coord: Coordinates, zoomLevel: Double = 18.0, modifier: Modifier) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            clipToOutline = true
        }
    }

    DisposableEffect(mapView) {
        onDispose { mapView.onDetach() }
    }

    LaunchedEffect(coord, zoomLevel) {
        mapView.controller.setZoom(zoomLevel)
        mapView.controller.setCenter(GeoPoint(coord.latitude, coord.longitude))
        mapView.overlays.clear()

        val marker = Marker(mapView)
        marker.position = GeoPoint(coord.latitude, coord.longitude)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)
        mapView.invalidate() // redraw map
    }

    AndroidView(
        modifier = modifier,
        factory = { mapView }
    )
}
