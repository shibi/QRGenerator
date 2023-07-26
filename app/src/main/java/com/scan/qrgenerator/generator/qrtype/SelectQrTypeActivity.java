package com.scan.qrgenerator.generator.qrtype;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;

import com.scan.qrgenerator.R;
import com.scan.qrgenerator.generator.Constants;
import com.scan.qrgenerator.generator.QrBatchGeneratorActivity;

import org.jetbrains.annotations.NotNull;

public class SelectQrTypeActivity extends AppCompatActivity {

    private AppCompatButton btn_color_picker;
    private String color_custom_hex;
    private int backgroundColor;


    private ImageView ivSelectedColorView;
    private LinearLayout ll_pick_color;
    private ImageView iv_tick1, iv_tick2;
    private CardView cv_white, cv_black;

    private RadioGroup radioGroup;
    private int qrColor;
    private int qrType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_qr_type);

        btn_color_picker = findViewById(R.id.btn_pick_color);
        ivSelectedColorView = findViewById(R.id.iv_selected_color);
        ll_pick_color = findViewById(R.id.ll_pick_color);
        radioGroup = findViewById(R.id.radioGrop);
        iv_tick1 = findViewById(R.id.iv_tick_1);
        iv_tick2 = findViewById(R.id.iv_tick_2);
        cv_white = findViewById(R.id.cv_qr_type1);
        cv_black = findViewById(R.id.cv_qr_type2);

        //init values
        color_custom_hex = "";
        backgroundColor = Color.TRANSPARENT;
        radioGroup.check(R.id.rb_tr);
        qrColor = Color.WHITE;
        iv_tick1.setVisibility(View.VISIBLE);
        iv_tick2.setVisibility(View.GONE);
        qrType = 1;



        ll_pick_color.setOnClickListener(v -> {
            if(ll_pick_color.getVisibility() == View.VISIBLE){
                ll_pick_color.setVisibility(View.GONE);
            }else {
                ll_pick_color.setVisibility(View.VISIBLE);
            }
        });

        btn_color_picker.setOnClickListener(v -> {
            // Java Code
            new ColorPickerDialog.Builder(SelectQrTypeActivity.this)
                    .setTitle("Pick Theme")
                    .setColorShape(ColorShape.CIRCLE)
                    .setColorListener(new ColorListener() {
                        @Override
                        public void onColorSelected(int color, @NotNull String colorHex) {
                            // Handle Color Selection
                            color_custom_hex = colorHex;
                            backgroundColor = color;
                            ivSelectedColorView.setBackgroundColor(color);

                        }
                    })
                    .show();
        });

        cv_white.setOnClickListener(v->{

            iv_tick1.setVisibility(View.VISIBLE);
            iv_tick2.setVisibility(View.GONE);
            qrColor = Color.WHITE;
            qrType = 1;

        });

        cv_black.setOnClickListener(v->{

            iv_tick1.setVisibility(View.GONE);
            iv_tick2.setVisibility(View.VISIBLE);
            qrColor = Color.BLACK;
            qrType = 2;

        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = group.findViewById(checkedId);
                String tag = button.getTag().toString();

                ll_pick_color.setVisibility(View.GONE);

                switch (tag){
                    case "rb_tr":
                        backgroundColor = Color.TRANSPARENT;
                        break;
                    case "rb_black":
                        backgroundColor = Color.BLACK;
                        break;
                    case "rb_white":
                        backgroundColor = Color.WHITE;
                        break;
                    case "rb_custom":
                        ll_pick_color.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            gotoNextScreen();
        });
    }

    private void gotoNextScreen(){
        Intent intent = new Intent(this, QrBatchGeneratorActivity.class);
        intent.putExtra(Constants.TAG_QR_TYPE, qrType);
        intent.putExtra(Constants.TAG_QR_COLOR, qrColor);
        intent.putExtra(Constants.TAG_QR_BG_COLOR, backgroundColor);
        startActivity(intent);
    }

    private void init(){

    }
}