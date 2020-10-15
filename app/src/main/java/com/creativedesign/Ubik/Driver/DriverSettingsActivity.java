package com.creativedesign.Ubik.Driver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.creativedesign.Ubik.Objects.DriverObject;
import com.creativedesign.Ubik.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity that displays the settings to the Driver
 */
public class DriverSettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mCarField, mTripsAvailable;

    private ImageView mProfileImage;

    private DatabaseReference mDriverDatabase;

    private String userID;

    private Uri resultUri;

    private SegmentedButtonGroup mRadioGroup;

    DriverObject mDriver = new DriverObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_settings);


        mNameField = findViewById(R.id.name);
        mPhoneField = findViewById(R.id.phone);
        mCarField = findViewById(R.id.car);
        mTripsAvailable = findViewById(R.id.creditos);

        mProfileImage = findViewById(R.id.profileImage);

        mRadioGroup = findViewById(R.id.radioRealButtonGroup);
        mRadioGroup.setOnPositionChangedListener(position -> {

        });

        Button mConfirm = findViewById(R.id.confirm);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);

        getUserInfo();

        mProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        mConfirm.setOnClickListener(v -> saveUserInformation());
        setupToolbar();
    }

    /**
     * Sets up toolbar with custom text and a listener
     * to go back to the previous activity
     */
    private void setupToolbar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.settings));
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        myToolbar.setNavigationOnClickListener(v -> finish());
    }


    /**
     * Fetches current user's info and populates the design elements
     */
    private void getUserInfo(){
        mDriverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }
                mDriver.parseData(dataSnapshot);
                mNameField.setText(mDriver.getName());
                mPhoneField.setText(mDriver.getPhone());
                mCarField.setText(mDriver.getCar());
                mTripsAvailable.setText(String.valueOf(mDriver.getTripsAvailable()));
                mTripsAvailable.setEnabled(false);

                if (!mDriver.getProfileImage().equals("default"))
                    Glide.with(getApplication()).load(mDriver.getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);

                switch (mDriver.getService()) {
                    case "type_1":
                        mRadioGroup.setPosition(0, false);
                        break;
                    case "type_2":
                        mRadioGroup.setPosition(1, false);
                        break;
                    case "type_3":
                        mRadioGroup.setPosition(2, false);
                        break;
                    case "type_4":
                        mRadioGroup.setPosition(3, false);
                        break;
                    case "type_5":
                        mRadioGroup.setPosition(4, false);
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    /**
     * Saves current user 's info to the database.
     * If the resultUri is not null that means the profile image has been changed
     * and we need to upload it to the storage system and update the database with the new url
     */
    private void saveUserInformation() {
        String name = mNameField.getText().toString();
        String phone = mPhoneField.getText().toString();
        String car = mCarField.getText().toString();
        String service = "type_1";


        switch (mRadioGroup.getPosition()){
            case 0:
                service = "type_1";
                break;
            case 1:
                service = "type_2";
                break;
            case 2:
                service = "type_3";
                break;
            case 3:
                service = "type_4";
                break;
            case 4:
                service = "type_5";
                break;
        }

        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
        userInfo.put("car", car);
        userInfo.put("service", service);
        mDriverDatabase.updateChildren(userInfo);

        if(resultUri != null) {

            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);

            UploadTask uploadTask = filePath.putFile(resultUri);
            uploadTask.addOnFailureListener(e -> {
                finish();
                return;
            });
            uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage = new HashMap();
                newImage.put("profileImageUrl", uri.toString());
                mDriverDatabase.updateChildren(newImage);

                finish();
                return;
            }).addOnFailureListener(exception -> {
                finish();
                return;
            }));
        }else{
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            resultUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                Glide.with(getApplication())
                        .load(bitmap) // Uri of the picture
                        .apply(RequestOptions.circleCropTransform())
                        .into(mProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
