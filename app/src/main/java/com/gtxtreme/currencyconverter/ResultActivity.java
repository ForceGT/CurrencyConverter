package com.gtxtreme.currencyconverter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class ResultActivity extends AppCompatActivity {

    private TextView amountFrom, amountTo;
    private Button buttonTakeSS;
    private ImageView preview;
    private File imageFile;
    private static final int WRITE_STORAGE_CODE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
        amountFrom = findViewById(R.id.amtfromvalue);
        amountTo = findViewById(R.id.amttovalue);
        preview = findViewById(R.id.imageView);
        buttonTakeSS = findViewById(R.id.button);

        Intent intent = getIntent();
        String amountFromValue = intent.getStringExtra("amountValue");
        String fromCountryCode = intent.getStringExtra("fromCountryCode");
        String toCountryCode = intent.getStringExtra("toCountryCode");
        String finalConversionRate = intent.getStringExtra("finalConversionRate");

        amountFrom.setText(amountFromValue + " " + fromCountryCode);
        String amountToValue = performCalculations(toCountryCode, finalConversionRate, amountFromValue);

        amountTo.setText(amountToValue);

        buttonTakeSS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String [] permissions ={Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if(ContextCompat.checkSelfPermission(ResultActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ResultActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_STORAGE_CODE);
                }
                else {
                    takeScreenshot();
                }
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        switch (requestCode){
            case WRITE_STORAGE_CODE:
                if(grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    takeScreenshot();
                }
                break;

        }
    }



    private void takeScreenshot() {
        Date currentDate = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", currentDate);

        try {
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + currentDate + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            preview.setImageBitmap(bitmap);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public String performCalculations(String toCountryCode, String finalConversionRate, String amountFromValue) {

        int value_1 = Integer.parseInt(amountFromValue);
        double rate = Double.parseDouble(finalConversionRate);
        double value_2 = value_1 * rate;
        int finalvalue = (int) value_2;


        return String.valueOf(finalvalue) + " " + toCountryCode;

    }
}
