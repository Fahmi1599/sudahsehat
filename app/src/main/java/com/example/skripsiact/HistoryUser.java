package com.example.skripsiact;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryUser extends AppCompatActivity {

    DatabaseReference reference2,reference3;
    FirebaseUser user;
    List<History> historyList;
    HistoryAdapter historyAdapter;

    ListView listView2;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_user);

        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        String uid = user.getUid();

        //String key = reference2.push().getKey();


        reference2 = FirebaseDatabase.getInstance().getReference().child("History").child(uid);

        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(HistoryUser.this, historyList);

        listView2 = findViewById(R.id.lvlisthistory);

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Log.e("Position",String.valueOf(position));
            }
        });


    }

    @Override
    protected void onStart() {
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                historyList.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    History history = dataSnapshot1.getValue(History.class);
                    historyList.add(history);

                }

                listView2.setAdapter((ListAdapter) historyAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        super.onStart();
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(HistoryUser.this, MainMenu.class);
        startActivity(BackToMain);
        finish();
    }
}