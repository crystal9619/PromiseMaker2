package com.example.leeje.androidpresentsystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class main_location extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private long ck1lat;
    private long ck1lon;
    private Marker ck1marker = null;
    private Marker ck2marker = null;
    private Marker ck1marker2 = null;
    private Marker ck2marker2 = null;

    private long endTime;
    private Polyline line1;
    private Polyline line2;
    private Double endlat;
    private Double endlon;
    private TextView name1;
    private TextView name2;
    private TextView adr1;
    private TextView adr2;
    private ImageView img1;
    private ImageView img2;
    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;
    private Marker marker1 = null;
    private Marker marker2 = null;
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
    float pressedX;
    LatLng departure;

    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_location_layout);
        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(main_location.this, showCheckpoint2.class);
                startActivity(intent);
                finish();

            }
        });
        img1 = (ImageView) findViewById(R.id.image1);
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ck1marker.isVisible() == true) {
                    ck1marker.setVisible(false);
                    ck2marker.setVisible(false);
                    line1.setVisible(false);

                } else {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker1.getPosition(), 13);
                    mGoogleMap.moveCamera(cameraUpdate);
                    ck1marker.setVisible(true);
                    ck2marker.setVisible(true);
                    line1.setVisible(true);
                }
            }
        });
        img2 = (ImageView) findViewById(R.id.image2);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ck1marker2.isVisible() == true) {
                    ck1marker2.setVisible(false);
                    ck2marker2.setVisible(false);
                    line2.setVisible(false);

                } else {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker2.getPosition(), 13);
                    mGoogleMap.moveCamera(cameraUpdate);
                    ck1marker2.setVisible(true);
                    ck2marker2.setVisible(true);
                    line2.setVisible(true);
                }
            }
        });
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.layout);


        name1 = (TextView) findViewById(R.id.name1);
        name2 = (TextView) findViewById(R.id.name2);
        adr1 = (TextView) findViewById(R.id.adr1);
        adr2 = (TextView) findViewById(R.id.adr2);


        Log.d(TAG, "onCreate");
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
        } else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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

        Log.d(TAG, "stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady :");

        mGoogleMap = googleMap;

        databaseReference.child("end").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Date date2 = new Date(endTime * 1000L);

                SimpleDateFormat hour2 = new SimpleDateFormat("hh");
                SimpleDateFormat minute2 = new SimpleDateFormat("mm");
                String formattedDate2 = hour2.format(date2) + "시 " + minute2.format(date2) + "분";

                endlat = dataSnapshot.child("lat").getValue(Double.class);
                endlon = dataSnapshot.child("lon").getValue(Double.class);
                slow();
                MarkerOptions end = new MarkerOptions();
                end.position(new LatLng(endlat, endlon)).title(formattedDate2).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_arrive));
                mGoogleMap.addMarker(end).showInfoWindow();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


        databaseReference.child("박수정").child("moving").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double lat = dataSnapshot.child("moving_site_lat").getValue(Double.class);
                Double lon = dataSnapshot.child("moving_site_lon").getValue(Double.class);
                if (marker1 != null) marker1.remove();
                marker1 = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                name1.setText("박수정");
                adr1.setText(getCurrentAddress(new LatLng(lat, lon)));
                Log.e("??", "데이터 넣는중...");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("???", "에러발생");
            }
        });


        databaseReference.child("류경민").child("moving").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double lat = dataSnapshot.child("moving_site_lat").getValue(Double.class);
                Double lon = dataSnapshot.child("moving_site_lon").getValue(Double.class);
                if (marker2 != null) marker2.remove();
                marker2 = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                name2.setText("류경민");
                adr2.setText(getCurrentAddress(new LatLng(lat, lon)));
                Log.e("??", "데이터 넣는중...");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("???", "에러발생");
            }
        });


        mGoogleMap.setOnMapLongClickListener(this);

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {

                Log.d(TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d(TAG, "onMapClick :");
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {

                if (mMoveMapByUser == true && mRequestingLocationUpdates) {

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


    @Override
    public void onLocationChanged(Location location) {

        databaseReference.child("이지은").child("moving").child("moving_site_lat").setValue(location.getLatitude());
        databaseReference.child("이지은").child("moving").child("moving_site_lon").setValue(location.getLongitude());

        currentPosition
                = new LatLng(location.getLatitude(), location.getLongitude());


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

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() == false) {

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

        if (mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) {


        if (mRequestingLocationUpdates == false) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

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

            } else {

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


        if (mMoveMapByAPI) {

            Log.d(TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude());
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 13);
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
        //currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 13);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
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

            if (mGoogleApiClient.isConnected() == false) {

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


                if (mGoogleApiClient.isConnected() == false) {

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

        AlertDialog.Builder builder = new AlertDialog.Builder(main_location.this);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(main_location.this);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(main_location.this);
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


                        if (mGoogleApiClient.isConnected() == false) {

                            Log.d(TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }

                break;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

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

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("Ex", data);
            br.close();

        } catch (Exception e) {
            Log.d("Ex", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
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



    private void slow()
    {
        databaseReference.child("박수정").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Double startlat = dataSnapshot.child("start").child("lat").getValue(Double.class);
                Double startlon = dataSnapshot.child("start").child("lon").getValue(Double.class);
                Double ck1lat = dataSnapshot.child("ck1").child("lat").getValue(Double.class);
                Double ck1lon = dataSnapshot.child("ck1").child("lon").getValue(Double.class);
                Double ck2lat = dataSnapshot.child("ck2").child("lat").getValue(Double.class);
                Double ck2lon = dataSnapshot.child("ck2").child("lon").getValue(Double.class);
                long unixSeconds = dataSnapshot.child("ck1").child("도착시간").getValue(Long.class);
                Date date = new Date(unixSeconds * 1000L);

                SimpleDateFormat hour = new SimpleDateFormat("hh");
                SimpleDateFormat minute = new SimpleDateFormat("mm");
                String formattedDate = hour.format(date) + "시 " + minute.format(date) + "분";

                long unixSecond2s = dataSnapshot.child("ck2").child("도착시간").getValue(Long.class);
                Date date2 = new Date(unixSecond2s * 1000L);

                SimpleDateFormat hour2 = new SimpleDateFormat("hh");
                SimpleDateFormat minute2 = new SimpleDateFormat("mm");
                String formattedDate2 = hour2.format(date2) + "시 " + minute2.format(date2) + "분";

                ck1marker = mGoogleMap.addMarker(new MarkerOptions().title(formattedDate).position(new LatLng(ck1lat, ck1lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.check)));
                ck2marker = mGoogleMap.addMarker(new MarkerOptions().title(formattedDate2).position(new LatLng(ck2lat, ck2lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.check)));


                ck1marker.setVisible(false);
                ck2marker.setVisible(false);




                DownloadTask downloadTask = new DownloadTask();
                String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + Double.toString(startlat) + "," + Double.toString(startlon) + "&destination=" + Double.toString(endlat) + "," + Double.toString(endlon) + "&mode=transit&key=AIzaSyDdDWNDyd7YM9RRTdCa10ha3PhIOPScqQA";
                Log.d("??", url);
                String result = "";
                String route = "";
                try {
                    result = downloadTask.execute(url).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jsonObject = null;
                    jsonObject = new JSONObject(result);
                    route = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                } catch (JSONException e) {
                    Log.d("??", "오류오류");
                }

                List<LatLng> poly;

                poly = decode(route);

                PolylineOptions polyoption = new PolylineOptions();
                polyoption.geodesic(true);
                for (int i = 0; i < poly.size(); i++) {
                    Log.d("??", Double.toString(poly.get(i).latitude));
                    polyoption.add(poly.get(i));
                }

                polyoption.width(10);
                polyoption.color(Color.GREEN);
                line1 = mGoogleMap.addPolyline(polyoption);

                line1.setVisible(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        databaseReference.child("류경민").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double startlat = dataSnapshot.child("start").child("lat").getValue(Double.class);
                Double startlon = dataSnapshot.child("start").child("lon").getValue(Double.class);
                Double ck1lat = dataSnapshot.child("ck1").child("lat").getValue(Double.class);
                Double ck1lon = dataSnapshot.child("ck1").child("lon").getValue(Double.class);
                Double ck2lat = dataSnapshot.child("ck2").child("lat").getValue(Double.class);
                Double ck2lon = dataSnapshot.child("ck2").child("lon").getValue(Double.class);
                long unixSeconds = dataSnapshot.child("ck1").child("도착시간").getValue(Long.class);
                Date date = new Date(unixSeconds * 1000L);

                SimpleDateFormat hour = new SimpleDateFormat("hh");
                SimpleDateFormat minute = new SimpleDateFormat("mm");
                String formattedDate = hour.format(date) + "시 " + minute.format(date) + "분";

                long unixSecond2s = dataSnapshot.child("ck2").child("도착시간").getValue(Long.class);
                Date date2 = new Date(unixSecond2s * 1000L);

                SimpleDateFormat hour2 = new SimpleDateFormat("hh");
                SimpleDateFormat minute2 = new SimpleDateFormat("mm");
                String formattedDate2 = hour2.format(date2) + "시 " + minute2.format(date2) + "분";

                ck1marker2 = mGoogleMap.addMarker(new MarkerOptions().title(formattedDate).position(new LatLng(ck1lat, ck1lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.check)));
                ck2marker2 = mGoogleMap.addMarker(new MarkerOptions().title(formattedDate2).position(new LatLng(ck2lat, ck2lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.check)));


                ck1marker2.setVisible(false);
                ck2marker2.setVisible(false);


                DownloadTask downloadTask = new DownloadTask();
                String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + Double.toString(startlat) + "," + Double.toString(startlon) + "&destination=" + Double.toString(endlat) + "," + Double.toString(endlon) + "&mode=transit&key=AIzaSyDdDWNDyd7YM9RRTdCa10ha3PhIOPScqQA";
                Log.d("??", url);
                String result = "";
                String route = "";
                try {
                    result = downloadTask.execute(url).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jsonObject = null;
                    jsonObject = new JSONObject(result);
                    route = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                } catch (JSONException e) {
                    Log.d("??", "오류오류");
                }

                List<LatLng> poly;

                poly = decode(route);

                PolylineOptions polyoption = new PolylineOptions();
                polyoption.geodesic(true);
                for (int i = 0; i < poly.size(); i++) {
                    Log.d("??", Double.toString(poly.get(i).latitude));
                    polyoption.add(poly.get(i));
                }

                polyoption.width(10);
                polyoption.color(Color.YELLOW);
                line2 = mGoogleMap.addPolyline(polyoption);

                line2.setVisible(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        databaseReference.child("end").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                endTime=dataSnapshot.child("time").getValue(Long.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
