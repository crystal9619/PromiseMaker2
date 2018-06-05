package com.example.leeje.androidpresentsystem;

/**
 * Created by leeje on 2018-05-03.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class makedate extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseRef = database.getReference();
    private Button next;
    private DatePicker calender;
    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_makepromise_data);
        next=(Button)findViewById(R.id.next);
        calender = (DatePicker) findViewById(R.id.calender);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calender.getYear();

                Intent intent = new Intent(getApplicationContext(),makeTime.class);
                intent.putExtra("year",calender.getYear());
                intent.putExtra("month",calender.getMonth());
                intent.putExtra("date",calender.getDayOfMonth());
                startActivity(intent);
                finish();
            }
        });

        /*
        calender.init(calender.getYear(), calender.getMonth(), calender.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {

            }
        });*/


    }

}

