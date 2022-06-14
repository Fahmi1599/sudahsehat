package com.example.skripsiact;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

public class Regist extends AppCompatActivity {

    //Deklarasi Variable
    private TextInputEditText myEmail, myPassword,userAge,userfullname;
    private Button regButtton;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private TextView haveacc;
    public static FirebaseDatabase database;
    public static DatabaseReference reference,reference1,reference2;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormat;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        //Inisialisasi Widget dan Membuat Objek dari Firebae Authenticaion
        myEmail = findViewById(R.id.regEmail);
        myPassword = findViewById(R.id.regPassword);
        regButtton = findViewById(R.id.regUser);
        progressBar = findViewById(R.id.progressBar);
        userAge = findViewById(R.id.regAge);
        userfullname = findViewById(R.id.regfullname);
        progressBar.setVisibility(View.GONE);
        haveacc = findViewById(R.id.TVLogin);
        auth = FirebaseAuth.getInstance();

        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        userAge.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        haveacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent haveacc = new Intent (Regist.this , Login.class);
                finish();
                startActivity(haveacc);

            }
        });
        


        //Menyembunyikan / Hide Password
        myPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        regButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                cekDataUser();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDateDialog() {
        Calendar calendar = getInstance();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override

            public void onDateSet(DatePicker datePicker, int year, int month, int dayofMonth) {
                Calendar newDate = getInstance();
                newDate.set(year, month, dayofMonth);
                userAge.setText(dateFormat.format(newDate.getTime()));

            }
        },calendar.get(YEAR), calendar.get(MONTH),
                calendar.get(DAY_OF_MONTH));
        datePickerDialog.show();
    }
    private void cekDataUser() {
        //Mendapatkan data yang diinputkan
        final String EdtEmail1 = myEmail.getText().toString();
        final String EdtPassword1 = myPassword.getText().toString();
        final String EdtAge1 = userAge.getText().toString();
        final String EdtFullname1 = userfullname.getText().toString();

        //Mengecek apakah email dan sandi kosong atau tidak
        if(TextUtils.isEmpty(EdtEmail1) || TextUtils.isEmpty(EdtPassword1)){
            Toast.makeText(this, "Email atau Password Dibutuhkan", Toast.LENGTH_SHORT).show();
        }else{
            //Mengecek panjang karakter password baru yang akan didaftarkan
            if(EdtPassword1.length() < 6){
                Toast.makeText(this, "Minimal Password Lebih Dari 6 Karakter", Toast.LENGTH_SHORT).show();
            }else {
                if (EdtAge1.isEmpty())
                    Toast.makeText(this, "Tanggal Lahir Dibutuhkan", Toast.LENGTH_SHORT).show();
                if  (EdtFullname1.isEmpty()){
                    Toast.makeText(this, "Nama Lengkap Dibutuhkan",Toast.LENGTH_SHORT).show();

                }

                progressBar.setVisibility(View.VISIBLE);
                createUserAccount(EdtEmail1 , EdtPassword1 ,EdtFullname1 ,EdtAge1);
            }
        }
    }

    private void createUserAccount(String EdtEmail1, String EdtPassword1 ,String EdtFullname1 , String EdtAge1) {


        auth.createUserWithEmailAndPassword(EdtEmail1, EdtPassword1 )
                .addOnCompleteListener(Regist.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {



                                        FirebaseUser SSuser = auth.getCurrentUser();
                                        assert SSuser != null;
                                        String UserId = SSuser.getUid();
                                        reference = FirebaseDatabase.getInstance().getReference("User").child(UserId);
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("UserId", UserId);
                                        hashMap.put("Fullname", EdtFullname1);
                                        hashMap.put("Age", EdtAge1);
                                        hashMap.put("Email", EdtEmail1);
                                        hashMap.put("Password", EdtPassword1);
                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (SSuser.isEmailVerified()) {

                                                    Toast.makeText(Regist.this, "Email Sudah Tervirifikasi , Silahkan Login", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    // UpdateUI();
                                                } else {
                                                    //Mengecek status keberhasilan saat medaftarkan email dan sandi baru
                                                    SSuser.sendEmailVerification();
                                                    Toast.makeText(Regist.this, "Daftar Akun Berhasi, Cek Email", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.VISIBLE);

                                                    reference1 = FirebaseDatabase.getInstance().getReference("User").child(UserId);
                                                    reference2 = FirebaseDatabase.getInstance().getReference("User").child(UserId);

                                                    reference1.child("Status").setValue("Inactive");
                                                    reference2.child("Valid_Date").setValue("Pending");


                                                    startActivity(new Intent(Regist.this, SetupActivity.class));
                                                    finish();
                                                }

                                            }
                                        });


                                    }
                                }

                    //private void UpdateUI() {
                        //UserHelperClass helperClass = new UserHelperClass(getEmail,getPassword,getAge,getFullname,UserID);
                        //String KeyID = mDatabase.push().getKey();
                        //mDatabase.child("KeyID").setValue(KeyID);
                    //}



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

                Intent backback2 = new Intent(Regist.this, Login.class);
                startActivity(backback2);
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

    //private void UpdateUI() {
        //UserHelperClass helperClass = new UserHelperClass(getEmail,getPassword,getAge,getFullname);

        //mDatabase.child("User").child(userID)

        //String keyID = mDatabase.push().getKey();
        //mDatabase.child(keyID).setValue(helperClass);

 
}



             