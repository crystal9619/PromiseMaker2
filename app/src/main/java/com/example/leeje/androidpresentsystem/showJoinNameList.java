package com.example.leeje.androidpresentsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by leeje on 2018-06-07.
 */

public class showJoinNameList extends AppCompatActivity {

    private ListView name_list1;
    private Button invite_btn;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showjoinnamelist);

        final String[] selected_group = {"null"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1);
        name_list1 = (ListView) findViewById(R.id.name_list);
        name_list1.setAdapter(adapter);
        invite_btn = (Button) findViewById(R.id.invite);


        Log.e("text","before list click");
        name_list1.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("text","after list click");
                String joinID = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(showJoinNameList.this, main_location.class);
                Intent getgroup=getIntent();
                //String getgroupname=getgroup.getStringExtra("ID");
               //databaseReference.child("group").child(getgroupname).;
                if(joinID!="null") {
                  //  Log.e("what","post");
                    //startActivity(intent);
                }
            }
        });
        invite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (invite_btn.getText().toString().equals(""))
                    return;
                Intent intent = new Intent(showJoinNameList.this , makedate.class);
                startActivity(intent);
                finish();
            }
        });
        final FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
        Log.e("text","get uid");
        String uid=mUser.getUid();
        databaseReference.child("UID").child("uid_group").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                adapter.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }
}





