package com.creativedesign.CityRemis.Customer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.maps.model.DirectionsResult;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsResult;
import com.google.maps.DirectionsStatus;
import com.google.maps.model.DirectionsRequest;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.UnitSystem;


import androidx.annotation.NonNull;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.creativedesign.CityRemis.Objects.NearObject;
import com.creativedesign.CityRemis.Reclamos;
import com.creativedesign.CityRemis.Share;
import com.creativedesign.CityRemis.TarifasUbik;
import com.creativedesign.CityRemis.TrabajarenUbik;
import com.creativedesign.CityRemis.WebUbik;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.creativedesign.CityRemis.sosact;
import com.creativedesign.CityRemis.DialogCobrar;
import com.creativedesign.CityRemis.EventDialogCobrar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.creativedesign.CityRemis.Objects.CustomerObject;
import com.creativedesign.CityRemis.Objects.DriverObject;
import com.creativedesign.CityRemis.History.HistoryActivity;
import com.creativedesign.CityRemis.Objects.LocationObject;
import com.creativedesign.CityRemis.Login.LauncherActivity;
import com.creativedesign.CityRemis.R;
import com.creativedesign.CityRemis.Objects.RideObject;
import com.creativedesign.CityRemis.Adapters.TypeAdapter;
import com.creativedesign.CityRemis.Objects.TypeObject;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Main Activity displayed to the customer
 */
public class CustomerMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, DirectionCallback, EventDialogCobrar {

    int MAX_SEARCH_DISTANCE = 4;

    private GoogleMap mMap;

    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationClient;

    private Button mRequest;

    private LocationObject pickupLocation, currentLocation, destinationLocation, destinationLocationdos;

    private Boolean requestBol = false, pickupIsCurrent = false;

    private Marker destinationMarker,destinationMarker2, pickupMarker;

    private SupportMapFragment mapFragment;

    private TextView  mRatingText, textViewPrecioSolicitud, textViewPrecioSolicitud2;

    private int mostrandoDialogo = 0;

    private LinearLayout mDriverInfo,
            mRadioLayout;

    private Float priceKm;
    private Float priceBase;
    private Float kmBase;


    int esperandoVehiculo = 0;
    static final int TIME_OUT = 25000;
    static final int TIME_OUT2 = 5000;
    static final int MSG_DISMISS_DIALOG = 0;

    private ImageView mDriverProfileImage, mCurrentLocation;

    private TextView mDriverName,
            mDriverCar,
            autocompleteFragmentTo,
            autocompleteFragmentDos,
            autocompleteFragmentFrom;
    CardView autocompleteFragmentFromContainer;

    Button mCallDriver, mCancelar;

    DrawerLayout drawer;

    LinearLayout mBringUpBottomLayout;

    RideObject mCurrentRide;

    private RecyclerView mRecyclerView;
    private TypeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<TypeObject> typeArrayList = new ArrayList<>();
    private boolean activar = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_customer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getPriceKm();

        mCurrentRide = new RideObject(CustomerMapActivity.this, null);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getUserData();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDriverInfo = findViewById(R.id.driverInfo);
        mRadioLayout = findViewById(R.id.radioLayout);

        mDriverProfileImage = findViewById(R.id.driverProfileImage);

        mDriverName = findViewById(R.id.driverName);
        mDriverCar = findViewById(R.id.driverCar);

        mCallDriver = findViewById(R.id.phone);

        mRatingText = findViewById(R.id.ratingText);

        autocompleteFragmentTo = findViewById(R.id.place_to);
        autocompleteFragmentDos = findViewById(R.id.place_to2);
        autocompleteFragmentFrom = findViewById(R.id.place_from);
        autocompleteFragmentFromContainer = findViewById(R.id.place_from_container);
        mCurrentLocation = findViewById(R.id.current_location);

        mCancelar = findViewById(R.id.cancelar);
        mCancelar.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cancelación de Viaje")
                    .setMessage("Está seguro que desea cancelar el viaje?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (DialogInterface dialog, int which) -> {
                        mCurrentRide.cancelRidecl();
                        endRider();
                        viajecliente();
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });

        mRequest = findViewById(R.id.request);
        textViewPrecioSolicitud= findViewById(R.id.textViewPrecioSolicitud);
        textViewPrecioSolicitud2= findViewById(R.id.textViewPrecioSolicitud2);

        mRequest.setOnClickListener(v -> {

            if (requestBol) {
                mCurrentRide.cancelRide();
                endRide();

            } else {

                mCurrentRide.setDestination(destinationLocation);
                mCurrentRide.setDestination(destinationLocationdos);
                mCurrentRide.setPickup(pickupLocation);
                mCurrentRide.setRequestService(mAdapter.getSelectedItem().getId());

                if(mCurrentRide.checkRide() == -1){
                    return;
                }
                mCurrentRide.postRide();

                requestBol = true;

                mRequest.setText(R.string.getting_driver);

                autocompleteFragmentTo.setVisibility(View.GONE);
                autocompleteFragmentDos.setVisibility(View.GONE);
                autocompleteFragmentFromContainer.setVisibility(View.GONE);

                getClosestDriver();
            }

        });
        mCallDriver.setOnClickListener(view -> {

            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + mCurrentRide.getDriver().getPhone() + "&text=Hola " +  mCurrentRide.getDriver().getName() +" soy tu pasajer@ de City "); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            /*if(mCurrentRide == null){
                Snackbar.make(findViewById(R.id.drawer_layout),getString(R.string.driver_no_phone), Snackbar.LENGTH_LONG).show();
            }
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCurrentRide.getDriver().getPhone()));
                startActivity(intent);
            }else{
                Snackbar.make(findViewById(R.id.drawer_layout),getString(R.string.no_phone_call_permissions), Snackbar.LENGTH_LONG).show();
            }*/
        });

        ImageView mDrawerButton = findViewById(R.id.drawerButton);
        mDrawerButton.setOnClickListener(v -> drawer.openDrawer(Gravity.LEFT));

        mBringUpBottomLayout = findViewById(R.id.bringUpBottomLayout);
        mBringUpBottomLayout.setOnClickListener(v -> {
            if(mBottomSheetBehavior.getState()!= BottomSheetBehavior.STATE_EXPANDED)
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            else
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });


        mCurrentLocation.setOnClickListener(view -> {
            pickupIsCurrent = !pickupIsCurrent;

            if(pickupIsCurrent){
                autocompleteFragmentFrom.setText("Localización actual");
                mCurrentLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_on_primary_24dp));
                pickupLocation = currentLocation;
                if(pickupLocation==null){return;}
                fetchLocationName();

                mMap.clear();
                pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation.getCoordinates()).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radios)));
                mCurrentRide.setPickup(pickupLocation);
                autocompleteFragmentFrom.setText(pickupLocation.getName());
                if(destinationLocation != null){
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLocation.getCoordinates()).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_filled)));
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    Double price = getPriceTravel(getDistance());
                    textViewPrecioSolicitud.setText(getString(R.string.price)+"$"+String.format("%.2f",price));
                } if(destinationLocationdos != null){
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLocation.getCoordinates()).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_filled)));
                    destinationMarker2 = mMap.addMarker(new MarkerOptions().position(destinationLocationdos.getCoordinates()).title("Destino2").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_filled)));
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    Double price = getPriceTravel(getDistance());
                    textViewPrecioSolicitud.setText(getString(R.string.price)+"$"+String.format("%.2f",price));
                }

                erasePolylines();
                getRouteToMarker();
                getDriversAround();

                mRequest.setText(getString(R.string.call_uber));
                mRequest.setEnabled(true);
            }else{
                mMap.clear();
                autocompleteFragmentFrom.setText(getString(R.string.from));
                mCurrentLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_on_grey_24dp));
                if(destinationLocation != null){
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLocation.getCoordinates()).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_filled)));
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }if(destinationLocationdos != null){
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLocation.getCoordinates()).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_filled)));
                    destinationMarker2 = mMap.addMarker(new MarkerOptions().position(destinationLocationdos.getCoordinates()).title("Destino2").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_filled)));
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }
                erasePolylines();
                getDriversAround();
            }



        });


        ViewTreeObserver vto = mBringUpBottomLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(() -> {
            initializeBottomLayout();
            initPlacesAutocomplete();
        });

        initRecyclerView();
    }


    /**
     * Al presionar tecla ATRAS, se solicitará un dialogo para confirmar
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==event.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Desea Salir de City?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })

                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();

        }
        return super.onKeyDown(keyCode, event);


    }

    /**
     * Initializes the recyclerview that shows the costumer the
     * available car types
     */
    private void initRecyclerView(){
        typeArrayList.add(new TypeObject("type_1", getResources().getString(R.string.type_1), getResources().getDrawable(R.drawable.wpp), 4));
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(CustomerMapActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TypeAdapter(typeArrayList, CustomerMapActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }
    boolean previousRequestBol = true;
    View mBottomSheet;
    BottomSheetBehavior mBottomSheetBehavior;
    BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;

    /**
     * Listener for the bottom popup. This will control
     * when it is shown and when it isn't according to the actions of the users
     * of pulling on it or just clicking on it.
     */
    private void initializeBottomLayout() {
        mBottomSheet =findViewById(R.id.bottomSheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setPeekHeight(mBringUpBottomLayout.getHeight());
        mBottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);


        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState==BottomSheetBehavior.STATE_COLLAPSED && requestBol != previousRequestBol){
                    if(!requestBol){
                        mDriverInfo.setVisibility(View.GONE);
                        mRadioLayout.setVisibility(View.VISIBLE);
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        previousRequestBol = requestBol;
                    }
                    else{
                        mDriverInfo.setVisibility(View.VISIBLE);
                        mRadioLayout.setVisibility(View.GONE);
                         mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        previousRequestBol = requestBol;
                    }
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    /**
     * Init Places according the updated google api and
     * listen for user inputs, when a user chooses a place change the values
     * of destination and destinationLocation so that the user can call a driver
     */
    void initPlacesAutocomplete() {
        /*
          Initialize Places. For simplicity, the API key is hard-coded. In a production
          environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }

        autocompleteFragmentTo.setOnClickListener(v -> {
            if(requestBol){return;}
            List<TypeFilter> typeFilters = new ArrayList<>(Arrays.asList(TypeFilter.values()));
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG))
                    .setLocationRestriction(RectangularBounds.newInstance(
                            new LatLng(-33.883745,-61.195373),
                                new LatLng(-32.580815,-60.501327))


                            //new LatLng(-33.563580,-61.206685),
                            //                            new LatLng(-32.580815,-60.501327))



                            // -33.080826, -60.797730 -- -32.580815, -60.501327
                            //-32.793840, -60.524446

                            //  new LatLng(-27.544747,-58.880979),
                            //  new LatLng(-27.4568393,-58.7662969))
                    )
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .build(getApplicationContext());
            startActivityForResult(intent, 1);
        });


        autocompleteFragmentFrom.setOnClickListener(v -> {
            if(requestBol){return;}
            List<TypeFilter> typeFilters = new ArrayList<>(Arrays.asList(TypeFilter.values()));
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG))
                    .setLocationRestriction(RectangularBounds.newInstance(
                            new LatLng(-33.883745,-61.195373),
                            new LatLng(-32.580815,-60.501327))
                    )
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .build(getApplicationContext());
            startActivityForResult(intent, 2);
        });

        autocompleteFragmentDos.setOnClickListener(v -> {
            if(requestBol){return;}
            List<TypeFilter> typeFilters = new ArrayList<>(Arrays.asList(TypeFilter.values()));
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG))
                    .setLocationRestriction(RectangularBounds.newInstance(
                                    new LatLng(-33.883745,-61.195373),
                                    new LatLng(-32.580815,-60.501327))


                            //new LatLng(-33.563580,-61.206685),
                            //                            new LatLng(-32.580815,-60.501327))



                            // -33.080826, -60.797730 -- -32.580815, -60.501327
                            //-32.793840, -60.524446

                            //  new LatLng(-27.544747,-58.880979),
                            //  new LatLng(-27.4568393,-58.7662969))
                    )
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .build(getApplicationContext());
            startActivityForResult(intent, 3);
        });
    }


    /**
     * Fetches current user's info and populates the design elements
     */
    private void getUserData() {
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    View header = navigationView.getHeaderView(0);

                    CustomerObject mCustomer = new CustomerObject();
                    mCustomer.parseData(dataSnapshot);

                    TextView mUsername = header.findViewById(R.id.usernameDrawer);
                    ImageView mProfileImage = header.findViewById(R.id.imageViewDrawer);

                    mUsername.setText(mCustomer.getName());

                    if(!mCustomer.getProfileImage().equals("default"))
                        Glide.with(getApplication()).load(mCustomer.getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Boolean driverFound = false;
    GeoQuery geoQuery;
    List<NearObject> lstNearObjects;

    private void oldgetClosestDriver(){

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.getCoordinates().latitude, pickupLocation.getCoordinates().longitude), MAX_SEARCH_DISTANCE);
        geoQuery.removeAllListeners();

        Handler handler = new Handler();
        int delay = 5000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                if(!driverFound){
                    autocompleteFragmentTo.setVisibility(View.VISIBLE);
                    autocompleteFragmentDos.setVisibility(View.VISIBLE);
                    autocompleteFragmentFromContainer.setVisibility(View.VISIBLE);

                    requestBol = false;
                    mRequest.setText(R.string.call_uber);
                    mRequest.setEnabled(true);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
                    builder.setMessage("No se ha encontrado ningún conductor cerca de usted. Aguarde en LISTA DE ESPERA o realice un nuevo pedido en un momento.")
                            .setPositiveButton("Pedido en espera", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri = Uri.parse("http://ubikargentina.com/");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });


                    builder.show();

                    //Snackbar.make(findViewById(R.id.drawer_layout), R.string.no_driver_near_you, Snackbar.LENGTH_LONG).show();
                    geoQuery.removeAllListeners();
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    return;
                }
                handler.postDelayed(this, delay);
            }
        }, delay);


        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (driverFound || !requestBol){return;}

                DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                            Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                            if (driverFound){
                                return;
                            }

                            if(driverMap.get("service").equals(mAdapter.getSelectedItem().getId())){
                                driverFound = true;

                                mCurrentRide.setDriver(new DriverObject(dataSnapshot.getKey()));

                                mCurrentRide.postRideInfo();


                                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                                getDriverLocation();
                                getDriverInfo();
                                getHasRideEnded();
                                esperandoVehiculo = 1;
                                mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, TIME_OUT);
                                mRequest.setText(R.string.looking_driver);

                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }


            @Override
            public void onKeyExited(String key) {

            }
            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }
            @Override

            public void onGeoQueryReady() {
            }
            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }


    //distancia y calculo de precio
    private Double getDistance(){
        double distance;
        Location locationA = new Location("Point A");
        locationA.setLatitude(pickupLocation.getCoordinates().latitude);
        locationA.setLongitude(pickupLocation.getCoordinates().longitude);

        Location locationB = new Location("Point B");
        locationB.setLatitude(destinationLocation.getCoordinates().latitude);
        locationB.setLongitude(destinationLocation.getCoordinates().longitude);

        Location locationC = new Location("Point C");
        locationC.setLatitude(destinationLocationdos.getCoordinates().latitude);
        locationC.setLongitude(destinationLocationdos.getCoordinates().longitude);

        // distance = locationA.distanceTo(locationB);   // in meters
        distance = locationA.distanceTo(locationB)/1000;


        return distance;
    }



    private Double getPriceTravel(Double distance){
        Double price = 0.00;

        if ( priceKm!=null && priceBase!=null && kmBase !=null){


            price = (distance*priceKm)+priceBase;
        }

        return price;

    }


    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_DISMISS_DIALOG:
                    if (esperandoVehiculo == 1) {
                        mCurrentRide.cancelRide();
                        endRide();
                        esperandoVehiculo = 0;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Get Closest Rider by getting all the drivers available
     * within a radius of the customer current location.
     * radius starts with 1 km and goes up to MAX_SEARCH_DISTANCE
     * Where if no driver is found, and error is thrown saying no
     * driver is found.
     */
    private void getClosestDriver(){

        final DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.getCoordinates().latitude,
                pickupLocation.getCoordinates().longitude),
                MAX_SEARCH_DISTANCE);
        geoQuery.removeAllListeners();

        lstNearObjects = new ArrayList<>();
        final Handler handler = new Handler();
        int delay = 1000; //milliseconds 40000
        final Runnable runnable = new Runnable(){
            public void run(){
                String keyMenor = "";
                double menor = 10000000.0;
                for (int i = 0; i < lstNearObjects.size(); i++) {
                    if(lstNearObjects.get(i).getDistance() < menor) {
                        keyMenor = lstNearObjects.get(i).getKey();
                        menor = lstNearObjects.get(i).getDistance();
                    }
                }

                if(keyMenor.equals("")) {
                    driverFound = false;
                    autocompleteFragmentTo.setVisibility(View.VISIBLE);
                    autocompleteFragmentDos.setVisibility(View.VISIBLE);
                    autocompleteFragmentFromContainer.setVisibility(View.VISIBLE);

                    requestBol = false;
                    mRequest.setText(R.string.call_uber);
                    mRequest.setEnabled(true);
                    /*AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
                    builder.setMessage("No se ha encontrado ningún conductor cerca de usted. Aguarde en LISTA DE ESPERA o realice un nuevo pedido en un momento.")
                            .setPositiveButton("Pedido en espera", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri = Uri.parse("http://ubikargentina.com/");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();*/
                    //Snackbar.make(findViewById(R.id.drawer_layout), R.string.no_driver_near_you, Snackbar.LENGTH_LONG).show();
                    geoQuery.removeAllListeners();
                     mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    return;
                } else {
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(keyMenor);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (!driverFound){
                                    if(driverMap.get("service").equals(mAdapter.getSelectedItem().getId())){
                                        if(destinationLocation != null) {
                                            driverFound = true;

                                            if(mostrandoDialogo == 0) {
                                                mostrandoDialogo = 1;

                                                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
                                                builder.setTitle("Conductor");
                                                builder.setCancelable(false);
                                                builder.setMessage("El conductor tiene una demora de aproximadamente 5/10 minutos. Desea esperar?");
                                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                mCurrentRide.cancelRidecl();
                                                                endRide();
                                                                esperandoVehiculo = 0;

                                                            }
                                                        });

                                               /// mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, TIME_OUT2);

                                                final AlertDialog alert = builder.create();
                                                alert.show();  //Muestra dialogo.

                                                //Crea handler, em 10 segundos cierra el dialogo.
                                                new Handler().postDelayed(new Runnable(){
                                                    public void run(){
                                                        if (alert.isShowing()) {
                                                            alert.dismiss();
                                                            mCurrentRide.cancelRidecl();
                                                            endRide();
                                                            esperandoVehiculo = 0;

                                                        }
                                                    }
                                                }, 35000);

                                                // builder.create();
                                                // builder.show();


                                            }


                                            AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
                                            builder.setMessage("A continuación vamos a buscar el conductor mas cercano a su ubación.")
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            showingDialog1();
                                                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                                            dialog.dismiss();
                                                        }

                                                    });
                                            builder.create();
                                            builder.show();

                                            activar = false;

                                            mCurrentRide.setDriver(new DriverObject(dataSnapshot.getKey()));
                                            mCurrentRide.setDestination(destinationLocation);
                                            mCurrentRide.setDestinationdos(destinationLocationdos);

                                            mCurrentRide.postRideInfo();

                                             mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                                            getDriverLocation();
                                            getDriverInfo();
                                            getHasRideEnded();


                                            mRequest.setText(R.string.looking_driver);
                                        }
                                    } else {
                                        if(!driverFound){
                                            autocompleteFragmentTo.setVisibility(View.VISIBLE);
                                            autocompleteFragmentFromContainer.setVisibility(View.VISIBLE);

                                            requestBol = false;
                                            mRequest.setText(R.string.call_uber);
                                            mRequest.setEnabled(true);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
                                            builder.setMessage("No se ha encontrado ningún conductor cerca de usted. Por favor realice su solicitud nuevamente en breve.")
                                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();

                                                        }
                                                    })
                                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            builder.show();
                                            //Snackbar.make(findViewById(R.id.drawer_layout), R.string.no_driver_near_you, Snackbar.LENGTH_LONG).show();
                                            geoQuery.removeAllListeners();
                                              mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                        }
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                //handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(runnable, delay);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                NearObject nearObject = new NearObject();
                nearObject.setKey(key);
                nearObject.setDistance(
                        distance(pickupLocation.getCoordinates().latitude, pickupLocation.getCoordinates().longitude,
                                location.latitude, location.longitude));
                lstNearObjects.add(nearObject);
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(key);
            }
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(key);
            }
            @Override

            public void onGeoQueryReady() {
                System.out.println("ready");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.out.println(error.getMessage());
            }
        });

        driverLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int size = (int) dataSnapshot.getChildrenCount();
                if (size == 0) {
                    autocompleteFragmentTo.setVisibility(View.VISIBLE);
                    autocompleteFragmentFromContainer.setVisibility(View.VISIBLE);

                    requestBol = false;
                    mRequest.setText(R.string.call_uber);
                    mRequest.setEnabled(true);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
                    builder.setMessage("No se ha encontrado ningún conductor cerca de usted. Por favor realice su solicitud nuevamente en breve.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }

                            });
                    builder.show();
                    //Snackbar.make(findViewById(R.id.drawer_layout), R.string.no_driver_near_you, Snackbar.LENGTH_LONG).show();
                      mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    return;
                } else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPriceKm(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Price");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    priceKm = Float.valueOf(dataSnapshot.child("km").getValue().toString());
                    priceBase = Float.valueOf(dataSnapshot.child("Base").getValue().toString());
                    kmBase = Float.valueOf(dataSnapshot.child("KilometrosdeBase").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; // in kilometers, change to  3958.75 for miles

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }


    /**
     * Get's most updated driver location and it's always checking for movements.
     * Even though we used geofire to push the location of the driver we can use a normal
     * Listener to get it's location with no problem.
     * 0 -> Latitude
     * 1 -> Longitudde
     */
    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    private void getDriverLocation(){
        if(mCurrentRide.getDriver().getId() == null){return;}
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(mCurrentRide.getDriver().getId()).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LocationObject mDriverLocation = new LocationObject(new LatLng(locationLat, locationLng), "");
                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.getCoordinates().latitude);
                    loc1.setLongitude(pickupLocation.getCoordinates().longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(mDriverLocation.getCoordinates().latitude);
                    loc2.setLongitude(mDriverLocation.getCoordinates().longitude);

                    float distance = loc1.distanceTo(loc2);


                    esperandoVehiculo = 0;
                    if (distance<100){
                        mRequest.setText(R.string.driver_here);

                        mRequest.setEnabled(false);
                    }else{
                        mRequest.setText(getString(R.string.driver_found));
                        mRequest.setEnabled(false);

                    }



                    mCurrentRide.getDriver().setLocation(mDriverLocation);

                    markerList.remove(mDriverMarker);

                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(mCurrentRide.getDriver().getLocation().getCoordinates()).title("Su Vehículo").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_carr)));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });





    }

    /**
     * Get all the user information that we can get from the user's database.
     */
    private void getDriverInfo(){
        if(mCurrentRide == null){return;}

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mCurrentRide.getDriver().getId());
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){


                    mDriverInfo.setVisibility(View.VISIBLE);
                    mRadioLayout.setVisibility(View.GONE);

                    mCurrentRide.getDriver().parseData(dataSnapshot);

                    mDriverName.setText(mCurrentRide.getDriver().getName());
                    mDriverCar.setText(mCurrentRide.getDriver().getCar());
                    Glide.with(getApplication())
                            .load(mCurrentRide.getDriver().getProfileImage())
                            .apply(RequestOptions.circleCropTransform())
                            .into(mDriverProfileImage);


                    mRatingText.setText(String.valueOf(mCurrentRide.getDriver().getRatingsAvg()));
                }
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
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("ride_info").child(mCurrentRide.getId());
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){return;}
                if(!Boolean.parseBoolean(dataSnapshot.child("ended").getValue().toString())){return;}

                if(Boolean.parseBoolean(dataSnapshot.child("cancelled").getValue().toString())){
                    new AlertDialog.Builder(CustomerMapActivity.this)
                            .setTitle("Cancelación de Viaje")
                            .setMessage("El conductor no se encuentra disponible en este momento, " +
                                    "le recomendamos realizar otra solicitud nuevamente para buscar otro conductor.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, null).show();
                }
                 /*if(Boolean.parseBoolean(dataSnapshot.child("cancelledcl").getValue().toString())){
                    new AlertDialog.Builder(CustomerMapActivity.this)
                            .setTitle("Cancelación de Viaje")
                            .setMessage("Usted a cancelado el viaje correctamente, " +
                                    "Solicitamos no realizar esta tarea repetidamente o podrá ser sancionado.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, null).show();
                }*/
                endRide();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void viajecliente(){
        if(mCurrentRide == null){return;}
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("ride_info").child(mCurrentRide.getId());
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){return;}
                if(!Boolean.parseBoolean(dataSnapshot.child("ended").getValue().toString())){return;}

                if(Boolean.parseBoolean(dataSnapshot.child("cancelledcl").getValue().toString())){
                    new AlertDialog.Builder(CustomerMapActivity.this)
                            .setTitle("Cancelación de Viaje")
                            .setMessage("Usted a cancelado el viaje correctamente, " +
                                    "Solicitamos no realizar esta tarea repetidamente o podrá ser sancionado.")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, null).show();
                }

                HashMap map = new HashMap();
                map.put("distanceKm", getDistance());
                map.put("priceTravel", getPriceTravel(getDistance()));

                endRider();
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

        showingDialog();

        DialogCobrar dialog= new DialogCobrar(String.format("%.2f",getPriceTravel(getDistance())),this);
        dialog.show(getSupportFragmentManager(),"DIALOG_COBRAR");
        dialog.setCancelable(false);

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        requestBol = false;
        if(geoQuery != null)
            geoQuery.removeAllListeners();
        if (driverLocationRefListener != null)
            driverLocationRef.removeEventListener(driverLocationRefListener);
        if (driveHasEndedRefListener != null)
            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);






        if (mCurrentRide != null && driverFound){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mCurrentRide.getDriver().getId()).child("customerRequest");
            driverRef.removeValue();
        }

        pickupLocation = null;
        destinationLocation = null;
        destinationLocationdos = null;

        driverFound = false;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId, (key, error) -> {});

        if(destinationMarker != null){
            destinationMarker.remove();
        }
        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
        }
        mMap.clear();
        mRequest.setText(getString(R.string.call_uber));
        mRequest.setEnabled(true);

        mDriverInfo.setVisibility(View.GONE);
        mRadioLayout.setVisibility(View.VISIBLE);

        mDriverName.setText("");
        mDriverCar.setText(getString(R.string.destination));
        mDriverProfileImage.setImageResource(R.mipmap.ic_default_user);

        autocompleteFragmentTo.setVisibility(View.VISIBLE);
        autocompleteFragmentFromContainer.setVisibility(View.VISIBLE);

        autocompleteFragmentTo.setText(getString(R.string.to));
        autocompleteFragmentDos.setText(getString(R.string.to));
        autocompleteFragmentFrom.setText(getString(R.string.from));
        mCurrentLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_on_grey_24dp));
    }



    private void endRider(){

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        requestBol = false;
        if(geoQuery != null)
            geoQuery.removeAllListeners();
        if (driverLocationRefListener != null)
            driverLocationRef.removeEventListener(driverLocationRefListener);
        if (driveHasEndedRefListener != null)
            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);




        if (mCurrentRide != null && driverFound){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mCurrentRide.getDriver().getId()).child("customerRequest");
            driverRef.removeValue();
        }

        pickupLocation = null;
        destinationLocation = null;
        destinationLocationdos = null;

        driverFound = false;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId, (key, error) -> {});

        if(destinationMarker != null){
            destinationMarker.remove();
        }
        if(pickupMarker != null){
            pickupMarker.remove();
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
        }
        mMap.clear();
        mRequest.setText(getString(R.string.call_uber));
        mRequest.setEnabled(true);

        mDriverInfo.setVisibility(View.GONE);
        mRadioLayout.setVisibility(View.VISIBLE);

        mDriverName.setText("");
        mDriverCar.setText(getString(R.string.destination));
        mDriverProfileImage.setImageResource(R.mipmap.ic_default_user);

        autocompleteFragmentTo.setVisibility(View.VISIBLE);
        autocompleteFragmentFromContainer.setVisibility(View.VISIBLE);

        autocompleteFragmentTo.setText(getString(R.string.to));
        autocompleteFragmentDos.setText(getString(R.string.to));
        autocompleteFragmentFrom.setText(getString(R.string.from));
        mCurrentLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_on_grey_24dp));
    }


    public void showingDialog() {
        try {
            final Dialog dialog = new Dialog(CustomerMapActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_ride_review);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            Button mConfirm = dialog.findViewById(R.id.confirm);
            final RatingBar mRate = dialog.findViewById(R.id.rate);
            TextView mName = dialog.findViewById(R.id.name);;
            ImageView mImage = dialog.findViewById(R.id.image);

            mName.setText(mCurrentRide.getDriver().getName());

            if(mCurrentRide.getDriver().getProfileImage() != null) {
                if (!mCurrentRide.getDriver().getProfileImage().equals("default")) {
                    Glide.with(CustomerMapActivity.this).load(mCurrentRide.getDriver().getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mImage);
                }
            }

            mConfirm.setOnClickListener(view -> {
                if (mRate.getNumStars() == 0) {
                    return;
                }
                mCurrentRide.updateRating(mRate.getRating());
                dialog.dismiss();
                mCurrentRide = new RideObject(CustomerMapActivity.this, null);
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DIALOGO TIMER BUSQUEDA DE CHOFER

    public void showingDialog1() {
        try {
            final Dialog dialog1 = new Dialog(CustomerMapActivity.this);
            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog1.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            dialog1.setCancelable(false);
            dialog1.setContentView(R.layout.dialog_ride_review1);
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            Button mConfirm = dialog1.findViewById(R.id.confirm);
            final RatingBar mRate = dialog1.findViewById(R.id.rate);
            TextView mName = dialog1.findViewById(R.id.name);;
            ImageView mImage = dialog1.findViewById(R.id.image);

            mName.setText(mCurrentRide.getDriver().getName());

            if(mCurrentRide.getDriver().getProfileImage() != null) {
                if (!mCurrentRide.getDriver().getProfileImage().equals("default")) {
                    Glide.with(CustomerMapActivity.this).load(mCurrentRide.getDriver().getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mImage);
                }
            }

            mConfirm.setOnClickListener(view -> {
                dialog1.dismiss();
            });
            dialog1.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog1.isShowing()){
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        getDriverInfo();
                        dialog1.dismiss();
                    }
                }
            }, 30000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Find and update user's location.
     * The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
     * If you're having trouble with battery draining too fast then change these to lower values
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }else{
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }



    }

    boolean zoomUpdated = false;
    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplication()!=null){
                    currentLocation = new LocationObject(new LatLng(location.getLatitude(), location.getLongitude()), "");
                    mCurrentRide.setCurrent(currentLocation);

                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    if(!zoomUpdated){
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        zoomUpdated = true;
                    }

                    if(!getDriversAroundStarted)
                        getDriversAround();

                }
            }
        }
    };

    /**
     * This function returns the name of location given the coordinates
     * of said location
     */
    private void fetchLocationName(){
        if(pickupLocation==null){return;}
        try {

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(currentLocation.getCoordinates().latitude, currentLocation.getCoordinates().longitude, 1);
            if (addresses.isEmpty()) {
                autocompleteFragmentFrom.setText(R.string.waiting_for_location);
            }
            else {
                addresses.size();
                if(addresses.get(0).getThoroughfare() == null) {
                    pickupLocation.setName(addresses.get(0).getLocality());
                }else if(addresses.get(0).getLocality() == null){
                    pickupLocation.setName("Unknown Location");
                }else{
                    pickupLocation.setName(addresses.get(0).getLocality() + ", " + addresses.get(0).getThoroughfare());
                }
                autocompleteFragmentFrom.setText(pickupLocation.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get permissions for our app if they didn't previously exist.
     * requestCode -> the number assigned to the request that we've made.
     * Each request has it's own unique request code.
     */
    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", (dialogInterface, i) -> ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE}, 1))
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplication(), "Por favor habilite los permisos", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                return;
        }
    }




    boolean getDriversAroundStarted = false;
    List<Marker> markerList = new ArrayList<Marker>();
    /**
     * Displays drivers around the user's current
     * location and updates them in real time.
     */
    private void getDriversAround(){
        if(currentLocation==null){return;}
        getDriversAroundStarted = true;
        DatabaseReference driversLocation = FirebaseDatabase.getInstance().getReference().child(("driversAvailable"));


        GeoFire geoFire = new GeoFire(driversLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLocation.getCoordinates().latitude, currentLocation.getCoordinates().longitude), 10000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                for(Marker markerIt : markerList){
                    if(markerIt.getTag() == null || key == null){continue;}
                    if(markerIt.getTag().equals(key))
                        return;
                }


                checkDriverLastUpdated(key);
                LatLng driverLocation = new LatLng(location.latitude, location.longitude);

                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)).position(driverLocation).title(key));
                mDriverMarker.setTag(key);

                markerList.add(mDriverMarker);

            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt : markerList) {
                    if(markerIt.getTag() == null || key == null){continue;}
                    if(markerIt.getTag().equals(key)){
                        markerIt.remove();
                        markerList.remove(markerIt);
                        return;
                    }

                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIt : markerList) {
                    if(markerIt.getTag() == null || key == null){continue;}
                    if(markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    /**
     * Checks if driver has not been updated in a while, if it has been more than x time
     * since the driver location was last updated then remove it from the database.
     * @param key
     *
     * S- Cada 1 minuto se fija si el chofer está dentro de la app, de lo contrario no le asigna viajes.
     * Antes: 60000 - 4000000
     *
     */
    private void checkDriverLastUpdated(String key) {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child("Drivers")
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){return;}

                        if(dataSnapshot.child("last_updated").getValue()!=null){
                            long lastUpdated = Long.parseLong(dataSnapshot.child("last_updated").getValue().toString());
                            long currentTimestamp = System.currentTimeMillis();

                            if(currentTimestamp - lastUpdated > 4000000){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable");
                                GeoFire geoFire = new GeoFire(ref);
                                geoFire.removeLocation(dataSnapshot.getKey(), (key1, error) -> {});
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }


    private void logOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(CustomerMapActivity.this, LauncherActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Get Route from pickup to destination, showing the route to the user
     */
    public void getRouteToMarker(LatLng origin, LatLng destination1, LatLng destination2) {
        DirectionsRequest directionsRequest = new DirectionsRequest();
        directionsRequest.origin = origin;
        directionsRequest.destination = destination1;
        directionsRequest.travelMode = TravelMode.DRIVING;
        directionsRequest.unitSystem = UnitSystem.METRIC;
        directionsService.route(directionsRequest, new DirectionsCallback() {
            @Override
            public void onCallback(DirectionsResult directionsResult, DirectionsStatus directionsStatus) {
                if (directionsStatus == DirectionsStatus.OK) {
                    DirectionsRenderer directionsRenderer = new DirectionsRenderer();
                    directionsRenderer.setDirections(directionsResult);
                    directionsRenderer.setMap(map);
                } else {
                    Window.alert("Error: " + directionsStatus);
                }
            }
        });
        DirectionsRequest directionsRequest2 = new DirectionsRequest();
        directionsRequest2.origin = origin;
        directionsRequest2.destination = destination2;
        directionsRequest2.travelMode = TravelMode.DRIVING;
        directionsRequest2.unitSystem = UnitSystem.METRIC;
        directionsService.route(directionsRequest2, new DirectionsCallback() {
            @Override
            public void onCallback(DirectionsResult directionsResult, DirectionsStatus directionsStatus) {
                if (directionsStatus == DirectionsStatus.OK) {
                    DirectionsRenderer directionsRenderer = new DirectionsRenderer();
                    directionsRenderer.setDirections(directionsResult);
                    directionsRenderer.setMap(map);
                } else {
                    Window.alert("Error: " + directionsStatus);
                }
            }
        });
    }
    private List<Polyline> polylines  = new ArrayList<>();

    /**
     * Remove route polylines from the map
     */
    private void erasePolylines(){
        if(polylines==null){return;}
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



    /**
     * Override the activity's onActivityResult(), check the request code, and
     * do something with the returned place data (in this example it's place name and place ID).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            LocationObject mLocation;

            if (currentLocation == null) {
                // Snackbar.make(findViewById(R.id.drawer_layout), "Por favor active el gps o localización en su celular", Snackbar.LENGTH_LONG).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Por favor active el gps o localización en su celular")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();

                return;
            }
            Place place = Autocomplete.getPlaceFromIntent(data);

            mLocation = new LocationObject(place.getLatLng(), place.getAddress());


            currentLocation = new LocationObject(new LatLng(currentLocation.getCoordinates().latitude, currentLocation.getCoordinates().longitude), "");


            if (requestCode == 1) {
                mMap.clear();
                destinationLocation = mLocation;
                destinationLocationdos = mLocation;
                destinationMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_filled)).position(destinationLocation.getCoordinates()).title("Destino"));
                destinationMarker2 = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_filled)).position(destinationLocationdos.getCoordinates()).title("Destino2"));
                mCurrentRide.setDestination(destinationLocation);
                mCurrentRide.setDestinationdos(destinationLocationdos);
                autocompleteFragmentTo.setText(destinationLocation.getName());
                autocompleteFragmentDos.setText(destinationLocationdos.getName());
                if(pickupLocation != null){
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation.getCoordinates()).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radios)));
                      mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }else if(requestCode == 2){
                mMap.clear();
                pickupLocation = mLocation;
                pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation.getCoordinates()).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radios)));
                mCurrentRide.setPickup(pickupLocation);
                autocompleteFragmentFrom.setText(pickupLocation.getName());
                if(destinationLocation != null){
                    destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLocation.getCoordinates()).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_filled)));
                    destinationMarker2 = mMap.addMarker(new MarkerOptions().position(destinationLocationdos.getCoordinates()).title("Destino2").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_filled)));
                       mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    Double price = getPriceTravel(getDistance());
                    textViewPrecioSolicitud.setText(getString(R.string.price)+"$"+String.format("%.2f",price));
                    textViewPrecioSolicitud2.setText(getString(R.string.price)+getDistance());
                }

            }

            erasePolylines();
            getRouteToMarker();
            getDriversAround();

            mRequest.setText(getString(R.string.call_uber));
            mRequest.setEnabled(true);


        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i("PLACE_AUTOCOMPLETE", status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            initPlacesAutocomplete();
        }
        initPlacesAutocomplete();


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




    public void cobrado() {

        requestBol = false;
        if(geoQuery != null)
            geoQuery.removeAllListeners();
        if (driverLocationRefListener != null)
            driverLocationRef.removeEventListener(driverLocationRefListener);
        if (driveHasEndedRefListener != null)
            driveHasEndedRef.removeEventListener(driveHasEndedRefListener);


        driverFound = false;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
            }
        });


        mMap.clear();
        mRequest.setText(getString(R.string.call_uber));

        mDriverInfo.setVisibility(View.GONE);
        mRadioLayout.setVisibility(View.VISIBLE);
        mDriverName.setText("");
        mDriverCar.setText(getString(R.string.destination));



        textViewPrecioSolicitud.setText("");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.history) {
            Intent intent = new Intent(CustomerMapActivity.this, HistoryActivity.class);
            intent.putExtra("customerOrDriver", "Customers");
            startActivity(intent);
        } else if (id == R.id.settings) {
            Intent intent = new Intent(CustomerMapActivity.this, CustomerSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.sos) {
            Intent intent = new Intent(CustomerMapActivity.this, sosact.class);
            startActivity(intent);
        } else if (id == R.id.share) {
            Intent intent = new Intent(CustomerMapActivity.this, Share.class);
            startActivity(intent);

        } else if (id == R.id.logout) {
            logOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
