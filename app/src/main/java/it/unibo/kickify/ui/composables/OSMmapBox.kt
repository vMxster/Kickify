package it.unibo.kickify.ui.composables

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import it.unibo.kickify.utils.Coordinates
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OSMmapBox(coord: Coordinates, zoomLevel: Double = 18.0, modifier: Modifier) {
    AndroidView(
        modifier = modifier.wrapContentSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                clipToOutline = true
                setTileSource(TileSourceFactory.MAPNIK)
                controller.setZoom(zoomLevel)
                controller.setCenter(GeoPoint(coord.latitude, coord.longitude))

                val marker = Marker(this)
                marker.position = GeoPoint(coord.latitude, coord.longitude)
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                overlays.add(marker)
            }
        }
    )
}
