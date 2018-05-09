package com.example.leeje.androidpresentsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class makeDetail2 extends AppCompatActivity {

    Button pre;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_checkpoint_layout);



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
}
