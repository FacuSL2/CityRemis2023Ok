package com.creativedesign.Ubik.AdminActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.creativedesign.Ubik.Login.LauncherActivity;
import com.creativedesign.Ubik.Objects.AdministratorObject;
import com.creativedesign.Ubik.Objects.DriverObject;
import com.creativedesign.Ubik.R;
import com.creativedesign.Ubik.sosact;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main Activity displayed to the driver
 */
public class AdministratorActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DriversListener {

    private Switch activeSwitch;
    private AutoCompleteTextView act_driver;
    String filterDriver;
    boolean isActive, actualizarDatos;
    RecyclerView rv_drivers;
    DriversAdapter driversAdapter;
    List<DriverObject> lstDriverObject;
    DatabaseReference mUser;
    TextView mUsername;
    ImageView mProfileImage;
    AdministratorObject administratorObject = new AdministratorObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);

        Toolbar toolbar = findViewById(R.id.toolbar);

        activeSwitch = findViewById(R.id.activeSwitch);
        activeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isActive = isChecked;
            actualizarDatos = true;
            getListDrivers();
        });
        activeSwitch.setChecked(true);

        rv_drivers = findViewById(R.id.rv_search_drivers);
        act_driver = findViewById(R.id.act_driver);
        filterDriver = "";

        actualizarDatos = true;
        act_driver.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                filterDriver = s.toString();
                actualizarDatos = true;
                getListDrivers();
            }
        });

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView mDrawerButton = findViewById(R.id.drawerButton);
        mDrawerButton.setOnClickListener(v -> drawer.openDrawer(Gravity.LEFT));

        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child("Admin").child(FirebaseAuth.getInstance().getUid());
        mUsername = navigationView.getHeaderView(0).findViewById(R.id.usernameDrawer);
        mProfileImage =  navigationView.getHeaderView(0).findViewById(R.id.imageViewDrawer);
        //getListDrivers();
        getUserData();
    }

    @Override
    public void updateDriverTrip(DriverObject driverObject, int newTripAvailable) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverObject.getId());
        Map userInfo = new HashMap();
        userInfo.put("tripsAvailable", newTripAvailable);
        ref.updateChildren(userInfo);
    }

    /**
     * Fetches current user's info and populates the design elements
     */
    private void getUserData() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Admin").child(driverId);
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    administratorObject.parseData(dataSnapshot);
                    mUsername.setText(administratorObject.getName());
                    if(!administratorObject.getProfileImage().equals("default"))
                        Glide.with(getApplication()).load(administratorObject.getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getListDrivers() {
        lstDriverObject = new ArrayList<>();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if(filterDriver.length() > 0) {
                        DriverObject post = postSnapshot.getValue(DriverObject.class);
                        post.setId(postSnapshot.getKey());
                        if(post.getName().toUpperCase().contains(filterDriver.toUpperCase()) && post.getActive() == isActive) {
                            for (int i = 0; i < lstDriverObject.size(); i++) {
                                if(lstDriverObject.get(i).getId() == post.getId()) {
                                    lstDriverObject.remove(i);
                                    break;
                                }
                            }
                            lstDriverObject.add(post);
                        }
                    } else {
                        DriverObject post = postSnapshot.getValue(DriverObject.class);
                        post.setId(postSnapshot.getKey());
                        if(post.getActive() == isActive) {
                            for (int i = 0; i < lstDriverObject.size(); i++) {
                                if(lstDriverObject.get(i).getId() == post.getId()) {
                                    lstDriverObject.remove(i);
                                    break;
                                }
                            }
                            lstDriverObject.add(post);
                        }
                    }
                }
                if(actualizarDatos)
                {
                    actualizarDatos = false;
                    driversAdapter = new DriversAdapter(AdministratorActivity.this, lstDriverObject, AdministratorActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AdministratorActivity.this);
                    rv_drivers.setLayoutManager(mLayoutManager);
                    rv_drivers.setItemAnimator(new DefaultItemAnimator());
                    rv_drivers.setAdapter(driversAdapter);
                    rv_drivers.addItemDecoration(new DividerItemDecoration(AdministratorActivity.this, 0));
                    driversAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void logOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LauncherActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent intent = new Intent(this, AdministratorSettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.sos) {
            Intent intent = new Intent(this, sosact.class);
            startActivity(intent);

        } else if (id == R.id.logout) {
            logOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
