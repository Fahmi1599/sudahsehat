package com.example.skripsiact;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRCodeActivity extends AppCompatActivity {

    ImageView QR;
    public final static int QRoodeWidth = 500;
    Bitmap bitmap;
    Button Save,BackMenu;

    private FirebaseAuth auth;
    public static FirebaseUser User;
    public static FirebaseDatabase database;
    public static DatabaseReference reference;
    public static String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        QR = findViewById(R.id.qr);
        BackMenu = findViewById(R.id.main_menu);
        reference = FirebaseDatabase.getInstance().getReference("User");
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        User = auth.getCurrentUser();
        UserId = User.getUid();

        try {
            bitmap = TextToImageEncode(UserId);
            QR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }


        BackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent BackToMain = new Intent(QRCodeActivity.this, MainMenu.class);
                startActivity(BackToMain);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent BackToMain = new Intent(QRCodeActivity.this, MainMenu.class);
        startActivity(BackToMain);
        finish();
    }
    Bitmap TextToImageEncode (String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE, QRoodeWidth,
                    QRoodeWidth, null
            );
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        for (int y = 0; y < bitMatrixHeight; y++){
            int offset = y * bitMatrixWidth;
            for (int x = 0; x <bitMatrixWidth; x++){
                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

}