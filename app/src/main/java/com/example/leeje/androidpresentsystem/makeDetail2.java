package com.example.leeje.androidpresentsystem;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.Thread.State.TERMINATED;

public class makeDetail2 extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener{

    private GoogleMap mGoogleMap = null;
    private long ar_time=1526827410;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef = database.getReference();
    private LinearLayout layout1;
    private LinearLayout layout2;
    TextView adr1;
    TextView adr2;
    Double lat;
    Double lon;
    Button pre;
    Button next;
    Double endlat=37.450890;
    Double endlon=126.656827;
    Double ck1lat;
    Double ck1lon;
    Double ck2lat;
    Double ck2lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_checkpoint_layout);
        adr1=(TextView) findViewById(R.id.adr1);
        adr2=(TextView) findViewById(R.id.adr2);
        layout1=(LinearLayout) findViewById(R.id.layout1);
        layout2=(LinearLayout) findViewById(R.id.layout2);

        Intent intent =getIntent();

        //  lat= intent.getDoubleExtra("lat",0);
        //  lon = intent.getDoubleExtra("lon",0);

        Log.e("??","ㅠㅠ");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        pre=(Button) findViewById(R.id.pre);
        next=(Button) findViewById(R.id.next1);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),showCheckpoint.class);
                intent.putExtra("ck1lat",ck1lat);
                intent.putExtra("ck1lon",ck1lon);
                intent.putExtra("ck2lat",ck2lat);
                intent.putExtra("ck2lon",ck2lon);
                startActivity(intent);
                finish();
            }
        });

        pre.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(),MakeDetail.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        readData(googleMap);



    }

    public static List<LatLng> decode(final String encodedPath) {
        int len = encodedPath.length();

        // For speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning.
        final List<LatLng> path = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        if(layout1.getVisibility()==View.INVISIBLE)
        {
            mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("체크포인트").icon(BitmapDescriptorFactory.fromResource(R.drawable.check)));
            layout1.setVisibility(View.VISIBLE);
            ck1lat=latLng.latitude;
            ck1lon=latLng.longitude;
            adr1.setText(getCurrentAddress(latLng));
        }

        else if(layout2.getVisibility()==View.INVISIBLE)
        {
            mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("체크포인트").icon(BitmapDescriptorFactory.fromResource(R.drawable.check)));
            layout2.setVisibility(View.VISIBLE);
            ck2lat=latLng.latitude;
            ck2lon=latLng.longitude;
            adr2.setText(getCurrentAddress(latLng));
        }

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

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }

    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();
            Log.d("Ex", data);
            br.close();

        }catch(Exception e){
            Log.d("Ex", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public void readData(final GoogleMap googleMap)
    {
        databaseRef.child("start").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lat=dataSnapshot.child("lat").getValue(Double.class);
                lon=dataSnapshot.child("lon").getValue(Double.class);
                Log.e("??","데이터 넣는중...");
                mapOperation(googleMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("???","에러발생");
            }
        });

    }

    public void mapOperation(GoogleMap googleMap)
    {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapLongClickListener(this);
        DownloadTask downloadTask = new DownloadTask();
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+Double.toString(lat)+","+Double.toString(lon)+"&destination="+Double.toString(endlat)+","+Double.toString(endlon)+"&mode=transit&arrival_time="+Long.toString(ar_time)+"&key=AIzaSyDdDWNDyd7YM9RRTdCa10ha3PhIOPScqQA";
        Log.d("??",url);
        String result="";
        String route="";
        try {
            result=downloadTask.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject=null;
            jsonObject=new JSONObject(result);
            route=jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
        } catch (JSONException e) {
            Log.d("??","오류오류");
        }

        List<LatLng> poly;

        poly=decode(route);

        PolylineOptions polyoption = new PolylineOptions();
        polyoption.geodesic(true);
        for(int i=0;i<poly.size();i++)
        {
            Log.d("??",Double.toString(poly.get(i).latitude));
            polyoption.add(poly.get(i));
        }

        polyoption.width(10);
        polyoption.color(Color.RED);
        mGoogleMap.addPolyline(polyoption);

        // 시작위치 지정하기

        //마커 표시하기
        MarkerOptions start = new MarkerOptions();
        start.position(new LatLng(lat, lon))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_start));
        mGoogleMap.addMarker(start).showInfoWindow();

        MarkerOptions end = new MarkerOptions();
        end.position(new LatLng(endlat,endlon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_arrive));
        mGoogleMap.addMarker(end).showInfoWindow();

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start.getPosition(),12));

        //줌 애니메이션
        //CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        //mGoogleMap.animateCamera(zoom);

    }

}
