package com.example.skripsiact;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.skripsiact.rangedialog.PickerDialog;
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

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class InputDataCov extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECTION = 2;
    int PICK_IMAGE = 1000;

    private static final String TAG = "InputDataCov";
    private ImageView uploadimage;
    private Button uploadbutton, selectbutton , selectPdf;
    private TextView tvProgress , tvNamePdf , choosefile;
    private EditText InputData;
    private final int PICK_PDF_CODE = 2342;
    private FirebaseStorage storage;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    Uri filePath = null;
    Uri filepath = null;

    Uri imgPath = null;
    String download_url = null;
    Bitmap bm, converetdImage, centerBitmap;
    ByteArrayOutputStream bytearrayoutputstream = null;
    String path = null;
    ProgressDialog progressDialog;


    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data_cov);

        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        String uid = user.getUid();
        String UserEmail = user.getEmail();

        uploadimage = findViewById(R.id.uploadimage);
        uploadbutton = findViewById(R.id.uploadbtn);
        InputData = findViewById(R.id.DateInput);
        choosefile = findViewById(R.id.text_choose);


        choosefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_image();
            }
        });

        InputData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date tempStart=new Date(),tempEnd=new Date();
                try {
                    tempStart = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2020");
                    tempEnd = new SimpleDateFormat("dd-MM-yyyy").parse("31-12-2025");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                PickerDialog dialog=new PickerDialog(InputDataCov.this);
                dialog.setDateRanges(tempStart,tempEnd);
                dialog.setMaxDateRange(tempEnd,true);
                dialog.setMinDateRange(tempStart,true);
                dialog.setMidDrawable(R.drawable.crfd);
                dialog.showPicker();
                dialog.setRangeSelected(new PickerDialog.OnRangeSelect() {
                    @Override
                    public void OnSelect(Date StartDate, Date EndDate) {
                        Toast.makeText(getApplicationContext(),StartDate.toString()+""+EndDate.toString(),Toast.LENGTH_SHORT).show();
                        InputData.setText(StartDate.toString()+"" +EndDate.toString());
                    }
                });

            }
        });

        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_image();
            }
            private String formatDate(Date dateObject) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                return timeFormat.format(dateObject);
            }

            private void upload_image() {

                if (filePath  != null) {

                    progressDialog = new ProgressDialog(InputDataCov.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();


                    final StorageReference sRef = storageReference.child("Health Check User/" + "User_" + System.currentTimeMillis());

                    sRef.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    progressDialog.dismiss();
                                    Toast.makeText(InputDataCov.this, "Image/File Uploaded!!", Toast.LENGTH_SHORT).show();

                                    sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            if (task.isSuccessful()) {
                                                final String download_url = task.getResult().toString();
                                                final String DataRange = InputData.getText().toString();


                                                database = FirebaseDatabase.getInstance();
                                                reference = database.getReference().child("Validation Data").child(uid);
                                                HashMap<String, String> hashMap = new HashMap<>();
                                                hashMap.put("Email" , UserEmail);
                                                hashMap.put("UserId", uid);
                                                hashMap.put("Date Range", DataRange);
                                                hashMap.put("Health Check User", download_url);
                                                reference.setValue(hashMap);

                                                Log.e(TAG, "onComplete:========>>><<<<" + download_url);
                                                final String push_key = reference.push().getKey().toString();
                                                reference.getRoot().child("Health Check User").child(push_key).setValue(download_url);
                                                Glide.with(InputDataCov.this)
                                                        .load(download_url);
                                                Intent gotorespond = new Intent (InputDataCov.this , RespondActivity.class);
                                                finish();
                                                startActivity(gotorespond);
                                            }
                                        }
                                    });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(InputDataCov.this, "File Upload Fail!" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e(TAG, "onFailure:====>>><<<" + e.getMessage());
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

        });
    }

    private void choose_image() {
        String[] saveOptions = {"Take a Photo", "Select From Gallery" , " Select PDF"};
        AlertDialog.Builder builder = new AlertDialog.Builder(InputDataCov.this);
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

                if (selected == 2) {
                    Intent selectPdf = new Intent();
                    selectPdf.setType("application/pdf");
                    selectPdf.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(selectPdf, "Select Document"), PICK_PDF_CODE);

                }

            }
        }).setCancelable(false);
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case 1000:
                    filePath = data.getData();
                    uploadimage.setImageURI(filePath);
                    //bytearrayoutputstream = null;
                    //imgPath = getResizedBitmap(uploadimage);
                    break;
            }
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                uploadimage.setImageBitmap(imageBitmap);
                filePath = data.getData();
                uploadimage.setImageURI(filePath);
               // bytearrayoutputstream = null;
                //imgPath = getResizedBitmap(uploadimage);
            }
            if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
                if (data.getData() != null) {
                    filePath = data.getData();
                    uploadimage.setImageDrawable(getDrawable(R.drawable.logopdf));

                } else
                    Toast.makeText(this, "NO FILE CHOSEN", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(this, "Image/File Not Selected!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }


    public Uri getResizedBitmap(ImageView imageview) {

        //get bitmap from uri
        bm = ((BitmapDrawable) imageview.getDrawable()).getBitmap();

        //fitxy the bitmap
        int dimension = Math.min(bm.getWidth(), bm.getHeight());
        centerBitmap = ThumbnailUtils.extractThumbnail(bm, dimension, dimension);

        // scale the bitmap
        converetdImage = Bitmap.createScaledBitmap(centerBitmap, 500, 500, true);
        path = null;

        //convert bitmap to uri
        path = MediaStore.Images.Media.insertImage(getContentResolver(), converetdImage, "title", null);
        return Uri.parse(path);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(InputDataCov.this, MainMenu.class);
        startActivity(BackToMain);
        finish();
    }
}