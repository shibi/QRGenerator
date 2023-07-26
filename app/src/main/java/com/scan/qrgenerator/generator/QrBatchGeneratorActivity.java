package com.scan.qrgenerator.generator;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.scan.qrgenerator.R;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.stream.IntStream;

public class QrBatchGeneratorActivity extends AppCompatActivity {
    private AppCompatEditText et_prefix, et_idNumber;

    private SeekBar seek_count;
    private LinearLayout ll_progress;

    private AppCompatTextView tv_count, tv_progressCount;
    private AppCompatButton btnGenerate;
    private int count;
    private String idNumberStr, prefix;

    private int WIDTH = 1024;
    private int HEIGHT = 1024;

    private Thread qrSaverThread;

    private boolean isThreadRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_batch_generator);

        et_prefix = findViewById(R.id.et_prefix);
        et_idNumber = findViewById(R.id.et_idnumber);
        seek_count = findViewById(R.id.seekBar);
        tv_count = findViewById(R.id.tv_count);
        ll_progress = findViewById(R.id.ll_progress);
        tv_progressCount = findViewById(R.id.tv_progress_count);

        count = 0;

        seek_count.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_count.setText(""+progress);
                count = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnGenerate = findViewById(R.id.btn_generate);


        //on click
        btnGenerate.setOnClickListener(this::onClickGenerate);
    }

    private void onClickGenerate(View view){
        String str_prefix = et_prefix.getText().toString();
        String str_id = et_idNumber.getText().toString();
        if(str_prefix.isEmpty()){
            showError(et_prefix,"Enter prefix");
            return;
        }

        if(str_id.isEmpty()){
            showError(et_idNumber, "Enter id number");
            return;
        }

        if(count < 1){
            showToast("Enter count");
            return;
        }


        ll_progress.setVisibility(View.VISIBLE);
        createAndSaveQr(str_prefix , str_id);

    }

    private void createAndSaveQr(String prefix_str, String idNumber_str){

        prefix = prefix_str;
        idNumberStr = idNumber_str;

        isThreadRunning = true;
        qrSaverThread = new Thread(qrSaverRunnable);
        qrSaverThread.start();

    }

    private Runnable qrSaverRunnable = new Runnable() {
        @Override
        public void run() {

            String dataToQr;
            long id = Long.parseLong(idNumberStr);
            final int totalCount = count;

            int pIndex;
            for (int i=0; i<= totalCount; i++) {

                if(!isThreadRunning){
                    return;
                }

                dataToQr = prefix + "" + id;
                try {

                    Log.e("-----------","index "+i);

                    Bitmap qrImage = createQRImage(dataToQr, WIDTH, HEIGHT);
                    saveBitmap(QrBatchGeneratorActivity.this,qrImage, Bitmap.CompressFormat.PNG, prefix, ""+id);
                    id++;

                    pIndex = i + 1;
                    updateProgress(pIndex, totalCount);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isThreadRunning = false;
                    ll_progress.setVisibility(View.GONE);
                    showToast("Images saved to folder DCIM/"+prefix);
                }
            });

        }
    };

    private void updateProgress(int percent, int total){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_progressCount.setText(""+percent+"/"+total);
            }
        });
    }

    public static Bitmap createQRImage(final String data, final int width, final int height) throws WriterException {
        final BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, width, height, Collections.singletonMap(EncodeHintType.CHARACTER_SET, "utf-8"));
        return Bitmap.createBitmap(IntStream.range(0, height)
                        .flatMap(h -> IntStream.range(0, width).map(w -> bitMatrix.get(w, h) ? Color.BLACK : Color.TRANSPARENT))
                        .collect(() -> IntBuffer.allocate(width * height), IntBuffer::put, IntBuffer::put)
                        .array(),
                width, height, Bitmap.Config.ARGB_8888);
    }

    private void showError(AppCompatEditText etField, String msg){
        etField.setError(msg);
        etField.requestFocus();
    }

    private void showToast(String msg){
        Toast.makeText(QrBatchGeneratorActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    public Uri saveBitmap(@NonNull final Context context, @NonNull final Bitmap bitmap,
                          @NonNull final Bitmap.CompressFormat format,
                          @NonNull final String prefix, @NonNull final String idNumber) throws IOException {

        final String fileName = prefix + idNumber;
        final String relativeLocation = Environment.DIRECTORY_DCIM + File.separator + prefix;

        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);

        final ContentResolver resolver = context.getContentResolver();
        Uri uri = null;

        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, values);

            if (uri == null)
                throw new IOException("Failed to create new MediaStore record.");

            try (final OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null)
                    throw new IOException("Failed to open output stream.");

                if (!bitmap.compress(format, 100, stream))
                    throw new IOException("Failed to save bitmap.");
            }

            return uri;
        }
        catch (IOException e) {

            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null);
            }

            throw e;
        }
    }


    @Override
    public void onBackPressed() {

        if(isThreadRunning){

            new AlertDialog.Builder(this)
                    .setTitle(R.string.stop_saving)
                    .setMessage(R.string.saving_cancel_msg)
                    .setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert))
                    .setPositiveButton(R.string.label_yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    //Do Something Here
                                    dialog.dismiss();
                                    try {

                                        isThreadRunning = false;
                                        qrSaverThread.interrupt();

                                        return;

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                            })
                    .setNegativeButton(
                            R.string.label_no,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    //Do Something Here

                                    isThreadRunning = true;

                                    dialog.dismiss();
                                }
                            }).show();

        }else {
            super.onBackPressed();
        }

    }
}