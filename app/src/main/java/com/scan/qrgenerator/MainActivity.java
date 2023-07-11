package com.scan.qrgenerator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.scan.qrgenerator.generator.QrBatchGeneratorActivity;
import com.scan.qrgenerator.scanner.QrScannerActivity;


public class MainActivity extends AppCompatActivity {

    private int REQUEST_CAMERA_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //camera permission check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

    }

    /**
     * button click
     * */
    public void onButtonClicked(View view) {

        String clicked_tag = view.getTag().toString();
        final String tag_qrScan = getString(R.string.tag_qrscanner);
        final String tag_batch = getString(R.string.tag_batch_qr);

        if(clicked_tag.equals(tag_qrScan)){
            //scanner screen
            Intent intent = new Intent(MainActivity.this, QrScannerActivity.class);
            startActivity(intent);

        }else if(clicked_tag.equals(tag_batch)){
            //qr generator screen
            Intent intent = new Intent(MainActivity.this, QrBatchGeneratorActivity.class);
            startActivity(intent);

        }else {
            //Do nothing
        }
    }
}