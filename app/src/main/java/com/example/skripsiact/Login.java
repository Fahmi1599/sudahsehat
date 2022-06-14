package com.example.skripsiact;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skripsiact.adminpage.AdminActivity;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button  Login;
    private TextInputEditText myEmail, myPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String getEmail, getPassword;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private TextView forgotpass , Register;
    private Boolean emailAddressChecker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        myEmail = findViewById(R.id.getEmail);
        myPassword = findViewById(R.id.getPassword);
        Register = findViewById(R.id.toregist);
        Register.setOnClickListener(this);
        Login = findViewById(R.id.login);
        Login.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        database = FirebaseDatabase.getInstance();
        forgotpass = findViewById(R.id.forgotpasslogin);

        Sprite fadingcircle = new FadingCircle();


        //Menyembunyikan / Hide Password
        myPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        //Instance / Membuat Objek Firebase Authentication
        auth = FirebaseAuth.getInstance();

       /// if(//auth.getCurrentUser() ! = null){
            //startActivity(new Intent(getApplicationContext(), MainMenu.class));
        //}
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toforgot = new Intent (Login.this , forgotpassword.class);
                finish();
                startActivity(toforgot);
            }
        });

        overridePendingTransition(0,0);
        View layout =findViewById(R.id.emailForm);
        Animation animation= AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        layout.startAnimation(animation);

    }


    //Menerapkan Lestener
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
   }

    //Method ini digunakan untuk proses autentikasi user menggunakan email dan kata sandi
    private void loginUserAccount() {
        auth.signInWithEmailAndPassword(getEmail, getPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Mengecek status keberhasilan saat login
                        if (task.isSuccessful())
                        {
                            if (getEmail.equals("admin@gmail.com")&&getPassword.equals("password"))
                            {
                                Intent i = new Intent(Login.this , AdminActivity.class);
                                startActivity(i);
                                progressBar.setVisibility(View.GONE);
                            } else {
                                final FirebaseUser user = task.getResult().getUser();
                                if (user.isEmailVerified()) {
                                    Sprite fadingcircle = new FadingCircle();
                                    Intent i = new Intent(Login.this, UserstatsCheck.class);
                                    startActivity(i);
                                    progressBar.setIndeterminateDrawable(fadingcircle);
                                    Toast.makeText(Login.this, "Berhasil Masuk ", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(Login.this , "Cek Email Verifikasi ",Toast.LENGTH_SHORT).show();
                                }
                            }

                        }  else {
                            Toast.makeText(Login.this, "Gagal , Coba Lagi !! ", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Gagal , Coba Lagi !! ", Toast.LENGTH_SHORT).show();

            }
        });
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.toregist:
                startActivity(new Intent(Login.this, Regist.class));
                break;

            case R.id.login:
                //Mendapatkan dat yang diinputkan User
                getEmail = myEmail.getText().toString().trim();
                getPassword = myPassword.getText().toString().trim();

                //Mengecek apakah email dan sandi kosong atau tidak
                if (TextUtils.isEmpty(getEmail) || TextUtils.isEmpty(getPassword)) {
                    Toast.makeText(this, "Email or Password Required !!", Toast.LENGTH_SHORT).show();
                }
                else {
                    loginUserAccount();
                }


                break;
        }

    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //set pesan yang akan ditampilkan
        builder.setMessage("Anda Yakin Ingin Keluar Aplikasi ?");
        //set positive tombol jika menjawab ya
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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