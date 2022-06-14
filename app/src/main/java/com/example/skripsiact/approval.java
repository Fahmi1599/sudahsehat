package com.example.skripsiact;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skripsiact.adminpage.AdminActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class approval extends AppCompatActivity {

    DatabaseReference databaseReference , databaseReference1;
    List<ValidationTest> validationTestList;
    CustomAdapter customAdapter;

    ListView listView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_verification_data);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("Validation Data Test");

        validationTestList = new ArrayList<>();
        customAdapter = new CustomAdapter(approval.this,validationTestList);



        listView = findViewById(R.id.lvlistaproval);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Log.e("Position",String.valueOf(position));
            }
        });


    }

    @Override
    protected void onStart() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                validationTestList.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    ValidationTest validationTest = dataSnapshot1.getValue(ValidationTest.class);
                    validationTestList.add(validationTest);

                }

                listView.setAdapter(customAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        super.onStart();
    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(approval.this, AdminActivity.class);
        startActivity(BackToMain);
        finish();
    }




}
