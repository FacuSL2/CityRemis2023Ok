package com.creativedesign.Ubik.History;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.creativedesign.Ubik.Objects.RideObject;
import com.creativedesign.Ubik.R;
import com.creativedesign.Ubik.Utils.PayPalConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This activity displays a single previous ride in detail.
 *
 * If you are a customer then it allows you to both rate the driver
 * and pay for the ride.
 */
public class HistorySingleActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback {
    private String rideId;

    private TextView mPickup
                    ,mDestination
                    ,mPrice
                    ,mCar
                    ,mDate
                    ,userName
                    ,userPhone,
                    userMail;

    private ImageView userImage;

    private RatingBar mRatingBar;

    private Button mPay;

    private DatabaseReference historyRideInfoDb;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    LinearLayout mRatingBarContainer;

    RideObject mRide = new RideObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        polylines = new ArrayList<>();

        rideId = getIntent().getExtras().getString("rideId");

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);


        mDestination = findViewById(R.id.destination_location);
        mPickup = findViewById(R.id.pickup_location);
        mCar = findViewById(R.id.car);
        mDate = findViewById(R.id.time);
        mPrice = findViewById(R.id.price);


        userName = findViewById(R.id.userName);
        userPhone = findViewById(R.id.userPhone);
        userMail = findViewById(R.id.email);
        userImage = findViewById(R.id.userImage);

        mRatingBar = findViewById(R.id.ratingBar);
        mRatingBarContainer = findViewById(R.id.ratingBar_container);

        mPay = findViewById(R.id.pay);


        historyRideInfoDb = FirebaseDatabase.getInstance().getReference().child("ride_info").child(rideId);
        getRideInformation();
        setupToolbar();
    }

    /**
     * Sets up toolbar with custom text and a listener
     * to go back to the previous activity
     */
    private void setupToolbar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.your_trips));
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        myToolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Fetches the info on the current ride and populates the design elements
     */
    private void getRideInformation() {
        historyRideInfoDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){return;}
                mRide.parseData(dataSnapshot);
                if(mRide.getDriver().getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    getUserInformation("Customers", mRide.getCustomer().getId());
                    mRatingBarContainer.setVisibility(View.GONE);
                }else{
                    getUserInformation("Drivers", mRide.getDriver().getId());
                    displayCustomerRelatedObjects();
                }


                mDate.setText(mRide.getDate());
                mPrice.setText(mRide.getPriceString() + " $");
                mDestination.setText(mRide.getDestination().getName());
                mPickup.setText(mRide.getPickup().getName());
                mCar.setText(mRide.getCar());
                mRatingBar.setRating(mRide.getRating());



                getRouteToMarker();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Displays the elements that are only available to the customer:
     *  - Rating bar
     *  - pay button
     */
    private void displayCustomerRelatedObjects() {
        mRatingBarContainer.setVisibility(View.VISIBLE);

        mPay.setVisibility(View.VISIBLE);


        mRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            historyRideInfoDb.child("rating").setValue(rating);
            DatabaseReference mDriverRatingDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mRide.getDriver().getId()).child("rating");
            mDriverRatingDb.child(rideId).setValue(rating);
        });
        if(mRide.getCustomerPaid()){
            mPay.setEnabled(false);
        }else{
            mPay.setEnabled(true);
        }
        mPay.setOnClickListener(view -> payPalPayment());
    }

    private int PAYPAL_REQUEST_CODE = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    private void payPalPayment() {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(mRide.getRidePrice()), "USD", "Ride",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirm != null){
                    try{
                        JSONObject jsonObj = new JSONObject(confirm.toJSONObject().toString());

                        String paymentResponse = jsonObj.getJSONObject("response").getString("state");

                        if(paymentResponse.equals("approved")){
                            Toast.makeText(getApplicationContext(), "Payment successful", Toast.LENGTH_LONG).show();
                            historyRideInfoDb.child("customerPaid").setValue(true);
                            mPay.setEnabled(false);
                            mPay.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }else{
                Toast.makeText(getApplicationContext(), "Payment unsuccessful", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    /**
     * Fetches other user information and populates the relevant design elements
     * @param otherUserDriverOrCustomer - String "customer" or "driver"
     * @param otherUserId - id of the user whom we want to fetch the info of
     */
    private void getUserInformation(String otherUserDriverOrCustomer, String otherUserId) {
        DatabaseReference mOtherUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserDriverOrCustomer).child(otherUserId);
        mOtherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name") != null){
                        userName.setText(map.get("name").toString());
                    }
                    if(map.get("phone") != null){
                        userPhone.setText(map.get("phone").toString());
                    }
                    if(map.get("profileImageUrl") != null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).apply(RequestOptions.circleCropTransform()).into(userImage);
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    /**
     * Get Route from pickup to destination, showing the route to the user
     */
    private void getRouteToMarker() {
        String serverKey = getResources().getString(R.string.google_maps_key);
        if (mRide.getPickup() != null && mRide.getDestination() != null){
            GoogleDirection.withServerKey(serverKey)
                    .from(mRide.getDestination().getCoordinates())
                    .to(mRide.getPickup().getCoordinates())
                    .transportMode(TransportMode.DRIVING)
                    .execute(this);

            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_finish)).position(mRide.getDestination().getCoordinates()).title("destination"));
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start)).position(mRide.getPickup().getCoordinates()).title("pickup"));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));

    }


    private List<Polyline> polylines;
    /**
     * Show map within the pickup and destination marker,
     * This will make sure everything is displayed to the user
     * @param route - route between pickup and destination
     */
    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
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

        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
    }


}
