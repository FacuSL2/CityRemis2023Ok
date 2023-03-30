package com.creativedesign.CityRemis.Login;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.creativedesign.CityRemis.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment Responsible for registering a new user
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private EditText    mName,
                        mEmail,
                        mPhone,
                        mPassword;


    private SegmentedButtonGroup mRadioGroup;

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_registration, container, false);
        else
            container.removeView(view);


        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeObjects();
    }

    /**
     * Register the user, but before that check if every field is correct.
     * After that registers the user and creates an entry for it oin the database
     */
    private void register(){
        if(mName.getText().length()==0) {
            mName.setError("por favor complete este campo");
            return;
        }
        if(mEmail.getText().length()==0) {
            mEmail.setError("por favor complete este campo");
            return;
        }
        if(mPassword.getText().length()==0) {
            mPassword.setError("por favor complete este campo");
            return;
        }
        if(mPhone.getText().length()==0) {
            mPhone.setError("por favor complete este campo");
            return;
        }
        if(mPassword.getText().length()< 6) {
            mPassword.setError("la contraseña debe tener al menos 6 caracteres");
            return;
        }

        final String name = mName.getText().toString();
        final String phone = mPhone.getText().toString();
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        final String accountType;
        int selectId = mRadioGroup.getPosition();

        switch (selectId){
            case 0:
                accountType = "Drivers";
                break;
            case 1:
                accountType = "Customers";
                break;
            case 2:
                accountType = "Admin";
            break;
            default:
                accountType = "Customers";
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
            if(!task.isSuccessful()){
                Snackbar.make(view.findViewById(R.id.layout), "error en el registro", Snackbar.LENGTH_SHORT).show();
            }else{
                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Map newUserMap = new HashMap();
                newUserMap.put("name", name);
                newUserMap.put("email", email);
                newUserMap.put("phone", phone);
                newUserMap.put("profileImageUrl", "default");
                if(accountType.equals("Drivers")){
                    newUserMap.put("service", "type_1");
                    newUserMap.put("activated", true);
                    newUserMap.put("tripsAvailable", "10000");
                }
                FirebaseDatabase.getInstance().getReference().child("Users").child(accountType).child(user_id).updateChildren(newUserMap);
            }
        });

    }

    /**
     * Initializes the design Elements and calls clickListeners for them
     */
    private void initializeObjects(){
        mEmail = view.findViewById(R.id.email);
        mName = view.findViewById(R.id.name);
        mPhone = view.findViewById(R.id.phone);
        mPassword = view.findViewById(R.id.password);
        Button mRegister = view.findViewById(R.id.register);
        mRadioGroup = view.findViewById(R.id.radioRealButtonGroup);

        mRadioGroup.setPosition(1, false); //0-CUSTOMER   1-DRIVER     2-ADMIN
        mRegister.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register) {
            register();
        }
    }
}