package com.example.leeje.androidpresentsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by leeje on 2018-06-04.
 */

public class makeName extends AppCompatActivity {

    private EditText using_name;
    private Button user_next;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makename);

        using_name = (EditText) findViewById(R.id.using_name);
        user_next = (Button) findViewById(R.id.user_next);

        user_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (using_name.getText().toString().equals(""))
                    return;
                Intent intent = new Intent(makeName.this , GroupList.class);
                intent.putExtra("ID", using_name.getText().toString());

                Log.e("text","지은");

                final FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
                mUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {

                        Log.e("text","uid first");
                        if(task.isSuccessful()){

                            Log.e("text","get uid");
                            String idToken=task.getResult().getToken();
                            String uid=mUser.getUid();
                            databaseReference.child("UID").child("uid_group").child(uid).child("name").setValue(using_name.getText().toString());
                            databaseReference.child("UID").child("uid_group").child(uid).child("group").child("1").setValue(1);
                            Log.e("text","token next");
                        }
                        else{
                            Log.e("message","idToken failed");
                        }
                    }
                });
                startActivity(intent);

            }
        });
    }
}