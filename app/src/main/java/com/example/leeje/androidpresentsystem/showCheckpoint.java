package com.example.leeje.androidpresentsystem;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class showCheckpoint extends AppCompatActivity implements OnMapReadyCallback {

    Double endlat=37.450890;
    Double endlon=126.656827;
    Double ck1lat;
    Double ck1lon;
    Double ck2lat;
    Double ck2lon;
    private GoogleMap mGoogleMap;
    private TextView adr1;
    private TextView adr2;
    private TextView adr3;
    private TextView time1;
    private TextView time2;
    private TextView time3;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_checkpoint_list_layout);

        Intent intent = getIntent();
        ck1lat = intent.getDoubleExtra("ck1lat", 0);
        ck1lon = intent.getDoubleExtra("ck1lon", 0);
        ck2lat = intent.getDoubleExtra("ck2lat", 0);
        ck2lon = intent.getDoubleExtra("ck2lon", 0);

        adr1 = (TextView) findViewById(R.id.adr1);
        adr2 = (TextView) findViewById(R.id.adr2);
        adr3 = (TextView) findViewById(R.id.adr3);
        time1 = (TextView) findViewById(R.id.time1);
        time2 = (TextView) findViewById(R.id.time2);
        time3 = (TextView) findViewById(R.id.time3);
        next = (Button) findViewById(R.id.button);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        IconGenerator iconFactory=new IconGenerator(this);

        MarkerOptions ck1 = new MarkerOptions();
        ck1.position(new LatLng(ck1lat, ck1lon))
                //  .title("체크포인트1")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.check));
        mGoogleMap.addMarker(ck1).showInfoWindow();

        MarkerOptions ck2 = new MarkerOptions();
        ck2.position(new LatLng(ck2lat, ck2lon))
                //  .title("체크포인트2")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.check));
        mGoogleMap.addMarker(ck2).showInfoWindow();

        MarkerOptions ck1txt = new MarkerOptions();
        ck1txt.position(new LatLng(ck1lat+0.0015, ck1lon))
                //  .title("체크포인트1")
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("체크포인트1")));
        mGoogleMap.addMarker(ck1txt).showInfoWindow();

        MarkerOptions ck2txt = new MarkerOptions();
        ck2txt.position(new LatLng(ck2lat+0.0015, ck2lon))
                //  .title("체크포인트2")
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("체크포인트2")));
        mGoogleMap.addMarker(ck2txt).showInfoWindow();

        adr2.setText(getCurrentAddress(ck1.getPosition()));
        adr3.setText(getCurrentAddress(ck2.getPosition()));

    }

    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }
}
