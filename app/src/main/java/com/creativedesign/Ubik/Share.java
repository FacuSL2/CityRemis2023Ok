package com.creativedesign.Ubik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Share extends AppCompatActivity {

    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        bt=(Button) findViewById(R.id.BtnCompartir);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "Te invito a descargar la aplicación de Ubik";
                String shareSub = "Descarga Ubik y viaja seguro al mejor precio! https://play.google.com/store/apps/details?id=com.creativedesign.Ubik";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
                myIntent.putExtra(Intent.EXTRA_TEXT,shareSub);
                startActivity(Intent.createChooser(myIntent, "Compartir"));


            }
        });
    }
}
