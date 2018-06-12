package com.example.leeje.androidpresentsystem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class showCheckpoint extends AppCompatActivity implements OnMapReadyCallback {

    Long startUnixTime;
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
    private GoogleMap mGoogleMap;
    private TextView adr1;
    private TextView adr2;
    private TextView adr3;
    private TextView time1;
    private TextView time2;
    private TextView time3;
    private Button next;
    private float pressedX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_checkpoint_list_layout);


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
               Calendar calendar = Calendar.getInstance();
               calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
               Intent mAlarmIntent = new Intent(showCheckpoint.this,BroadcastD.class);
               PendingIntent mPendingIntent = PendingIntent.getBroadcast(showCheckpoint.this,0,mAlarmIntent,0);
                AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= 23)
                {
                    Log.e("알람울리는시간",Long.toString(   startUnixTime-300*1000L));
                    Log.e("알람현재시간",Long.toString(calendar.getTimeInMillis()));
                    mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,startUnixTime-300*1000L, mPendingIntent);
                }

                else
                {
                    if ( Build.VERSION.SDK_INT >= 19) {
                        Log.e("알람현재시간",Long.toString(calendar.getTimeInMillis()));
                        Log.e("알람울리는시간", Long.toString(startUnixTime - 300 * 1000L));
                        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,startUnixTime - 300 * 1000L, mPendingIntent);
                    }
                    else
                    {
                        Log.e("알람현재시간",Long.toString(calendar.getTimeInMillis()));
                        Log.e("알람울리는시간",Long.toString(startUnixTime-300*1000L));
                        mAlarmManager.set(AlarmManager.RTC_WAKEUP, startUnixTime-300*1000L, mPendingIntent);
                    }


                }

                Toast.makeText(getApplicationContext(), "출발시간 5분전 알림이 울립니다.",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        //1528541455
        //1528541755
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        readData(googleMap);



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
                startlat=dataSnapshot.child("박수정").child("start").child("lat").getValue(Double.class);
                startlon=dataSnapshot.child("박수정").child("start").child("lon").getValue(Double.class);
                ar_time=dataSnapshot.child("end").child("time").getValue(Long.class);
                endlat=dataSnapshot.child("end").child("lat").getValue(Double.class);
                endlon=dataSnapshot.child("end").child("lon").getValue(Double.class);

                ck1lat=  dataSnapshot.child("박수정").child("ck1").child("lat").getValue(Double.class);
                ck1lon =  dataSnapshot.child("박수정").child("ck1").child("lon").getValue(Double.class);
                ck2lat =  dataSnapshot.child("박수정").child("ck2").child("lat").getValue(Double.class);
                ck2lon =  dataSnapshot.child("박수정").child("ck2").child("lon").getValue(Double.class);

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
            databaseRef.child("박수정").child("ck2").child("도착시간").setValue(unixSeconds);
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
            databaseRef.child("박수정").child("ck2").child("도착시간").setValue(unixSeconds);
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
            databaseRef.child("박수정").child("ck1").child("도착시간").setValue(unixSeconds);
            time2.setText(formattedDate);

        }

        else
        {
            unixSeconds = Long.parseLong(timetxt2);
            Date date = new Date(unixSeconds*1000L);

            SimpleDateFormat hour = new SimpleDateFormat("hh");
            SimpleDateFormat minute = new SimpleDateFormat("mm");
            String formattedDate = hour.format(date)+"시 "+minute.format(date)+"분";
            databaseRef.child("박수정").child("ck1").child("도착시간").setValue(unixSeconds);
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
            startUnixTime=unixSeconds*1000L;
            Date date = new Date(unixSeconds*1000L);

            SimpleDateFormat hour = new SimpleDateFormat("hh");
            SimpleDateFormat minute = new SimpleDateFormat("mm");
            String formattedDate = hour.format(date)+"시 "+minute.format(date)+"분";
            databaseRef.child("박수정").child("start").child("출발시간").setValue(unixSeconds);
            time1.setText(formattedDate);

        }

        else
        {
            unixSeconds = Long.parseLong(timetxt1);
            Date date = new Date(unixSeconds*1000L);
            startUnixTime=unixSeconds*1000L;
            SimpleDateFormat hour = new SimpleDateFormat("hh");
            SimpleDateFormat minute = new SimpleDateFormat("mm");
            String formattedDate = hour.format(date)+"시 "+minute.format(date)+"분";
            databaseRef.child("박수정").child("start").child("출발시간").setValue(unixSeconds);
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
}
