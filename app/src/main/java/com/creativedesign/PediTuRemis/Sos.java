package com.creativedesign.PediTuRemis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Sos extends AppCompatActivity {

    ImageView BtnSetEmergency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        BtnSetEmergency = (ImageView)findViewById(R.id.BtnSetEmergency);
        BtnSetEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = "911";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Sos.this,"por favor dar permisos", Toast.LENGTH_SHORT).show();

                    requestionPermission();
                }
                else {

                    startActivity(intent);
                }
            }


        });


    }

    private void requestionPermission() {

        ActivityCompat.requestPermissions(Sos.this,new String[]{Manifest.permission.CALL_PHONE},1);
    }
}
