package com.example.medivault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQRActivity extends AppCompatActivity {

    private TextView tvBack;
    private TextView tvScanResult;
    private TextView tvScanStatus;

    private MaterialButton btnScanQR;
    private MaterialButton btnViewEmergencyInfo;

    private MaterialCardView cardScanResult;

    private String scannedEmergencyId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan_qr);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {

        tvBack = findViewById(R.id.tvBack);
        tvScanResult = findViewById(R.id.tvScanResult);
        tvScanStatus = findViewById(R.id.tvScanStatus);

        btnScanQR = findViewById(R.id.btnScanQR);
        btnViewEmergencyInfo = findViewById(R.id.btnViewEmergencyInfo);

        cardScanResult = findViewById(R.id.cardScanResult);
    }

    private void setupClickListeners() {

        tvBack.setOnClickListener(v -> finish());

        btnScanQR.setOnClickListener(v -> startQRScanner());

        btnViewEmergencyInfo.setOnClickListener(v -> {

            if (scannedEmergencyId.isEmpty()) {

                Toast.makeText(
                        ScanQRActivity.this,
                        "Please scan an Emergency QR first",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Intent intent = new Intent(
                    ScanQRActivity.this,
                    EmergencyInfoActivity.class
            );

            intent.putExtra(
                    "emergencyId",
                    scannedEmergencyId
            );

            startActivity(intent);
        });
    }

    private void startQRScanner() {

        IntentIntegrator integrator =
                new IntentIntegrator(this);

        integrator.setDesiredBarcodeFormats(
                IntentIntegrator.QR_CODE
        );

        integrator.setPrompt(
                "Scan MediVault Emergency QR"
        );

        integrator.setCameraId(0);

        integrator.setBeepEnabled(true);

        integrator.setBarcodeImageEnabled(false);

        integrator.setOrientationLocked(false);

        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        IntentResult result =
                IntentIntegrator.parseActivityResult(
                        requestCode,
                        resultCode,
                        data
                );

        if (result != null) {

            if (result.getContents() == null) {

                Toast.makeText(
                        ScanQRActivity.this,
                        "QR scan cancelled",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                handleScanResult(
                        result.getContents()
                );
            }

            return;
        }

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );
    }

    private void handleScanResult(String scannedData) {

        if (scannedData == null) {
            return;
        }

        scannedData = scannedData.trim();

        if (scannedData.startsWith("MV-")) {

            scannedEmergencyId = scannedData;

            tvScanResult.setText(
                    scannedEmergencyId
            );

            tvScanStatus.setText(
                    "Valid MediVault Emergency QR detected"
            );

            cardScanResult.setVisibility(
                    View.VISIBLE
            );

            btnViewEmergencyInfo.setVisibility(
                    View.VISIBLE
            );

            Toast.makeText(
                    ScanQRActivity.this,
                    "Emergency QR scanned successfully",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            scannedEmergencyId = "";

            tvScanResult.setText(
                    "Invalid QR Code"
            );

            tvScanStatus.setText(
                    "This QR code is not a valid MediVault Emergency ID"
            );

            cardScanResult.setVisibility(
                    View.VISIBLE
            );

            btnViewEmergencyInfo.setVisibility(
                    View.GONE
            );

            Toast.makeText(
                    ScanQRActivity.this,
                    "Invalid MediVault QR code",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}