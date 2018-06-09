package com.example.leeje.androidpresentsystem;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED;

public class showCheckpoint2 extends AppCompatActivity implements OnMapReadyCallback,   GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private long ar_time;
    long unixSeconds;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef = database.getReference();
    Double endlat;
    Double endlon;
    Double startlat;
    Double startlon;
    Double ck1lat;
    Double ck1lon;
    Double ck2lat;
    Double ck2lon;
    int flag=0;
    private TextView adr1;
    private TextView adr2;
    private TextView adr3;
    private TextView time1;
    private TextView time2;
    private TextView time3;
    private Button next;
    private float pressedX;

    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPosition;

    LatLng departure;

    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_checkpoint_list_layout2);

        Log.d(TAG, "onCreate");
        mActivity = this;



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

                Intent intent = new Intent(showCheckpoint2.this,main_location.class);
                startActivity(intent);
                finish();
            }
        });
        //1528541455
        //1528541755

        mActivity = this;


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        readData(googleMap);

        mGoogleMap.setOnMapLongClickListener(this);

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){

            @Override
            public boolean onMyLocationButtonClick() {

                Log.d( TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {

                if (mMoveMapByUser == true && mRequestingLocationUpdates){

                    Log.d(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;

            }
        });


        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {


            }
        });


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

    public void readData(final GoogleMap googleMap)
    {
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                startlat=dataSnapshot.child("이지은").child("start").child("lat").getValue(Double.class);
                startlon=dataSnapshot.child("이지은").child("start").child("lon").getValue(Double.class);
                ar_time=dataSnapshot.child("end").child("time").getValue(Long.class);
                endlat=dataSnapshot.child("end").child("lat").getValue(Double.class);
                endlon=dataSnapshot.child("end").child("lon").getValue(Double.class);

                ck1lat=  dataSnapshot.child("이지은").child("ck1").child("lat").getValue(Double.class);
                ck1lon =  dataSnapshot.child("이지은").child("ck1").child("lon").getValue(Double.class);
                ck2lat =  dataSnapshot.child("이지은").child("ck2").child("lat").getValue(Double.class);
                ck2lon =  dataSnapshot.child("이지은").child("ck2").child("lon").getValue(Double.class);

                mapOperation(googleMap);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("???","에러발생");
            }
        });

    }
    public void mapOperation (GoogleMap googleMap)
    {
        IconGenerator iconFactory=new IconGenerator(this);
//
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


        MarkerOptions end = new MarkerOptions();
        end.position(new LatLng(endlat,endlon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_arrive));
        mGoogleMap.addMarker(end).showInfoWindow();

        adr2.setText(getCurrentAddress(ck1.getPosition()));
        adr3.setText(getCurrentAddress(ck2.getPosition()));


        MarkerOptions start = new MarkerOptions();
        start.position(new LatLng(startlat, startlon))
                //  .title("체크포인트1")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_start));
        mGoogleMap.addMarker(start);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(startlat,startlon),13));
        adr1.setText(getCurrentAddress(new LatLng(startlat,startlon)));

        urlTask url3 = new urlTask();
        String timetxt3= null;
        unixSeconds=ar_time;
        try {
            timetxt3 = url3.execute(ck2lat,ck2lon,endlat,endlon).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(flag==1)
        {
            flag=0;

            unixSeconds=unixSeconds-Long.parseLong(timetxt3)*60;

            Date date = new Date(unixSeconds*1000L);

            SimpleDateFormat hour = new SimpleDateFormat("hh");
            SimpleDateFormat minute = new SimpleDateFormat("mm");
            String formattedDate = hour.format(date)+"시 "+minute.format(date)+"분";
            databaseRef.child("이지은").child("ck2").child("도착시간").setValue(unixSeconds);
            time3.setText(formattedDate);

        }
//1527496740
//1527496800
        else
        {
            unixSeconds = Long.parseLong(timetxt3);
            Date date = new Date(unixSeconds*1000L);

            SimpleDateFormat hour = new SimpleDateFormat("hh");
            SimpleDateFormat minute = new SimpleDateFormat("mm");
            String formattedDate = hour.format(date)+"시 "+minute.format(date)+"분";
            databaseRef.child("이지은").child("ck2").child("도착시간").setValue(unixSeconds);
            time3.setText(formattedDate);

        }

        urlTask url2 = new urlTask();
        String timetxt2= null;
        try {
            timetxt2 = url2.execute(ck1lat,ck1lon,ck2lat,ck2lon).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(flag==1)
        {
            flag=0;

            unixSeconds=unixSeconds-Long.parseLong(timetxt2)*60;

            Date date = new Date(unixSeconds*1000L);

            SimpleDateFormat hour = new SimpleDateFormat("hh");
            SimpleDateFormat minute = new SimpleDateFormat("mm");
            String formattedDate = hour.format(date)+"시 "+minute.format(date)+"분";
            databaseRef.child("이지은").child("ck1").child("도착시간").setValue(unixSeconds);
            time2.setText(formattedDate);

        }

        else
        {
            unixSeconds = Long.parseLong(timetxt2);
            Date date = new Date(unixSeconds*1000L);

            SimpleDateFormat hour = new SimpleDateFormat("hh");
            SimpleDateFormat minute = new SimpleDateFormat("mm");
            String formattedDate = hour.format(date)+"시 "+minute.format(date)+"분";
            databaseRef.child("이지은").child("ck1").child("도착시간").setValue(unixSeconds);
            time2.setText(formattedDate);

        }




        urlTask url = new urlTask();
        String timetxt1= null;
        try {
            timetxt1 = url.execute(startlat,startlon,ck1lat,ck1lon).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(flag==1)
        {
            flag=0;

            unixSeconds=unixSeconds-Long.parseLong(timetxt1)*60;

            Date date = new Date(unixSeconds*1000L);

            SimpleDateFormat hour = new SimpleDateFormat("hh");
            SimpleDateFormat minute = new SimpleDateFormat("mm");
            String formattedDate = hour.format(date)+"시 "+minute.format(date)+"분";
            databaseRef.child("이지은").child("start").child("출발시간").setValue(unixSeconds);
            time1.setText(formattedDate);

        }

        else
        {
            unixSeconds = Long.parseLong(timetxt1);
            Date date = new Date(unixSeconds*1000L);

            SimpleDateFormat hour = new SimpleDateFormat("hh");
            SimpleDateFormat minute = new SimpleDateFormat("mm");
            String formattedDate = hour.format(date)+"시 "+minute.format(date)+"분";
            databaseRef.child("이지은").child("start").child("출발시간").setValue(unixSeconds);
            time1.setText(formattedDate);

        }






    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }


    private class urlTask extends AsyncTask<Double, Void, String> {

        String data;
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(Double... doubles) {

            // For storing data from web service
            try {
                // Fetching the data from web service
                data = downloadUrl(doubles[0],doubles[1],doubles[2],doubles[3]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }

    private String downloadUrl(Double lat,Double lon,Double enda, Double endo) throws IOException{

        Location a=new Location("a");
        a.setLatitude(lat);
        a.setLongitude(lon);

        Location b=new Location("b");
        b.setLatitude(enda);
        b.setLongitude(endo);

        float dist=a.distanceTo(b);

        Log.e("??????",Float.toString(dist));

        if(dist<=700)
        {
            flag=1;
            Document doc2= Jsoup.connect("https://map.naver.com/findroute2/findWalkRoute.nhn")
                    .data("call","route2")
                    .data("output","xml")
                    .data("coord_type","naver")
                    .data("search","0")
                    .data("start",Double.toString(lon)+","+Double.toString(lat)+",ㅋ")
                    .data("destination",Double.toString(endo)+","+Double.toString(enda)+",ㅋ")
                    .parser(Parser.xmlParser()).get();

            Log.e("??",doc2.location());
            Log.e("띠용",doc2.select("totalTime").text());
            return doc2.select("totalTime").text();
        }
        else
        {
            Document doc= Jsoup.connect("https://maps.googleapis.com/maps/api/directions/xml")
                    .data("origin",Double.toString(lat)+","+Double.toString(lon))
                    .data("destination",Double.toString(enda)+","+Double.toString(endo))
                    .data("mode","transit")
                    .data("arrival_time",Long.toString(unixSeconds))
                    .data("key","AIzaSyDdDWNDyd7YM9RRTdCa10ha3PhIOPScqQA")
                    .parser(Parser.xmlParser()).get();

            Log.e("여기 ㅜㅜ",doc.location());
            Log.e("띠용",doc.select("leg departure_time text").last().text());
            return doc.select("leg departure_time value").last().text();

        }
        /*
        if(flag==1.0)
        {
            return doc.select("leg departure_time value").last().text();
        }
        else
        {
            return  doc.select("leg duration value").last().text();
        }
*/
        /*

        stemp안에 travel mode == transit 이라면면
       transit_detail -> step (여러개있음) -> leg -> route -> directionsresponse>

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
        */
    }


    //long unixTime = System.currentTimeMillis()/1000L;
/*
    //arrival_time 원하는 도착시간 departure_time 출발시간
    1970년 1월 1일 자정 이후의 시간 초단위
    2012년 7월 30일 9시 45분 1343641500

    arrival_stop departure_stop 출발 및 도착시의 정류장 정보
    name 정류장 이름
    location  위치

1000밀리초는 1초니까, getTime()으로 구한 값을 1000으로 나누면 초를 얻습니다.



마찬가지로 분을 구할 때는 1000*60=60000으로 나누고,



시를 구할 때는 1000*60*60=3600000으로 나눕니다.


Calendar cal = Calendar.getInstance();
cal.set(Calendar.HOUR, 0);
cal.set(Calendar.MINUTE, 0);
cal.set(Calendar.SECOND, 0);
long unixTime = cal.getTime() / 1000;


  */

    public void onResume() {

        super.onResume();

        if (mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }


        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }
    }


    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);

        }

    }



    private void stopLocationUpdates() {

        Log.d(TAG,"stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }




    @Override
    public void onLocationChanged(Location location) {

        currentPosition
                = new LatLng( location.getLatitude(), location.getLongitude());


        Log.d(TAG, "onLocationChanged : ");

        String markerTitle = getCurrentAddress(currentPosition);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());

        //현재 위치에 마커 생성하고 이동
        setCurrentLocation(location, markerTitle, markerSnippet);

        mCurrentLocatiion = location;
    }


    @Override
    protected void onStart() {

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){

            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    protected void onStop() {

        if (mRequestingLocationUpdates) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if ( mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) {


        if ( mRequestingLocationUpdates == false ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                } else {

                    Log.d(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

            }else{

                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed");
        setDefaultLocation();
    }


    @Override
    public void onConnectionSuspended(int cause) {

        Log.d(TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        mMoveMapByUser = false;


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        //구글맵의 디폴트 현재 위치는 파란색 동그라미로 표시
        //마커를 원하는 이미지로 변경하여 현재 위치 표시하도록 수정 fix - 2017. 11.27
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));

//        currentMarker = mGoogleMap.addMarker(markerOptions);


        if ( mMoveMapByAPI ) {

            Log.d( TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude() ) ;
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng,18);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }


    public void setDefaultLocation() {

        mMoveMapByUser = false;


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {


            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");

            if ( mGoogleApiClient.isConnected() == false) {

                Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {


                if ( mGoogleApiClient.isConnected() == false) {

                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }



            } else {

                checkPermissions();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(showCheckpoint2.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(showCheckpoint2.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(showCheckpoint2.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");


                        if ( mGoogleApiClient.isConnected() == false ) {

                            Log.d( TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }

                break;
        }
    }


}
