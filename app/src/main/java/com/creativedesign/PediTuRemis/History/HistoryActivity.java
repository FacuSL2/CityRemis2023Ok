package com.creativedesign.PediTuRemis.History;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.creativedesign.PediTuRemis.Objects.RideObject;
import com.creativedesign.PediTuRemis.Adapters.HistoryAdapter;
import com.creativedesign.PediTuRemis.R;
import com.creativedesign.PediTuRemis.Utils.PayPalConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



/**
 * This activity displays a list of all the previous drives made
 * by the user.
 *
 * If the current user is a driver then it also displays a space with the
 * current money available for payout and a space for the user to place
 * the paypal email to which it is sent
 */
public class HistoryActivity extends AppCompatActivity {
    private String customerOrDriver, userId;

    private RecyclerView mHistoryRecyclerView;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView.LayoutManager mHistoryLayoutManager;

    private TextView mBalance;

    private Double balance = 0.0;

    private Button mPayout;

    private EditText mPayoutEmail;

    CardView mPayoutLayout;

    String idRef = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mBalance = findViewById(R.id.balance);
        mPayout = findViewById(R.id.payout);
        mPayoutEmail = findViewById(R.id.payoutEmail);
        mPayoutLayout = findViewById(R.id.payout_layout);

        mHistoryRecyclerView = findViewById(R.id.historyRecyclerView);
        mHistoryRecyclerView.setNestedScrollingEnabled(false);
        mHistoryRecyclerView.setHasFixedSize(true);
        mHistoryLayoutManager = new LinearLayoutManager(HistoryActivity.this);
        mHistoryRecyclerView.setLayoutManager(mHistoryLayoutManager);
        mHistoryAdapter = new HistoryAdapter(resultsHistory, HistoryActivity.this);
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);




        customerOrDriver = getIntent().getExtras().getString("customerOrDriver");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(customerOrDriver.equals("Drivers")){
            mPayoutLayout.setVisibility(View.VISIBLE);
            idRef = "driverId";
        }else{
            idRef = "customerId";
        }

        mPayout.setOnClickListener(view -> payoutRequest());

        getUserHistoryIds();
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
     * Fetch all of the rides that are completed and populate the
     * design elements
     */
    private void getUserHistoryIds() {

        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseDatabase.getInstance().getReference().child("ride_info").orderByChild(idRef).equalTo(driverId);

        query.addChildEventListener(new ChildEventListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!dataSnapshot.exists()){return;}

                RideObject mRide = new RideObject();
                mRide.parseData(dataSnapshot);


                balance += mRide.getRidePrice();


                mBalance.setText(getString(R.string.balance) + ":" + String.format("%.2f", balance) + " $");


                resultsHistory.add(0,  mRide);
                mHistoryAdapter.notifyDataSetChanged();
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
    private ArrayList<RideObject> resultsHistory = new ArrayList<>();


    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    ProgressDialog progress;

    /**
     * Start a payout request to the server.
     *
     * Before that checks if the email is not empty
     */
    private void payoutRequest() {
        progress = new ProgressDialog(this);
        progress.setTitle("Processing your payout");
        progress.setMessage("please wait");
        progress.setCancelable(false);
        progress.show();

        final OkHttpClient client = new OkHttpClient();
        JSONObject postData = new JSONObject();
        try {
            postData.put("uid", FirebaseAuth.getInstance().getUid());
            postData.put("email", mPayoutEmail.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postData.toString());

        final Request request = new Request.Builder()
                .url(PayPalConfig.PAYPAL_PAYOUT_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("cache-control", "no-cache")
                .addHeader("Authorization", "Your Token")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progress.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) {
                int responseCode = response.code();
                if (response.isSuccessful()){
                    switch(responseCode){
                        case 200:
                            Snackbar.make(findViewById(R.id.layout), "Payout Successful!", Snackbar.LENGTH_LONG).show();
                            balance = 0.0;
                            mBalance.setText(getString(R.string.balance) + ":" + balance + " $");
                            break;
                        default:
                            Snackbar.make(findViewById(R.id.layout), "Error: Could not complete Payout", Snackbar.LENGTH_LONG).show();
                            break;
                    }
                }
                progress.dismiss();
            }
        });
    }
}















