package com.example.skripsiact;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;




public class MainMenu extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECTION = 2;
    int PICK_IMAGE = 1000;

    private FirebaseUser user;
    private FirebaseAuth auth;
    private DatabaseReference reference,reference1,reference2;
    private Button UploadBerkas,QrCode,History;
    private String getStatus,getDateExpired,getToday;
    private TextView greetingTV, RapidStatus, DateExpired,Today;
    private ImageView EditProfile;
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private CircleImageView ProfilPic;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        greetingTV = findViewById(R.id.greeting);
        RapidStatus = findViewById(R.id.statushc);
        DateExpired = findViewById(R.id.DateExp);
        EditProfile = findViewById(R.id.editpoto);
        Today = findViewById(R.id.testtest);

        ProfilPic = (CircleImageView) findViewById(R.id.profilpic);
        UploadBerkas = findViewById(R.id.UploadBerkas);
        QrCode = findViewById(R.id.QRCodeButton);
        History = findViewById(R.id.HistoryUser);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        user = auth.getCurrentUser();
        String uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("User");
        mStorageRef = storage.getReference();


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String date = df.format(calendar.getTime());
        Today.setText(date);

        //TestDate();
        getToday  = Today.getText().toString().trim();
        getStatus = RapidStatus.getText().toString().trim();
        getDateExpired = DateExpired.getText().toString().trim();

        //  0 comes when two date are same,
        //  1 comes when date1 is higher then date2
        // -1 comes when date1 is lower then date2
        getProfileData();






        //StatusDate();



        reference.child(uid).child("Valid_Date").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String DateExp = snapshot.getValue(String.class);
                System.out.println(DateExp);
                DateExpired.setText(DateExp);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(MainMenu.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        reference.child(uid).child("Status").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                System.out.println(status);
                RapidStatus.setText(status);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(MainMenu.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
        //Mendapatkan UserID dari akun yang terautentikasi
        reference.child(uid).child("Fullname").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Nama = snapshot.getValue(String.class);
                System.out.println(Nama);
                greetingTV.setText("Hai, "+ Nama);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child(uid).child("Profil_pic").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(MainMenu.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });


        QrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusButton();
            }
        });

        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ShowProfile = new Intent (MainMenu.this , Setupdimain.class);
                finish();
                startActivity(ShowProfile);
            }
        });
        UploadBerkas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadTime();
            }
        });
        History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toHistory = new Intent (MainMenu.this , HistoryUser.class);
                finish();
                startActivity(toHistory);
            }
        });
    }

    private void TestDate() {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        //String toDate = getDateExpired;
        //String toDayy = getToday;
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        user = auth.getCurrentUser();
        String uid = user.getUid();

        try {
            Date todayys = df.parse(getToday);
            Date Expireds = df.parse(getDateExpired);
            if (getToday.compareTo(getDateExpired) >= 0)
            {
                reference1 = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Health_Status");
                reference2 = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Health_Status");

                reference1.child("Status").setValue("Inactive");
                reference2.child("Valid_Date").setValue("Pending");

            }
        } catch (ParseException e) {

            e.printStackTrace();
        }


    }

    private void StatusDate() {
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        user = auth.getCurrentUser();
        String uid = user.getUid();
        if (getToday.compareTo(getDateExpired) >= 0) {

            reference1 = FirebaseDatabase.getInstance().getReference("User").child(uid);
            reference2 = FirebaseDatabase.getInstance().getReference("User").child(uid);

            reference1.child("Status").setValue("Inactive");
            reference2.child("Valid_Date").setValue("Pending");
        }


    }

    private void getProfileData() {
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        user = auth.getCurrentUser();
        String uid = user.getUid();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = root.child("User").child(uid);
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String Image = snapshot.child("Profil_Pic").getValue().toString();
                    if (Image.equals("default")) {
                        Picasso.get().load(R.drawable.post_placeholder).into(ProfilPic);
                    } else
                        Picasso.get().load(Image).placeholder(R.drawable.post_placeholder).into(ProfilPic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        m.addListenerForSingleValueEvent(valueEventListener);
    }


    private void UploadTime() {
        getStatus = RapidStatus.getText().toString().trim();

        if (getStatus.equals("Inactive"))
        {
            Intent InputDataCov = new Intent (MainMenu.this , termcondition.class);
            finish();
            startActivity(InputDataCov);
        }

        if (getStatus.equals("Diproses"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            //set pesan yang akan ditampilkan
            builder.setMessage("Data Sedang Di Proses");
            //set positive tombol jika menjawab ya
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        if (getStatus.equals("Active"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            //set pesan yang akan ditampilkan
            builder.setMessage("Akun Aktif, Silahkan Akses QR Code");
            //set positive tombol jika menjawab ya
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    private void StatusButton() {

        getStatus = RapidStatus.getText().toString().trim();

        if (getStatus.equals("Active"))
        {
            Intent MenuUp = new Intent (MainMenu.this , DetectorActivity.class);
            startActivity(MenuUp);
            Toast.makeText(MainMenu.this, "Gunakan Masker", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (getStatus.equals("Inactive"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            //set pesan yang akan ditampilkan
            builder.setMessage("Upload Berkas Kesehatan Covid-19");
            //set positive tombol jika menjawab ya
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        if (getStatus.equals("Wait Admin Approval"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            //set pesan yang akan ditampilkan
            builder.setMessage("Data Sedang Di Proses");
            //set positive tombol jika menjawab ya
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    //onBackPressed adalah fungsi yang akan dieksekusi saat menekan tombol kembali
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
                Intent keluar = new Intent(MainMenu.this, Login.class);
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




    }


}
