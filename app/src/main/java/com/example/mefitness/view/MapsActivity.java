package com.example.mefitness.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.example.mefitness.R;
import com.example.mefitness.model.RecentPlace;
import com.example.mefitness.viewmodel.CustomBottomDialog;
import com.example.mefitness.viewmodel.CustomDialogLatLon;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, CustomBottomDialog.CustomBottomDialogListener, CustomDialogLatLon.CustomDialogListenerLatLon {
    GoogleMap map;
    SupportMapFragment mapFragment;
    SearchView searchView;
    ArrayList<RecentPlace> recentPlaces;
    int mapMarkCount = 0;
    ArrayList<LatLng> latLngs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        recentPlaces = new ArrayList<>();
        dialog();


        Toolbar toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        toolbar.setTitle("Mapa");
        toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fabZoomOut = findViewById(R.id.fab_maps_zoom_out);
        fabZoomOut.setOnClickListener(v -> {
            if(mapMarkCount==1) map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 0));
            else if(mapMarkCount==2) map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 0));
            fabZoomOut.setVisibility(FloatingActionButton.GONE);
        });

        FloatingActionButton fabLatLon = findViewById(R.id.fab_maps_input_lat_long);
        fabLatLon.setOnClickListener(v -> {
            CustomDialogLatLon customDialogLatLong = new CustomDialogLatLon();
            customDialogLatLong.show(getSupportFragmentManager(), "Custom Dialog");
        });
        searchView = findViewById(R.id.sv_location);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        if(mapMarkCount==2) {
                            map.clear();
                            FloatingActionButton fabZoomOut = findViewById(R.id.fab_maps_zoom_out);
                            fabZoomOut.setVisibility(FloatingActionButton.GONE);
                            LinearLayout linearLayout = findViewById(R.id.map_item_child);
                            linearLayout.setVisibility(LinearLayout.GONE);
                            latLngs.clear();
                            mapMarkCount=0;
                            isPolylineEnable=false;
                        }
                        map.addMarker(new MarkerOptions().position(latLng).title(location));
                        if(mapMarkCount==0){
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            FloatingActionButton fabZoomOut = findViewById(R.id.fab_maps_zoom_out);
                            fabZoomOut.setVisibility(FloatingActionButton.VISIBLE);
                        }
                        latLngs.add(new LatLng(latLng.latitude, latLng.longitude));
                        if(mapMarkCount==1) distanceBetweenTwoPointsNoDataSet(latLngs.get(0), latLngs.get(1));
                        mapMarkCount++;
                        recentPlaces.add(new RecentPlace(location, address.getLatitude() + "", address.getLongitude() + ""));
                        dialog();

                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(MapsActivity.this, "Localização não encontrada", Toast.LENGTH_SHORT).show();
                    }
                }
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLongClickListener(latLng -> {
            if (mapMarkCount == 2) {
                latLngs.clear();
                map.clear();
                mapMarkCount = 0;
                isPolylineEnable= false;
            }
            else if (mapMarkCount == 1) {

                map.addMarker(new MarkerOptions().position(latLng).title(""));
                recentPlaces.add(new RecentPlace("Lat: " + Math.round(latLng.latitude) + " Lon: " + Math.round(latLng.longitude), latLng.latitude + "", latLng.longitude + ""));
                latLngs.add(new LatLng(latLng.latitude, latLng.longitude));
                mapMarkCount++;
                distanceBetweenTwoPointsNoDataSet(latLngs.get(0), latLngs.get(1));
            } else {
                mapMarkCount++;

                map.addMarker(new MarkerOptions().position(latLng).title(""));
                recentPlaces.add(new RecentPlace("Lat: " + Math.round(latLng.latitude) + " Lon: " + Math.round(latLng.longitude), latLng.latitude + "", latLng.longitude + ""));
                latLngs.add(new LatLng(latLng.latitude, latLng.longitude));
            }
        });

        drawPolyline();

    }





    /*
     * */

    public void dialog() {
        CustomBottomDialog customBottomDialog = new CustomBottomDialog(MapsActivity.this, recentPlaces);
        FloatingActionButton button = findViewById(R.id.fab_maps);
        button.setOnClickListener(v -> customBottomDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment"));
    }

    boolean isPolylineEnable = false;

    public void drawPolyline() {

        FloatingActionButton button = findViewById(R.id.fab_maps_add_polyline);
        button.setOnClickListener(v -> {
            if (!isPolylineEnable) {
                if (mapMarkCount == 2) {
                    if (latLngs.size() != 0) {
                        LatLng origin = latLngs.get(0);
                        LatLng destiny = latLngs.get(1);
                        try {
                            map.addPolyline(new PolylineOptions()
                                    .add(new LatLng(origin.latitude, origin.longitude), new LatLng(destiny.latitude, destiny.longitude))
                                    .width(5)
                                    .color(Color.RED));
                            distanceBetweenTwoPoints(origin, destiny);
                            isPolylineEnable = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(MapsActivity.this, "destiny nulo", Toast.LENGTH_SHORT).show();
                } else if (mapMarkCount == 1)
                    Toast.makeText(MapsActivity.this, "Defina o ponto de destino", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MapsActivity.this, "Defina o ponto de origem", Toast.LENGTH_SHORT).show();
            }
            else {
                map.clear();
                LinearLayout linearLayout = findViewById(R.id.map_item_child);
                linearLayout.setVisibility(LinearLayout.GONE);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 0));
                latLngs.clear();
                isPolylineEnable = false;
                mapMarkCount=0;
            }
        });

    }

    public void distanceBetweenTwoPoints(LatLng origin, LatLng destiny) {
        float[] results = new float[1];
        Location.distanceBetween(origin.latitude, origin.longitude,
                destiny.latitude, destiny.longitude,
                results);
        LinearLayout linearLayout = findViewById(R.id.map_item_child);
        linearLayout.setVisibility(LinearLayout.VISIBLE);
        TextView textView = findViewById(R.id.map_item_child_textView);
        int result = Math.round(results[0]);
        String text = "";
        if (result > 1500) {
            result = result / 1000;
            text = "Distancia entre os dois pontos em quilômetros: " + result;
        } else text = "Distancia entre os dois pontos em metros: " + result;

        textView.setText(text);
    }

    public void distanceBetweenTwoPointsNoDataSet(LatLng origin, LatLng destiny) {
        float[] results = new float[1];
        Location.distanceBetween(origin.latitude, origin.longitude,
                destiny.latitude, destiny.longitude,
                results);

        int result = Math.round(results[0]);
        if (result < 1500) map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
        else if (result >15000 && result < 50000) map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 9));
        else if (result >50000 && result < 100000) map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 8));
        else if (result >100000 && result < 200000) map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 7));
        else if (result >200000 && result < 400000) map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 6));
        else if (result >400000 && result < 800000) map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 5));
        else if (result >800000 && result < 1600000) map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 4));
        else if (result >1600000 && result < 3200000) map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 3));
        else if (result >3200000 && result < 6400000) map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 2));
        else if (result >12800000 && result < 25600000) map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 1));
        else if (result >25600000) map.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 0));

        FloatingActionButton fabZoomOut = findViewById(R.id.fab_maps_zoom_out);
        fabZoomOut.setVisibility(FloatingActionButton.GONE);
    }

    @Override
    public void appyTexts(String minutes, String seconds) {
        LatLng latLng = new LatLng(Double.parseDouble(minutes), Double.parseDouble(seconds));
        if(mapMarkCount==2) {
            map.clear();
            mapMarkCount=0;
            latLngs.clear();
            isPolylineEnable = false;
            FloatingActionButton fabZoomOut = findViewById(R.id.fab_maps_zoom_out);
            fabZoomOut.setVisibility(FloatingActionButton.GONE);
            LinearLayout linearLayout = findViewById(R.id.map_item_child);
            linearLayout.setVisibility(LinearLayout.GONE);
        }

        map.addMarker(new MarkerOptions().position(latLng).title(""));

        if(mapMarkCount==0) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            FloatingActionButton fabZoomOut = findViewById(R.id.fab_maps_zoom_out);
            fabZoomOut.setVisibility(FloatingActionButton.VISIBLE);
        }
        latLngs.add(new LatLng(latLng.latitude, latLng.longitude));
        if(mapMarkCount==1) distanceBetweenTwoPointsNoDataSet(latLngs.get(0), latLngs.get(1));

        mapMarkCount++;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void apply(String lat, String lon) {
        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        if(mapMarkCount==2) {
            map.clear();
            mapMarkCount=0;
            latLngs.clear();
            isPolylineEnable = false;
            FloatingActionButton fabZoomOut = findViewById(R.id.fab_maps_zoom_out);
            fabZoomOut.setVisibility(FloatingActionButton.GONE);
            LinearLayout linearLayout = findViewById(R.id.map_item_child);
            linearLayout.setVisibility(LinearLayout.GONE);
        }
        map.addMarker(new MarkerOptions().position(latLng).title(""));

        if(mapMarkCount==0) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            FloatingActionButton fabZoomOut = findViewById(R.id.fab_maps_zoom_out);
            fabZoomOut.setVisibility(FloatingActionButton.VISIBLE);
        }
        latLngs.add(new LatLng(latLng.latitude, latLng.longitude));
        if(mapMarkCount==1) distanceBetweenTwoPointsNoDataSet(latLngs.get(0), latLngs.get(1));


        recentPlaces.add(new RecentPlace("Lat: " + Math.round(latLng.latitude) + " Lon: " + Math.round(latLng.longitude), latLng.latitude + "", latLng.longitude + ""));
        mapMarkCount++;
     }
}