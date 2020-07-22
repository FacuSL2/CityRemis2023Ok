package com.creativedesign.PediTuRemis.Driver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.creativedesign.PediTuRemis.Utils.ConsumptionWS;
import com.creativedesign.PediTuRemis.sosact;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.creativedesign.PediTuRemis.Objects.DriverObject;
import com.creativedesign.PediTuRemis.History.HistoryActivity;
import com.creativedesign.PediTuRemis.Login.LauncherActivity;
import com.creativedesign.PediTuRemis.R;
import com.creativedesign.PediTuRemis.Objects.RideObject;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main Activity displayed to the driver
 */
public class DriverMapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, DirectionCallback  {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    LatLng oldLocation, newLocaation;
    long lastTimeUpdated;
    String nombreChofer, mUserId;

    private FusedLocationProviderClient mFusedLocationClient;

    private Button  mRideStatus, mMaps, mCall, mCancelRide, waiting;

    private Switch mWorkingSwitch;

    private int status = 0;

    private LinearLayout mCustomerInfo, mBringUpBottomLayout;

    private ImageView mCustomerProfileImage;

    private TextView mCustomerName, mLocation;
    DatabaseReference mUser;

    RideObject mCurrentRide;

    Marker pickupMarker, destinationMarker, clientMarker;

    DriverObject mDriver = new DriverObject();

    TextView mUsername;
    ImageView mProfileImage;

    MediaPlayer mp;
    private boolean isMarkerRotating;

    private SharedPreferences mySharedPreferences;

    private int mostrandoDialogo = 0;
    static final int TIME_OUT = 30000;
    static final int MSG_DISMISS_DIALOG = 0;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Toolbar toolbar = findViewById(R.id.toolbar);

        mp = MediaPlayer.create(this, R.raw.sonido);

        mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        polylines = new ArrayList<>();

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(FirebaseAuth.getInstance().getUid());

        mCustomerInfo = findViewById(R.id.customerInfo);

        mCustomerProfileImage = findViewById(R.id.customerProfileImage);
        mBringUpBottomLayout = findViewById(R.id.bringUpBottomLayout);

        mCustomerName = findViewById(R.id.name);
        mLocation = findViewById(R.id.location_name);
        mUsername = navigationView.getHeaderView(0).findViewById(R.id.usernameDrawer);
        mProfileImage =  navigationView.getHeaderView(0).findViewById(R.id.imageViewDrawer);
        mMaps = findViewById(R.id.open_maps);

        mCall = findViewById(R.id.phone);

        mWorkingSwitch = findViewById(R.id.workingSwitch);
        mWorkingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            long tiempoSancionado = mySharedPreferences.getLong("sancion", 0);
            if(tiempoSancionado != 0) {
                Calendar tSancionado = Calendar.getInstance();
                tSancionado.setTimeInMillis(tiempoSancionado);
                Calendar ahora = Calendar.getInstance();
                if(tSancionado.after(ahora)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Suspención Temporal")
                            .setMessage("Se encuentra suspendido por un periodo de 10 minutos, esto porque cancelo un viaje recientemente.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, null).show();
                    mWorkingSwitch.setChecked(false);
                    return;
                } else {
                    SharedPreferences.Editor edit = mySharedPreferences.edit();
                    edit.putLong("sancion", 0);
                    edit.apply();
                }
            }

            if(!mDriver.getActive()){
                Toast.makeText(DriverMapActivity.this, R.string.not_approved, Toast.LENGTH_LONG).show();
                mWorkingSwitch.setChecked(false);
                return;
            }
            if (isChecked){
                if(mLastLocation != null) {
                    if(mMap != null) {
                        pickupMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.vehiculo)));
                    }
                } else {
                    if(mMap != null) {
                        pickupMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(0, 0))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.vehiculo)));
                    }
                }
                connectDriver();
            }else{
                disconnectDriver();
            }
        });

        mRideStatus = findViewById(R.id.rideStatus);

        mRideStatus.setOnClickListener(v -> {
            switch(status){
                case 1:
                    if(mCurrentRide==null){
                        endRide();
                        return;
                    }
                    status=2;
                    erasePolylines();
                    if(mCurrentRide.getDestination().getCoordinates().latitude!=0.0 && mCurrentRide.getDestination().getCoordinates().longitude!=0.0){
                        getRouteToMarker(mCurrentRide.getDestination().getCoordinates());
                    }
                    mRideStatus.setText(R.string.drive_complete);
                    mLocation.setText(mCurrentRide.getDestination().getName());

                    break;
                case 2:
                    if(mCurrentRide != null)
                        mCurrentRide.recordRide();
                    endRide();
                    break;
            }
        });

        waiting = findViewById(R.id.waiting);
        waiting.setVisibility(View.GONE);

        waiting.setOnClickListener(v -> {
            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                @Override
                public void idsAvailable(String userId, String registrationId) {
                    Log.d("debug", "UserId:" + userId);
                    if (registrationId != null) {
                        mUserId = userId;
                        Log.d("debug", "registrationId:" + registrationId);
                        SendPostTask sendPostTask = new SendPostTask();
                        sendPostTask.execute((Void) null);
                    }
                }
            });

        });

        isMarkerRotating = false;

        mMaps.setOnClickListener(view -> {
            if(status == 1){
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" +  mCurrentRide.getPickup().getCoordinates().latitude + "," + mCurrentRide.getPickup().getCoordinates().longitude));
                startActivity(intent);
            }else{
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + mCurrentRide.getDestination().getCoordinates().latitude + "," + mCurrentRide.getDestination().getCoordinates().longitude));
                startActivity(intent);
            }
        });

        mCall.setOnClickListener(view -> {
            if(mCurrentRide == null){
                Snackbar.make(findViewById(R.id.drawer_layout),getString(R.string.driver_no_phone), Snackbar.LENGTH_LONG).show();
            }
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCurrentRide.getCustomer().getPhone()));
                startActivity(intent);
            }else{
                Snackbar.make(findViewById(R.id.drawer_layout),getString(R.string.no_phone_call_permissions), Snackbar.LENGTH_LONG).show();
            }
        });


        ImageView mDrawerButton = findViewById(R.id.drawerButton);
        mDrawerButton.setOnClickListener(v -> drawer.openDrawer(Gravity.LEFT));

        mBringUpBottomLayout = findViewById(R.id.bringUpBottomLayout);
        mBringUpBottomLayout.setOnClickListener(v -> {
            if(mBottomSheetBehavior.getState()!= BottomSheetBehavior.STATE_EXPANDED)
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            else
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            if(status == 0){
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        getUserData();
        getAssignedCustomer();

        ViewTreeObserver vto = mBringUpBottomLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(() -> initializeBottomLayout());

        mCancelRide = findViewById(R.id.cancelRide);
        mCancelRide.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cancelación de Viaje")
                    .setMessage("Está seguro que desea cancelar el viaje?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (DialogInterface dialog, int which) -> {
                        mCurrentRide.cancelRide();
                        disconnectDriver();
                        Calendar ahora = Calendar.getInstance();
                        ahora.add(Calendar.MINUTE, 10);
                        SharedPreferences.Editor edit = mySharedPreferences.edit();
                        edit.putLong("sancion", ahora.getTimeInMillis());
                        edit.apply();
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });

    }

    View mBottomSheet;
    BottomSheetBehavior mBottomSheetBehavior;

    /**
     * Listener for the bottom popup. This will control
     * when it is shown and when it isn't according to the actions of the users
     * of pulling on it or just clicking on it.
     */
    private void initializeBottomLayout() {
        mBottomSheet =findViewById(R.id.bottomSheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setPeekHeight(mBringUpBottomLayout.getHeight());


        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(status == 0){
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private class SendPostTask extends AsyncTask<Void, Void, Boolean> {

        protected Boolean doInBackground(Void... urls) {
            int restulado = ConsumptionWS.postOneSignalJSON(nombreChofer, mCurrentRide.getCustomer().getEmail());
            return true ;
        }
    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    protected double distances(double lat1, double lon1, double lat2, double lon2, String unit)
    {
        double rlat1 = Math.PI * lat1/180.0f;
        double rlat2 = Math.PI * lat2/180.0f;
        double rlon1 = Math.PI * lon1/180.0f;
        double rlon2 = Math.PI * lon2/180.0f;

        double theta = lon1-lon2;
        double rtheta = Math.PI * theta/180.0f;

        double dist = Math.sin(rlat1) * Math.sin(rlat2) + Math.cos(rlat1) *     Math.cos(rlat2) * Math.cos(rtheta);
        dist = Math.acos(dist);
        dist = dist * 180.0f/Math.PI;
        dist = dist * 60.0f * 1.1515f;

        if (unit=="K") { dist = dist * 1.609344f; }
        if (unit == "M") { dist = dist * 1.609344 * 1000f; }
        if (unit == "N") { dist = dist * 0.8684f; }
        return dist;
    }

    /**
     * Fetches current user's info and populates the design elements
     */
    private void getUserData() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId);
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mDriver.parseData(dataSnapshot);
                    nombreChofer = mDriver.getName();
                    mUsername.setText(mDriver.getName());
                    if(!mDriver.getProfileImage().equals("default"))
                        Glide.with(getApplication()).load(mDriver.getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("driversAvailable").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                   connectDriver();
                }else
                    disconnectDriver();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("driversWorking").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    connectDriver();
                }else
                    disconnectDriver();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_DISMISS_DIALOG:
                    if (mAlertDialog != null && mAlertDialog.isShowing()) {
                        mAlertDialog.dismiss();
                        mostrandoDialogo = 0;
                        mCurrentRide.cancelRide();
                        endRide();
                        disconnectDriver();
                        Calendar ahora = Calendar.getInstance();
                        ahora.add(Calendar.MINUTE, 10);
                        SharedPreferences.Editor edit = mySharedPreferences.edit();
                        edit.putLong("sancion", ahora.getTimeInMillis());
                        edit.apply();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Is always listening to the ride_info table to see if the current driver's id
     * pops up in there.
     *
     * If it does then it means the driver has been assigned a new job and must complete it.
     */
    private void getAssignedCustomer(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseDatabase.getInstance().getReference().child("ride_info").orderByChild("driverId").equalTo(driverId);

        query.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    status = 1;
                    mCurrentRide= new RideObject();
                    mCurrentRide.parseData(dataSnapshot);

                    if(mCurrentRide.getEnded() || mCurrentRide.getCancelled()){
                        endRide();
                        mCurrentRide= null;
                        return;
                    }

                    if(mostrandoDialogo == 0) {
                        mostrandoDialogo = 1;
                        AlertDialog.Builder builder = new AlertDialog.Builder(DriverMapActivity.this);
                        builder.setTitle("Nuevo Viaje");
                        builder.setCancelable(false);
                        builder.setMessage("Tiene un nuevo viaje, dispone de tiempo para realizarlo?");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mostrandoDialogo = 0;
                            }
                        })
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mostrandoDialogo = 0;
                                        mCurrentRide.cancelRide();
                                        endRide();
                                        disconnectDriver();
                                        Calendar ahora = Calendar.getInstance();
                                        ahora.add(Calendar.MINUTE, 10);
                                        SharedPreferences.Editor edit = mySharedPreferences.edit();
                                        edit.putLong("sancion", ahora.getTimeInMillis());
                                        edit.apply();
                                    }
                                });
                        mAlertDialog = builder.create();
                        mAlertDialog.show();
                        // dismiss dialog in TIME_OUT ms
                        mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, TIME_OUT);
                    }
                    ///Reemplazar "ic_radio_filled" x "destino"
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(mCurrentRide.getDestination().getCoordinates()).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_filled)));
                    //pickupMarker = mMap.addMarker(new MarkerOptions().position(mCurrentRide.getPickup().getCoordinates()).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio)));

                    if(mCurrentRide.getPickup() != null && mCurrentRide.getPickup().getCoordinates() != null) {
                        if(pickupMarker == null) {
                            pickupMarker = mMap.addMarker(new MarkerOptions()
                                    .position(mCurrentRide.getPickup().getCoordinates())
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.vehiculo)));
                        }
                        pickupMarker.setPosition(mCurrentRide.getPickup().getCoordinates());
                    }

                    mLocation.setText(mCurrentRide.getPickup().getName());
                    mCustomerName.setText(mCurrentRide.getDestination().getName());
                    mp.start();

                    getAssignedCustomerInfo();
                    getHasRideEnded();
                }else{
                    endRide();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    /**
     * Get Route from pickup to destination, showing the route to the user
     */
    private void getRouteToMarker(LatLng pickupLatLng) {
        String serverKey = getResources().getString(R.string.google_maps_key);
        if (pickupLatLng != null && mLastLocation != null){
            GoogleDirection.withServerKey(serverKey)
                    .from(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .to(pickupLatLng)
                    .transportMode(TransportMode.DRIVING)
                    .execute(this);
        }
    }

    /**
     * Fetch assigned customer's info and display it in the Bottom sheet
     */
    private void getAssignedCustomerInfo(){
        if(mCurrentRide.getCustomer().getId() == null){return;}
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(mCurrentRide.getCustomer().getId());
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){return;}

                if(mCurrentRide != null){

                    mCurrentRide.getCustomer().parseData(dataSnapshot);

                    mCustomerName.setText(mCurrentRide.getCustomer().getName());
                    if(!mCurrentRide.getCustomer().getProfileImage().equals("default"))
                        Glide.with(getApplication()).load(mCurrentRide.getCustomer().getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mCustomerProfileImage);

                    clientMarker = mMap.addMarker(new MarkerOptions()
                            .position(mCurrentRide.getPickup().getCoordinates()));
                    getRouteToMarker(mCurrentRide.getPickup().getCoordinates());
                }

                mCustomerInfo.setVisibility(View.VISIBLE);

                mBottomSheetBehavior.setHideable(true);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Listen for the customerRequest Node to see if the driver ended it
     * in the mean time.
     */
    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded(){
        if(mCurrentRide == null){return;}
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("ride_info").child(mCurrentRide.getId()).child("cancelled");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){return;}
                if ((boolean) dataSnapshot.getValue() == true)
                    endRide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * End Ride by removing all of the active listeners,
     * returning all of the values to the default state
     * and clearing the map from markers
     */
    private void endRide(){
        if(mCurrentRide == null){return;}

        mRideStatus.setText(getString(R.string.picked_customer));
        erasePolylines();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
        driverRef.removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(mCurrentRide.getCustomer().getId(), (key, error) -> {});

        mCurrentRide = null;

        status = 0;

        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if(destinationMarker != null){
            destinationMarker.remove();
        }
        if (driveHasEndedRefListener != null)
            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);

        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mCustomerName.setText("");
        mLocation.setText("");
        mCustomerProfileImage.setImageResource(R.mipmap.ic_default_user);

        mMap.clear();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }
    }


    boolean zoomUpdated = false;
    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }

            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
                    DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                    GeoFire geoFireAvailable = new GeoFire(refAvailable);
                    GeoFire geoFireWorking = new GeoFire(refWorking);

                    if(!mWorkingSwitch.isChecked()){
                        geoFireWorking.removeLocation(userId, (key, error) -> {});
                        return;
                    }
                    if(mCurrentRide != null) {
                        if (mCurrentRide.getEnded() || mCurrentRide.getCancelled()) {
                            mCurrentRide.cancelRide();
                        }
                    }

                    if(mCurrentRide != null && mLastLocation!=null && location != null){
                        mCurrentRide.setRideDistance(mCurrentRide.getRideDistance() + mLastLocation.distanceTo(location)/1000);
                    }
                    mLastLocation = location;

                    Map newUserMap = new HashMap();
                    newUserMap.put("last_updated", ServerValue.TIMESTAMP);
                    mUser.updateChildren(newUserMap);

                    if(!zoomUpdated){
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                .zoom(18)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));

                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                        //mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                        zoomUpdated = true;
                    }

                    if(mCurrentRide == null){
                        //mMap.clear();
                        erasePolylines();
                        geoFireWorking.removeLocation(userId, (key, error) -> {});
                        if(mWorkingSwitch.isChecked()){
                            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()), (key, error) -> {});
                            //mMap.clear();
                            erasePolylines();
                        }
                    }else{
                        geoFireAvailable.removeLocation(userId, (key, error) -> {});
                        geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()), (key, error) -> {});
                    }
                    pickupMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));

                    Date date = new Date();
                    Calendar now = Calendar.getInstance();
                    now.setTime(date);
                    now.add(Calendar.SECOND, 10);

                    if(lastTimeUpdated == 0) {
                        lastTimeUpdated = now.getTimeInMillis();
                    } else {
                        if(date.getTime() > lastTimeUpdated) {
                            lastTimeUpdated = now.getTimeInMillis();
                            if(oldLocation == null) {
                                oldLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            }
                            newLocaation = new LatLng(location.getLatitude(), location.getLongitude());
                            float bearing = (float) bearingBetweenLocations(oldLocation, newLocaation);

                            double distancia = distances(oldLocation.latitude, oldLocation.longitude, newLocaation.latitude, newLocaation.longitude, "K");
                            if(distancia <= 1) {
                                waiting.setVisibility(View.VISIBLE);
                            }
                            oldLocation = newLocaation;
                            rotateMarker(pickupMarker, bearing);
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(newLocaation)
                                    .bearing(bearing)
                                    .zoom(18)
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));
                            if(status == 1 && mCurrentRide != null) {
                                if(mCurrentRide.getPickup() != null) {
                                    clientMarker = mMap.addMarker(new MarkerOptions()
                                            .position(mCurrentRide.getPickup().getCoordinates()));
                                    erasePolylines();
                                    getRouteToMarker(mCurrentRide.getPickup().getCoordinates());
                                }
                            }
                        }
                    }


                }
            }
        }
    };


    /**
     * Get permissions for our app if they didn't previously exist.
     * requestCode: the number assigned to the request that we've made. Each
     *     |                request has it's own unique request code.
     */
    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", (dialogInterface, i) -> ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1))
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void logOut(){
        disconnectDriver();

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(DriverMapActivity.this, LauncherActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    /**
     * Connects driver, waking up the code that fetches current location
     */
    private void connectDriver(){
        mWorkingSwitch.setChecked(true);
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
    }

    /**
     * Disconnects driver, putting to sleep the code that fetches current location
     */
    private void disconnectDriver(){
        mWorkingSwitch.setChecked(false);
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable").child(userId);
        ref.removeValue();
        mMap.clear();
    }

    private List<Polyline> polylines;

    /**
     * Remove route polylines from the map
     */
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

    /**
     * Show map within the pickup and destination marker,
     * This will make sure everything is displayed to the user
     * @param route - route between pickup and destination
     */
    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }
    /**
     * Checks if route where fetched successfully, if yes then
     * add them to the map
     * @param direction
     * @param rawBody - data of the route
     */
    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            Route route = direction.getRouteList().get(0);

            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            Polyline polyline = mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.BLACK));
            polylines.add(polyline);
            setCameraWithCoordinationBounds(route);

        } else {
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

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

        if (id == R.id.history) {
            Intent intent = new Intent(DriverMapActivity.this, HistoryActivity.class);
            intent.putExtra("customerOrDriver", "Drivers");
            startActivity(intent);
        } else if (id == R.id.settings) {
            Intent intent = new Intent(DriverMapActivity.this, DriverSettingsActivity.class);
            startActivity(intent);

        } else if (id == R.id.sos) {
            Intent intent = new Intent(DriverMapActivity.this, sosact.class);
            startActivity(intent);

        } else if (id == R.id.logout) {
           logOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
