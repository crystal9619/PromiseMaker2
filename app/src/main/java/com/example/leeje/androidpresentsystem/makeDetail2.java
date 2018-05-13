package com.example.leeje.androidpresentsystem;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.Thread.State.TERMINATED;

public class makeDetail2 extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mGoogleMap = null;
    String b="냐";
    TextView adr;
    Double lat;
    Double lon;
    Button pre;
    Button next;
    Double endlat;
    Double endlon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_checkpoint_layout);
        adr=(TextView) findViewById(R.id.adr);

        Intent intent =getIntent();
        lat= intent.getDoubleExtra("lat",0);
        lon = intent.getDoubleExtra("lon",0);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        pre=(Button) findViewById(R.id.pre);
        next=(Button) findViewById(R.id.next1);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),showCheckpoint.class);
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
        mGoogleMap = googleMap;

        DownloadTask downloadTask = new DownloadTask();
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+Double.toString(lat)+","+Double.toString(lon)+"&destination=37.4475541,126.6531090&mode=transit&key=AIzaSyDdDWNDyd7YM9RRTdCa10ha3PhIOPScqQA";
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


        Log.d("??", Integer.toString(poly.size()));
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

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lon)));

        //줌 애니메이션
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        mGoogleMap.animateCamera(zoom);

        //마커 표시하기
        MarkerOptions marker = new MarkerOptions();
        marker.position(new LatLng(lat, lon))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_start));//부제
        mGoogleMap.addMarker(marker).showInfoWindow();

        //자신의 위치
        /*
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
    //    mGoogleMap.setMyLocationEnabled(true);

        //이벤트 처리하기
        /*
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(), marker.getTitle() + "를 클릭했습니다.", Toast.LENGTH_SHORT).show();
                return false;
            }

        });
*/
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


}
