package com.example.skripsiact;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class forgotpassword extends AppCompatActivity {

    private TextInputEditText input_Email;
    private Button btnresetpass;
    private FirebaseAuth auth;
    private ProgressBar progressBarForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        input_Email = findViewById(R.id.ForgotEmail);
        btnresetpass = findViewById(R.id.resetpass);
        auth = FirebaseAuth.getInstance();
        progressBarForgot = findViewById(R.id.progressBarForgot);
        progressBarForgot.setVisibility(View.GONE);


        btnresetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cekEdit();
            }
        });
    }

    private void cekEdit() {
        final String EdtEmail2 = input_Email.getText().toString();

        if (TextUtils.isEmpty(EdtEmail2))
        {
            Toast.makeText(this, "Input Email", Toast.LENGTH_SHORT).show();
        } else {
            resetpassword(EdtEmail2);
        }
    }


    private void resetpassword(String EdtMail2) {
        auth.sendPasswordResetEmail(EdtMail2)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(forgotpassword.this, "Email terkirim , Cek Email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                            progressBarForgot.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(forgotpassword.this, "Gagal Mengirimkan Email", Toast.LENGTH_SHORT).show();
                            progressBarForgot.setVisibility(View.GONE);
                        }
                    }
                });


    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //set pesan yang akan ditampilkan
        builder.setMessage("Kembali Ke Halaman Login ?");
        //set positive tombol jika menjawab ya
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent backback = new Intent(forgotpassword.this, Login.class);
                startActivity(backback);

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
    }

}



