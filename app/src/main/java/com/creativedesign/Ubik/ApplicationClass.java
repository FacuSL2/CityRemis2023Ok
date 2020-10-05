package com.creativedesign.Ubik;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;

public class ApplicationClass extends Application {

    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(false)
                .init();
        if(mAuth.getCurrentUser() != null){
            //OneSignal.setEmail(mAuth.getCurrentUser().getEmail());
            OneSignal.sendTag("UID", mAuth.getCurrentUser().getUid());
            OneSignal.sendTag("email", mAuth.getCurrentUser().getEmail());
        }
    }
}
