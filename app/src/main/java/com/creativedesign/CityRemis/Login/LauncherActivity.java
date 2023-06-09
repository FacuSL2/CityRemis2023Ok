package com.creativedesign.CityRemis.Login;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.creativedesign.CityRemis.AdminActivity.AdministratorActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.creativedesign.CityRemis.Customer.CustomerMapActivity;
import com.creativedesign.CityRemis.Driver.DriverMapActivity;

/**
 * First activity of the app.
 *
 * Responsible for checking if the user is logged in or not and call
 * the AuthenticationActivity or MainActivity depending on that.
 */
public class LauncherActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            checkUserAccType();
        }else{
            Intent intent = new Intent(LauncherActivity.this, AuthenticationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
    }

    private void checkUserAccType(){
        String userID;

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Intent intent = new Intent(getApplication(), CustomerMapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        DatabaseReference mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);
        mDriverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Intent intent = new Intent(getApplication(), DriverMapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        DatabaseReference mAdminDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Admin").child(userID);
        mAdminDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Intent intent = new Intent(getApplication(), AdministratorActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
