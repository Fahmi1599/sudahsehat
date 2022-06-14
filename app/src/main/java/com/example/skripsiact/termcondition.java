package com.example.skripsiact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.SpinKitView;

public class termcondition extends AppCompatActivity {

    Button buttonnext;
    private SpinKitView SK2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termcondition);

        buttonnext = findViewById(R.id.btnnext);
        SK2 = findViewById(R.id.sk2);

        buttonnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toHealthcheckreq = new Intent(termcondition.this, HealthCheckReq.class);
                startActivity(toHealthcheckreq);
                finish();
                SK2.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent tomain = new Intent(termcondition.this, MainMenu.class);
        startActivity(tomain);
        finish();
        SK2.setVisibility(View.VISIBLE);
    }
}