package com.creativedesign.Ubik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class sosact extends AppCompatActivity {
    ImageView BtnSetEmergency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosact);
        BtnSetEmergency = (ImageView)findViewById(R.id.BtnSetEmergency);
        BtnSetEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = "911";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(sosact.this,"por favor dar permisos", Toast.LENGTH_SHORT).show();

                    requestionPermission();
                }
                else {

                    startActivity(intent);
                }
            }


        });


    }

    private void requestionPermission() {

        ActivityCompat.requestPermissions(sosact.this,new String[]{Manifest.permission.CALL_PHONE},1);
    }
}
