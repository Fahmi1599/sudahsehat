package com.example.skripsiact;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECTION = 2;
    int PICK_IMAGE = 1000;
    private StorageReference mStorageRef;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private CircleImageView ProfilPic , EditProfilePic;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private Bitmap bitmap;
    private Button UpProfPic;
    Uri filePath = null;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ProfilPic = (CircleImageView) findViewById(R.id.setup_image);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        user = auth.getCurrentUser();
        String uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        mStorageRef = storage.getReference();
        UpProfPic = findViewById(R.id.setup_btn);
        reference = database.getReference("User");
        EditProfilePic = findViewById(R.id.addpic);


        EditProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Choose_Profile();
            }
        });

        UpProfPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImageInStorageDataBase();
            }
        });


    }

    private void UploadImageInStorageDataBase() {
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        user = auth.getCurrentUser();
        String uid = user.getUid();
        database = FirebaseDatabase.getInstance();
        mStorageRef = storage.getReference();

        if (filePath  != null){

            progressDialog = new ProgressDialog(SetupActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference FilePath = mStorageRef.child("users_image").child(uid + "jpg");
            FilePath.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(SetupActivity.this, "Profil Pic Uploaded", Toast.LENGTH_SHORT).show();

                            FilePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    final String ProfilPic_url = task.getResult().toString();
                                    database = FirebaseDatabase.getInstance();
                                    final String push_key = reference.push().getKey().toString();
                                    reference.getRoot().child("ProfileUser").child(push_key).setValue(ProfilPic_url);
                                    reference = database.getReference().child("User").child(uid);
                                    Glide.with(SetupActivity.this)
                                            .load(ProfilPic_url);
                                    reference.child("Profil_Pic").setValue(ProfilPic_url);
                                    Intent finishprof = new Intent (SetupActivity.this , Login.class);
                                    finish();
                                    startActivity(finishprof);
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SetupActivity.this, "File Upload Fail!" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(@NotNull UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setMessage("Uploading " + taskSnapshot.getBytesTransferred() / 1024 + " / " + taskSnapshot.getTotalByteCount() / 1024 + " KB");
                        }
                    });


        }

    }

    private void Choose_Profile() {
        String[] saveOptions = {"Take a Photo", "Select From Gallery" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SetupActivity.this);
        builder.setTitle("Select a Photo");
        builder.setItems(saveOptions, new DialogInterface.OnClickListener() {
                    @SuppressLint("QueryPermissionsNeeded")
                    @Override
                    public void onClick(DialogInterface dialog, int selected) {

                        if (selected == 0) {
                            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePhotoIntent.resolveActivity(getPackageManager()) != null) ;
                            try {
                                startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
                            } catch (ActivityNotFoundException e) {
                                // display error state to the user
                            }

                        }
                        if (selected == 1) {
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
                            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
                        }




                    }

                }
        ).setCancelable(false);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case 1000:
                    filePath = data.getData();
                    ProfilPic.setImageURI(filePath);
                    //bytearrayoutputstream = null;
                    //imgPath = getResizedBitmap(uploadimage);
                    break;
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ProfilPic.setImageBitmap(imageBitmap);
                filePath = data.getData();
                ProfilPic.setImageURI(filePath);
                // bytearrayoutputstream = null;
                //imgPath = getResizedBitmap(uploadimage);
            }
        } else {
            Toast.makeText(this, "Image/File Not Selected!", Toast.LENGTH_SHORT).show();
            return;
        }
    }


}