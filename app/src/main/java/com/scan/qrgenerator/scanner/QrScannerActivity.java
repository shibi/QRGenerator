package com.scan.qrgenerator.scanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.scan.qrgenerator.R;

public class QrScannerActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private boolean isScannerInitialized;

    private AppCompatTextView tv_scannedId;

    private int REQUEST_CAMERA_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);


        tv_scannedId = findViewById(R.id.tv_scannedId);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            //initialize qr scanner
            initQRScanner();

        }else {
            isScannerInitialized = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isScannerInitialized) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {

        if (isScannerInitialized) {
            mCodeScanner.releaseResources();
        }

        super.onPause();
    }

    /**
     * initialize QR scanner
     * */
    private void initQRScanner(){
        try {

            //set flag true to identify scanner initialized
            isScannerInitialized = true;
            //get initialize scanner view
            CodeScannerView scannerView = findViewById(R.id.scanner_view);
            mCodeScanner = new CodeScanner(this, scannerView);

            //add decode callback
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //get scanned data
                                String scannedMemberId = result.getText();

                                tv_scannedId.setText(scannedMemberId);

                                //show child as toast
                                //showToast(result.getText());

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //check request code
        if(requestCode == REQUEST_CAMERA_PERMISSION){
            //check permission granted
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //initialize scanner
                initQRScanner();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //release scanner object for garbage collection
        if(isScannerInitialized){
            if(mCodeScanner!=null){
                mCodeScanner = null;
            }
        }
    }
}