package com.example.leeje.androidpresentsystem;

/**
 * Created by leeje on 2018-05-13.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class firstgroupActivity extends AppCompatActivity {

        private EditText user_chat, user_edit;
        private Button user_next;
        private ListView chat_list;

        private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        private DatabaseReference databaseReference = firebaseDatabase.getReference();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_firstgroup);

            user_chat = (EditText) findViewById(R.id.user_chat);
            user_edit = (EditText) findViewById(R.id.user_edit);
            user_next = (Button) findViewById(R.id.user_next);
            chat_list = (ListView) findViewById(R.id.chat_list);

            user_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user_edit.getText().toString().equals("") || user_chat.getText().toString().equals(""))
                        return;

                    Intent intent = new Intent(firstgroupActivity.this, groupActivity.class);
                    intent.putExtra("chatName", user_chat.getText().toString());
                    intent.putExtra("userName", user_edit.getText().toString());
                    startActivity(intent);
                }
            });

            showChatList();

        }

        private void showChatList() {
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
            chat_list.setAdapter(adapter);

            databaseReference.child("chat").addChildEventListener(new ChildEventListener() {
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





