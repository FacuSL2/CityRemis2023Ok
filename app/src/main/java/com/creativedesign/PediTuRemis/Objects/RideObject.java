package com.creativedesign.PediTuRemis.Objects;

import android.app.Activity;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Object of a ride
 *
 * It is responsible for containing all the info of the ride-
 * It also posts the ride request, records it,...
 */
public class RideObject {

    private String id;

    private LocationObject pickup,
                    current,
                    destination;

    private String  requestService = "type_1", car = "--";


    private DriverObject mDriver;
    private CustomerObject mCustomer;

    Activity activity;

    Boolean ended = false, customerPaid = false, driverPaid = false, cancelled = false;

    private float rideDistance = 0;
    private Double ridePrice = 0.0;
    private Long timestamp;
    private int rating = 0;

    public RideObject(Activity activity, String id){
        this.id = id;
        this.activity = activity;
    }

    public RideObject(){
    }

    public void postRide(){

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(userId, new GeoLocation(pickup.getCoordinates().latitude, pickup.getCoordinates().longitude), (key, error) -> {});
    }

    public int checkRide(){
        if (current == null) {
            Toast.makeText(activity.getApplicationContext(), "No podemos obtener localización", Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (destination == null) {
            Toast.makeText(activity.getApplicationContext(), "Por favor, escriba la dirección de destino", Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (pickup == null) {
            Toast.makeText(activity.getApplicationContext(), "Por favor, escriba la dirección de origen", Toast.LENGTH_SHORT).show();
            return -1;
        }

        return 0;
    }


    public void parseData(DataSnapshot dataSnapshot){
        id = dataSnapshot.getKey();

        pickup = new LocationObject();
        destination = new LocationObject();

        if(dataSnapshot.child("pickup").child("name").getValue()!=null){
            pickup.setName(dataSnapshot.child("pickup").child("name").getValue().toString());
        }
        if(dataSnapshot.child("destination").child("name").getValue()!=null){
            destination.setName(dataSnapshot.child("destination").child("name").getValue().toString());
        }
        if(dataSnapshot.child("pickup").child("lat").getValue()!=null && dataSnapshot.child("pickup").child("lng").getValue()!=null){
            pickup.setCoordinates(
                    new LatLng(Double.parseDouble(dataSnapshot.child("pickup").child("lat").getValue().toString()),
                                Double.parseDouble(dataSnapshot.child("pickup").child("lng").getValue().toString())));
        }
        if(dataSnapshot.child("destination").child("lat").getValue()!=null && dataSnapshot.child("destination").child("lng").getValue()!=null){
            destination.setCoordinates(
                    new LatLng(Double.parseDouble(dataSnapshot.child("destination").child("lat").getValue().toString()),
                                Double.parseDouble(dataSnapshot.child("destination").child("lng").getValue().toString())));
        }


        if(dataSnapshot.child("customerId").getValue() != null){
            mCustomer = new CustomerObject(dataSnapshot.child("customerId").getValue().toString());
        }
        if(dataSnapshot.child("driverId").getValue() != null){
            mDriver = new DriverObject(dataSnapshot.child("driverId").getValue().toString());
        }
        if(dataSnapshot.child("ended").getValue() != null){
            ended = Boolean.parseBoolean(dataSnapshot.child("ended").getValue().toString());
        }
        if(dataSnapshot.child("cancelled").getValue() != null){
            cancelled = Boolean.parseBoolean(dataSnapshot.child("cancelled").getValue().toString());
        }


        if(dataSnapshot.child("timestamp").getValue() != null){
            timestamp = Long.valueOf(dataSnapshot.child("timestamp").getValue().toString());
        }
        if(dataSnapshot.child("price").getValue() != null){
            ridePrice = Double.valueOf(dataSnapshot.child("price").getValue().toString());
        }
        if(dataSnapshot.child("car").getValue() != null){
            car = dataSnapshot.child("car").getValue().toString();
        }
        if(dataSnapshot.child("customerPaid").getValue() != null){
            customerPaid = true;
        }
        if(dataSnapshot.child("driverPaidOut").getValue() != null){
            driverPaid = true;
        }
        if(dataSnapshot.child("rating").getValue() != null){
            rating = Integer.parseInt(dataSnapshot.child("rating").getValue().toString());
        }
    }

    public void postRideInfo(){
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("ride_info");

        id =  driverRef.push().getKey();
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap map = new HashMap();
        map.put("service", requestService);
        map.put("customerId", customerId);
        map.put("car", mDriver.getCar());
        map.put("cancelled", false);
        map.put("driverId", mDriver.getId());
        map.put("ended", false);
        map.put("destination/name", destination.getName());
        map.put("destination/lat", destination.getCoordinates().latitude);
        map.put("destination/lng", destination.getCoordinates().longitude);
        map.put("pickup/name", pickup.getName());
        map.put("pickup/lat", pickup.getCoordinates().latitude);
        map.put("pickup/lng", pickup.getCoordinates().longitude);

        driverRef.child(id).updateChildren(map);
    }

    public void recordRide(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ride_info").child(id);

        HashMap map = new HashMap();
        map.put("ended", true);
        map.put("rating", 0);
        map.put("distance", rideDistance);
        map.put("timestamp", ServerValue.TIMESTAMP);

        ref.updateChildren(map);
    }

    public String getDate() {
        if(timestamp == null){return "--";}
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("MM-dd-yyyy, hh:mm", cal).toString();
        return date;
    }

    public void cancelRide(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ride_info").child(id);
        HashMap map = new HashMap();
        map.put("cancelled", true);
        map.put("ended", true);
        ref.updateChildren(map);
    }

    public DriverObject getDriver() {
        return mDriver;
    }
    public void setDriver(DriverObject mDriver) {
        this.mDriver = mDriver;
    }

    public CustomerObject getCustomer() {
        return mCustomer;
    }
    public void setCustomer(CustomerObject mCustomer) {
        this.mCustomer = mCustomer;
    }

    public LocationObject getPickup() {
        return pickup;
    }
    public void setPickup(LocationObject pickup) {
        this.pickup = pickup;
    }

    public LocationObject getCurrent() {
        return current;
    }
    public void setCurrent(LocationObject current) {
        this.current = current;
    }

    public LocationObject getDestination() {
        return destination;
    }
    public void setDestination(LocationObject destination) {
        this.destination = destination;
    }

    public String getRequestService() {
        return requestService;
    }
    public void setRequestService(String requestService) {
        this.requestService = requestService;
    }

    public float getRideDistance() {
        return rideDistance;
    }
    public void setRideDistance(float rideDistance) {
        this.rideDistance = rideDistance;
    }

    public String getId() {
        return id;
    }

    public Boolean getEnded() {
        return ended;
    }

    public Double getRidePrice() {
        return ridePrice;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Boolean getCustomerPaid() {
        return customerPaid;
    }

    public String getCar() {
        return car;
    }

    public int getRating() {
        return rating;
    }

    public String getPriceString(){
        return String.format("%.2f", ridePrice);
    }

    public Boolean getCancelled() {
        return cancelled;
    }
}
