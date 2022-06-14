package com.example.skripsiact.adminpage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skripsiact.AboutApp;
import com.example.skripsiact.Login;
import com.example.skripsiact.R;
import com.example.skripsiact.approval;
import com.example.skripsiact.userlist;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    Button goToUserList,goToApproval;
    ImageView infoapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        goToUserList = findViewById(R.id.btnuserall);
        goToApproval = findViewById(R.id.approvalData);
        infoapp = findViewById(R.id.infoapp);

        goToApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Approval = new Intent(AdminActivity.this, approval.class);
                finish();
                startActivity(Approval);
            }
        });


        goToUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent zzz = new Intent(AdminActivity.this, userlist.class);
                finish();
                startActivity(zzz);

            }
        });

        infoapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent zz = new Intent(AdminActivity.this, AboutApp.class);
                finish();
                startActivity(zz);

            }
        });
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //set pesan yang akan ditampilkan
        builder.setMessage("Anda Yakin Ingin Logout ?");
        //set positive tombol jika menjawab ya
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                FirebaseAuth.getInstance().signOut();
                Intent keluar = new Intent(AdminActivity.this, Login.class);
                startActivity(keluar);

            }
        });
        //set negative tombol jika menjawab tidak
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //jika menekan tombol tidak, maka kalian akan tetap berada di activity saat ini
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
}}