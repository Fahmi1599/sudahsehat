package com.example.skripsiact;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserstatsCheck extends AppCompatActivity {

    private SpinKitView SK1;
    private TextView Todays1 , Expired1;
    private String getToday1 , getExpired1;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference , reference1, reference2;
    private String sampleDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userstats_check);

        SK1 = findViewById(R.id.sk1);
        Todays1 = findViewById(R.id.today1);
        Expired1 = findViewById(R.id.expireduser1);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("User");

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String date = df.format(calendar.getTime());
        Todays1.setText(date);


        getExpired1 = Expired1.getText().toString().trim();
        getToday1 = Todays1.getText().toString().trim();

        SK1.setVisibility(View.GONE);

        Intent ShowProfile = new Intent(UserstatsCheck.this, MainMenu.class);
        startActivity(ShowProfile);


        getData();

    }

    private void getData() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("User");

        reference.child(uid).child("Valid_Date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    sampleDate = snapshot.getValue(String.class);
                    SK1.setVisibility(View.GONE);
                    Expired1.setText(sampleDate);

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    try {
                        Date savedDate = dateFormat.parse(sampleDate);
                        System.out.println(savedDate);

                        Date currentDate = dateFormat.parse(dateFormat.format(new Date()));

                        long diff = currentDate.getTime() - savedDate.getTime();
                        long seconds = diff / 1000;
                        long minutes = seconds / 60;
                        long hours = minutes / 60;
                        long days = hours / 24;

                        if (savedDate.before(currentDate))
                        {
                            SK1.setVisibility(View.VISIBLE);

                            Intent ShowProfile = new Intent(UserstatsCheck.this, MainMenu.class);
                            startActivity(ShowProfile);

                            reference1 = FirebaseDatabase.getInstance().getReference("User").child(uid);
                            reference2 = FirebaseDatabase.getInstance().getReference("User").child(uid);
                            reference2.child("Valid_Date").setValue("Pending");
                            reference1.child("Status").setValue("Inactive");

                        }
                        if (savedDate.after(currentDate)){
                            SK1.setVisibility(View.VISIBLE);

                            Intent ShowProfile = new Intent(UserstatsCheck.this, MainMenu.class);
                            startActivity(ShowProfile);

                            reference1 = FirebaseDatabase.getInstance().getReference("User").child(uid);
                            reference1.child("Status").setValue("Active");
                        }
                        if (savedDate.equals(currentDate)){
                            {
                                SK1.setVisibility(View.GONE);

                                Intent ShowProfiles = new Intent(UserstatsCheck.this, MainMenu.class);
                                startActivity(ShowProfiles);
                                reference1 = FirebaseDatabase.getInstance().getReference("User").child(uid);
                                reference1.child("Status").setValue("Active");

                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserstatsCheck.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


}