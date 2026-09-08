package com.example.medivault;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class EmergencyQRActivity extends AppCompatActivity {

    private ImageView ivQRCode;
    private TextView tvEmergencyId;
    private MaterialButton btnCopyId;
    private MaterialButton btnRegenerateQR;

    private String emergencyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_emergency_qr);

        initializeViews();
        setupClickListeners();

        generateEmergencyId();
        generateQRCode(emergencyId);
    }

    private void initializeViews() {

        TextView tvBack = findViewById(R.id.tvBack);

        ivQRCode = findViewById(R.id.ivQRCode);
        tvEmergencyId = findViewById(R.id.tvEmergencyId);
        btnCopyId = findViewById(R.id.btnCopyId);
        btnRegenerateQR = findViewById(R.id.btnRegenerateQR);

        tvBack.setOnClickListener(v -> finish());
    }

    private void setupClickListeners() {

        btnCopyId.setOnClickListener(v -> copyEmergencyId());

        btnRegenerateQR.setOnClickListener(v -> {

            generateEmergencyId();
            generateQRCode(emergencyId);

            Toast.makeText(
                    EmergencyQRActivity.this,
                    "Emergency QR updated",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void generateEmergencyId() {

        emergencyId = "MV-" + System.currentTimeMillis();

        tvEmergencyId.setText(emergencyId);
    }

    private void copyEmergencyId() {

        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(
                        Context.CLIPBOARD_SERVICE
                );

        if (clipboard != null) {

            ClipData clip = ClipData.newPlainText(
                    "MediVault Emergency ID",
                    emergencyId
            );

            clipboard.setPrimaryClip(clip);

            Toast.makeText(
                    this,
                    "Emergency ID copied",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void generateQRCode(String data) {

        try {

            BitMatrix bitMatrix =
                    new MultiFormatWriter().encode(
                            data,
                            BarcodeFormat.QR_CODE,
                            700,
                            700
                    );

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {

                int offset = y * width;

                for (int x = 0; x < width; x++) {

                    pixels[offset + x] =
                            bitMatrix.get(x, y)
                                    ? 0xFF000000
                                    : 0xFFFFFFFF;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(
                    width,
                    height,
                    Bitmap.Config.ARGB_8888
            );

            bitmap.setPixels(
                    pixels,
                    0,
                    width,
                    0,
                    0,
                    width,
                    height
            );

            ivQRCode.setImageBitmap(bitmap);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to generate QR code",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
