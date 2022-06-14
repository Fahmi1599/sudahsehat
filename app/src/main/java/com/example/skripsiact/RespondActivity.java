package com.example.skripsiact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RespondActivity extends AppCompatActivity {

    private TextView backmenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond);

        backmenu = findViewById(R.id.tvresponback);

        backmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backrespond = new Intent (RespondActivity.this , MainMenu.class);
                finish();
                startActivity(backrespond);
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent backrespond1 = new Intent (RespondActivity.this , MainMenu.class);
        finish();
        startActivity(backrespond1);

    }
}