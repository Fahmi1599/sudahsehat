package com.example.skripsiact;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

public class HealthCheckReq extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECTION = 2;

    private TextInputEditText vaccineDateEditInput;
    private TextInputEditText immuneUntilEditInput;
    private TextInputEditText fullNameEditInput;

    private TextInputLayout VaccineTypeInput;
    private AutoCompleteTextView VaccineTypeEditInput;

    ArrayList<String> arrayList_Type;
    ArrayAdapter<String> arrayAdapter_Type;


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
    private FirebaseDatabase database,database1;
    private DatabaseReference reference,reference1,reference2;
    private DatePickerDialog datePickerDialog;
    Uri filePath = null;
    Uri filepath = null;

    Uri imgPath = null;
    String download_url = null;
    Bitmap bm, converetdImage, centerBitmap;
    ByteArrayOutputStream bytearrayoutputstream = null;
    String path = null;
    ProgressDialog progressDialog;
    private SimpleDateFormat dateFormat;

    public HealthCheckReq() {
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_appointment);

        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        String uid = user.getUid();
        String UserEmail = user.getEmail();

        uploadimage = findViewById(R.id.uploadimage1);
        uploadbutton = findViewById(R.id.createPass);
        choosefile = findViewById(R.id.text_choose1);
        vaccineDateEditInput = findViewById(R.id.createPassportVaccineDateEditInput);
        immuneUntilEditInput = findViewById(R.id.createPassportImmuneUntilEditInput);
        fullNameEditInput = findViewById(R.id.createPassportFullNameEditInput);
        VaccineTypeInput = (TextInputLayout)findViewById(R.id.createPassportVaccineTypeInput);
        VaccineTypeEditInput =(AutoCompleteTextView)findViewById(R.id.createPassportVaccineTypeEditInput);

        arrayList_Type = new ArrayList<>();
        arrayList_Type.add("Swab Antigen");
        arrayList_Type.add("Swab PCR");
        arrayList_Type.add("Genose");

        final String EdtVaccineType = VaccineTypeEditInput.getText().toString();
        final String EdtStartDate = vaccineDateEditInput.getText().toString();




        arrayAdapter_Type=new ArrayAdapter<>(getApplicationContext(),R.layout.tv_dd,arrayList_Type);
        VaccineTypeEditInput.setAdapter(arrayAdapter_Type);

        VaccineTypeEditInput.setThreshold(1);

        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        vaccineDateEditInput.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });

        choosefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_image();
            }
        });


        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                check_req();
                uploaddata();
            }
    });}

    private void check_req() {
        final String EdtFullnameUp = fullNameEditInput.getText().toString();
        final String EdtVaccineType = VaccineTypeEditInput.getText().toString();
        final String EdtStartDate = vaccineDateEditInput.getText().toString();
        final String EdtUntil = immuneUntilEditInput.getText().toString();
        //Mengecek apakah email dan sandi kosong atau tidak
        if(TextUtils.isEmpty(EdtFullnameUp) ){
            Toast.makeText(this, "Fullname Required", Toast.LENGTH_SHORT).show();
                }
                {
                if (EdtVaccineType.isEmpty())
                    Toast.makeText(this, "Input Vaccine Type", Toast.LENGTH_SHORT).show();
                if  (EdtStartDate.isEmpty())
                {
                    Toast.makeText(this, "Input Start Date",Toast.LENGTH_SHORT).show();
                }
                if (EdtUntil.isEmpty())
                    Toast.makeText(this, "Input Until Date",Toast.LENGTH_SHORT).show();
                }
    }

    private void uploaddata() {
        if (filePath  != null) {

            progressDialog = new ProgressDialog(HealthCheckReq.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();



            final StorageReference sRef = storageReference.child("Health Check User/" + "User_" + System.currentTimeMillis());

            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            Toast.makeText(HealthCheckReq.this, "Image/File Uploaded!!", Toast.LENGTH_SHORT).show();

                            sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    final String download_url = task.getResult().toString();

                                    if (task.isSuccessful()) {
                                        storageReference = FirebaseStorage.getInstance().getReference();
                                        auth = FirebaseAuth.getInstance();
                                        storage = FirebaseStorage.getInstance();
                                        user = auth.getCurrentUser();
                                        String uid = user.getUid();
                                        String UserEmail = user.getEmail();

                                        final String EdtFullnameUp = fullNameEditInput.getText().toString();
                                        final String EdtVaccineType = VaccineTypeEditInput.getText().toString();
                                        final String EdtStartDate = vaccineDateEditInput.getText().toString();
                                        final String EdtUntil = immuneUntilEditInput.getText().toString();


                                        database = FirebaseDatabase.getInstance();
                                        reference2 = database.getReference().child("History").child(uid);
                                        HashMap<String, String> hashMap2 = new HashMap<>();
                                        hashMap2.put("Email" , UserEmail);
                                        hashMap2.put("UserId", uid);
                                        hashMap2.put("Fullname" , EdtFullnameUp);
                                        hashMap2.put("Start_Date" , EdtStartDate);
                                        hashMap2.put("End_Date" , EdtUntil);
                                        hashMap2.put("Type_Vaccine" , EdtVaccineType);
                                        hashMap2.put("Health_Check_User", download_url);
                                        reference2.push().setValue(hashMap2);

                                        database = FirebaseDatabase.getInstance();
                                        reference = database.getReference().child("Validation Data Test").child(uid);
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("Email" , UserEmail);
                                        hashMap.put("UserId", uid);
                                        hashMap.put("Fullname" , EdtFullnameUp);
                                        hashMap.put("Start_Date" , EdtStartDate);
                                        hashMap.put("End_Date" , EdtUntil);
                                        hashMap.put("Type_Vaccine" , EdtVaccineType);
                                        hashMap.put("Health_Check_User", download_url);
                                        reference.setValue(hashMap);

                                        Log.e(TAG, "onComplete:========>>><<<<" + download_url);
                                        final String push_key = reference.push().getKey().toString();
                                        reference.getRoot().child("Health Check User").child(push_key).setValue(download_url);
                                        Glide.with(HealthCheckReq.this)
                                                .load(download_url);
                                        reference1 = database.getReference().child("User").child(uid);
                                        reference1.child("Status").setValue("Diproses");
                                        reference1.child("Start_Date").setValue(EdtStartDate);
                                        reference1.child("End_Date").setValue(EdtUntil);
                                        reference1.child("Type_Vaccine").setValue(EdtVaccineType);
                                        reference1.child("Health_Check_User").setValue(download_url);

                                        Intent gotorespond = new Intent (HealthCheckReq.this , RespondActivity.class);
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
                            Toast.makeText(HealthCheckReq.this, "File Upload Fail!" + e.getMessage(), Toast.LENGTH_LONG).show();
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






    private void showImmuneDialog() {


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showInputDialog() {
        Calendar calendar = getInstance();
        final String EdtVaccineType = VaccineTypeEditInput.getText().toString();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override

            public void onDateSet(DatePicker datePicker, int year, int month, int dayofMonth) {
                Calendar InputDate = getInstance();
                InputDate.set(year, month, dayofMonth);
                vaccineDateEditInput.setText(dateFormat.format(InputDate.getTime()));

                if (EdtVaccineType.equals("Swab PCR"))
                {
                    dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

                    InputDate.add(DAY_OF_MONTH,2 );
                    immuneUntilEditInput.setText(dateFormat.format(InputDate.getTime()));
                }

                if (EdtVaccineType.equals("Swab Antigen"))
                {
                    dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

                    InputDate.add(DAY_OF_MONTH,1 );
                    immuneUntilEditInput.setText(dateFormat.format(InputDate.getTime()));
                }
                if (EdtVaccineType.equals("Genose"))
                {
                    dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

                    InputDate.add(DAY_OF_MONTH,1 );
                    immuneUntilEditInput.setText(dateFormat.format(InputDate.getTime()));
                }







            }
        },calendar.get(YEAR), calendar.get(MONTH),
                calendar.get(DAY_OF_MONTH));
        datePickerDialog.show();


    }

    private void choose_image() {
        String[] saveOptions = {"Take a Photo", "Select From Gallery" , " Select PDF"};
        AlertDialog.Builder builder = new AlertDialog.Builder(HealthCheckReq.this);
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
        Intent BackToMain = new Intent(HealthCheckReq.this, MainMenu.class);
        startActivity(BackToMain);
        finish();
    }
}