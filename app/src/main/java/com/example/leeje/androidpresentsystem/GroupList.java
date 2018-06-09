package com.example.leeje.androidpresentsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GroupList extends AppCompatActivity  {

    private ListView group_list1;
    private FloatingActionButton groupMake;
    private Button enter_btn;

    int flag=0;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouplist);

        final String[] selected_group = {"null"};
        groupMake = findViewById(R.id.fbtn2);
        groupMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"약속이 생성되었습니다.",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                Intent intent = new Intent(GroupList.this , makeGroupName.class);

                startActivity(intent);
                flag=0;
            }
        });

        Log.e("text","list map");

        group_list1 = (ListView) findViewById(R.id.group_list);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
            group_list1.setAdapter(adapter);


        Log.e("text","before list click");
        group_list1.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("text","after list click");
                String groupname = (String) parent.getItemAtPosition(position);
                if(flag==1)
                {
                    Intent intent = new Intent(GroupList.this, showJoinNameList.class);
                    startActivity(intent);
                }

                else
                {
                    Intent intent = new Intent(GroupList.this,MakeDetail.class);
                    startActivity(intent);
                    flag=1;
                }
                if(groupname!="null") {
            //    intent.putExtra("groupName", groupname);

                }
            }
        });

        final FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
        Log.e("text","get uid");

        String uid=mUser.getUid();
        databaseReference.child("UID").child("uid_group").child(uid).child("group").addChildEventListener(new ChildEventListener() {
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





