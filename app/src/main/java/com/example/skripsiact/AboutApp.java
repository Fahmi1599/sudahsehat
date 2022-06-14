package com.example.skripsiact;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skripsiact.adminpage.AdminActivity;

public class AboutApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

    }

    @Override
    public void onBackPressed() {
        Intent keluar = new Intent(AboutApp.this, AdminActivity.class);
        startActivity(keluar);
    }
}