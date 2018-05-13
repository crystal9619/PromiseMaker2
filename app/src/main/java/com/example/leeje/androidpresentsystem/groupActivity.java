package com.example.leeje.androidpresentsystem;

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
/**
 * Created by leeje on 2018-05-11.
 */

public class groupActivity extends AppCompatActivity{

        private String CHAT_NAME;
        private String USER_NAME;

        private ListView chat_view;
        private EditText chat_edit;
        private Button chat_send;

        private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        private DatabaseReference databaseReference = firebaseDatabase.getReference();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_group);

            chat_view = (ListView) findViewById(R.id.chat_view);
            chat_edit = (EditText) findViewById(R.id.chat_edit);
            chat_send = (Button) findViewById(R.id.chat_sent);

            Intent intent = getIntent();
            CHAT_NAME = intent.getStringExtra("chatName");
            USER_NAME = intent.getStringExtra("userName");

            openChat(CHAT_NAME);

            chat_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chat_edit.getText().toString().equals(""))
                        return;

                    groupData chat = new groupData(USER_NAME, chat_edit.getText().toString());
                    databaseReference.child("chat").child(CHAT_NAME).push().setValue(chat);
                    chat_edit.setText("");

                }
            });
        }

        private void addMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
            groupData chatDTO = dataSnapshot.getValue(groupData.class);
            adapter.add(chatDTO.getUserName() + " : " + chatDTO.getMessage());
        }

        private void removeMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
            groupData chatDTO = dataSnapshot.getValue(groupData.class);
            adapter.remove(chatDTO.getUserName() + " : " + chatDTO.getMessage());
        }

        private void openChat(String chatName) {
            // 리스트 어댑터 생성 및 세팅
            final ArrayAdapter<String> adapter

                    = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
            chat_view.setAdapter(adapter);

            // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
            databaseReference.child("chat").child(chatName).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    addMessage(dataSnapshot, adapter);
                    Log.e("LOG", "s:"+s);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    removeMessage(dataSnapshot, adapter);
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
