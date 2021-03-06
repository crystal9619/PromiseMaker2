package com.example.leeje.androidpresentsystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class ListOnline extends AppCompatActivity {

    Button detail;
    Button show;
    Button group;
    Button promise;
    //firebase
    DatabaseReference onlineRef, currentUserRef, counterRef;
    FirebaseRecyclerAdapter<User, ListOnlineViewHolder> adapter;
    //View

    RecyclerView listOnline;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);

        //Init view
        listOnline = (RecyclerView) findViewById(R.id.listOnline);
        listOnline.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listOnline.setLayoutManager(layoutManager);

        //임시공간
        detail = (Button) findViewById(R.id.promiseDetail);
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MakeDetail.class);
                startActivity(intent);
            }
        });

        show = (Button) findViewById(R.id.mainMap);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), main_location.class);
                startActivity(intent);

            }
        });

        group = (Button) findViewById(R.id.groupMake);
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), firstgroupActivity.class);
                startActivity(intent);
            }
        });

        promise = (Button) findViewById(R.id.promiseMake);
        promise.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           Intent intent = new Intent(getApplicationContext(), makedate.class);
                                           startActivity(intent);
                                       }
                                   }


        );


        //       FirebaseMessaging.getInstance().subscribeToTopic("news");
        //       FirebaseInstanceId.getInstance().getToken();

        //set toolbar and logout/join menu

    }



}









