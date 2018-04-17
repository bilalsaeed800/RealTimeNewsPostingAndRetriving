package com.example.bilal.hci_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class AddInterest_Activity extends FragmentActivity implements OnMapReadyCallback{

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    final DatabaseReference usersRef = ref.child("users");
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    public static final int RC_SIGN_IN = 1;
    public static final int PLACE_PICKER_REQUEST = 2;

    private AlertDialog.Builder alert;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private boolean isUserNew=false,isActivityResumed,wantToAddAnotherInterest=true;
    private ArrayList<String> mArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interest_);

        mAuth = FirebaseAuth.getInstance();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();



                if (user != null) {
                        doMyWork(); //Bhai jo add interest wala kaam karna iss function men karna.

                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder().setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);

                }
            }
        };









    }

    private void doMyWork()
    {


         if(isActivityResumed&&alert!=null){
            alert.show();
             alert=null;}
        else{
        usersRef.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    isUserNew = true;
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(AddInterest_Activity.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                } else {
                    startActivity(new Intent(AddInterest_Activity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityResumed=true;
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityResumed=false;
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            // Successfully signed in
            if (resultCode == RESULT_OK) {

                Toast.makeText(AddInterest_Activity.this,"Signed In",Toast.LENGTH_SHORT).show();
                doMyWork();
            } else if(resultCode == RESULT_CANCELED){

                Toast.makeText(AddInterest_Activity.this,"Cancelled Sign In",Toast.LENGTH_SHORT).show();
                finish();
            }



        }
        if(requestCode==PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(data, this);
            final String placeID = place.getId();
            final LatLng latLng = place.getLatLng();

            if(isUserNew)
            {

                if(!mArrayList.contains(placeID)&&placeID!=null)
                { mArrayList.add(placeID);}
                else {
                    Toast.makeText(AddInterest_Activity.this, "Already Added", Toast.LENGTH_SHORT).show();
                    // usersRef.child(mAuth.getUid()).child("Interests").child("0").setValue("FASTNUCES");
                    //usersRef.child(mAuth.getUid()).child("Interests").child("1").setValue("IBA");
                }

            }else
            {

                    startActivity(new Intent(AddInterest_Activity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }

            alert = new AlertDialog.Builder(this);
            alert.setTitle("Confirmation");
            alert.setMessage("Are you want to add another Interest");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doMyWork();
                    dialog.dismiss();
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isUserNew=false;
                    dialog.dismiss();
                    for(int i = 0; i<mArrayList.size();i++)
                    { usersRef.child(mAuth.getUid()).child("Interests").child(Integer.toString(i)).setValue(mArrayList.get(i));}

                    startActivity(new Intent(AddInterest_Activity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            });



            } else if(resultCode == RESULT_CANCELED){

                finish();
            }
        }






        }

    }





