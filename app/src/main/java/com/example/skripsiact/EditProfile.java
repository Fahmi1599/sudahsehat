package com.example.skripsiact;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditProfile extends AppCompatActivity {


    private TextView UserName , UserAge , UserEmail;
    private StorageReference storage;
    private FirebaseDatabase database;
    private String UserId;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String UserId = user.getUid();
        storage = FirebaseStorage.getInstance().getReference();
        reference = database.getReference();
        database = FirebaseDatabase.getInstance();

        UserName = (TextView) findViewById(R.id.NP);
        UserEmail = (TextView) findViewById(R.id.EP);
        UserAge = (TextView) findViewById(R.id.BP);

    }

}