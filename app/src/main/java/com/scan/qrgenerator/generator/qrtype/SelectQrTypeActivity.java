package com.scan.qrgenerator.generator.qrtype;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;

import com.scan.qrgenerator.R;
import org.jetbrains.annotations.NotNull;

public class SelectQrTypeActivity extends AppCompatActivity {

    private AppCompatButton btn_color_picker;
    private String color_custom_hex;

    private ImageView ivSelectedColorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_qr_type);

        color_custom_hex = "";

        btn_color_picker = findViewById(R.id.btn_pick_color);
        ivSelectedColorView = findViewById(R.id.iv_selected_color);




        btn_color_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Java Code
                new ColorPickerDialog.Builder(SelectQrTypeActivity.this)
                        .setTitle("Pick Theme")
                        .setColorShape(ColorShape.SQAURE)
                        .setColorListener(new ColorListener() {
                            @Override
                            public void onColorSelected(int color, @NotNull String colorHex) {
                                // Handle Color Selection
                                color_custom_hex = colorHex;
                                ivSelectedColorView.setBackgroundColor(color);

                            }
                        })
                        .show();

            }
        });
    }


}