package com.example.leeje.androidpresentsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by 수정 on 2018-06-05.
 */

public class makeTime extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private TimePicker timepicker;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_makepromise_time);
        timepicker=(TimePicker) findViewById(R.id.timePicker);
        Intent intent = getIntent();
        final int year = intent.getIntExtra("year", 0);
        final int month = intent.getIntExtra("month", 0);
        final int date = intent.getIntExtra("date", 0);

        next=(Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DATE,date);
                calendar.set(Calendar.HOUR_OF_DAY,timepicker.getHour());
                calendar.set(Calendar.MINUTE,timepicker.getMinute());
                calendar.set(Calendar.SECOND,0);
                long endTime=calendar.getTimeInMillis();
                endTime=endTime/1000;
                databaseReference.child("end").child("time").setValue(endTime);
                Log.e("날짜",Integer.toString(year)+" "+Integer.toString(month)+" "+Integer.toString(date)+" "+Integer.toString(timepicker.getHour())+" "+Integer.toString(timepicker.getMinute())+" "+Long.toString(endTime));
                Intent intent = new Intent(getApplicationContext(),makePosition.class);
                startActivity(intent);
                finish();
            }
        });





    }
}
