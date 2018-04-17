package com.example.bilal.hci_project;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GeneralNews_Activity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String[] news;
    private LatLng[] pointer;
    private Handler handler;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private DatabaseReference ReferenceToUserInterest;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private ArrayList<String> interestArray;
    String queriedLocation = "";
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_news_);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.GeneralNewMapView);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(GeneralNews_Activity.this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mContext=this;
        mAuth = FirebaseAuth.getInstance();
        ReferenceToUserInterest = ref.child("users").child(mAuth.getUid()).child("Interests");

        FetchDataAysncTask fetchDataAysncTask = new FetchDataAysncTask();
        fetchDataAysncTask.doInBackground(null);
    }

    class FetchDataAysncTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {

            ReferenceToUserInterest.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    interestArray = (ArrayList<String>) dataSnapshot.getValue();

                    if(interestArray!=null) {
                        furtherworking();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return interestArray;
        }

        public FetchDataAysncTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(mContext,"Context is Loading...",Toast.LENGTH_LONG).show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

    private void furtherworking() {
//        totalplace = interestArray.size();
//        String[] interests = new String[]{"ChIJW-N2Umwxsz4R_V8ZAAFqOoI","ChIJv3707Gwxsz4RS6rGKEAeHAo","ChIJVVVV5W0xsz4RaKsKSW__mks"};
        pointer = new LatLng[interestArray.size()];
        news = new String[interestArray.size()];
        news[0] = "HCI final demonstration of Project is on Thursday.";
        news[1] = "the price of Roll is 40% Off for today";
        news[2] = "Sale! 20% on all Hp laptops.";
        for (int i=3;i<interestArray.size();i++)
        {
            news[i] = "No news";
        }
        for (int i = 0; i < interestArray.size(); i++) {

            String interest = interestArray.get(i);
            findLocation(interest, i);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("billal -> ","Map is ready");
        mMap = googleMap;

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void show_content_map() {
        ResourceUtil resourceUtil = new ResourceUtil();
        for (int i = 0; i < interestArray.size(); i++) {
            mMap.addMarker(new MarkerOptions()
                    .position(pointer[i])
                    .title(news[i])
                    .icon(BitmapDescriptorFactory.fromBitmap(resourceUtil.getBitmap(GeneralNews_Activity.this, R.drawable.ic_message_black_24dp))));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pointer[0],15));
        }
    }

    private void findLocation(String placeId, final int i) {
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place myPlace = places.get(0);
                            queriedLocation = (String) myPlace.getName();
                            pointer[i] = new LatLng(myPlace.getLatLng().latitude,myPlace.getLatLng().longitude);

                            Log.v("billal from General", "" + queriedLocation);
//                            return true;
                        }
                        if(i+1==interestArray.size())
                        {
                            if(pointer[0]!=null)
                            { show_content_map();}
                        }
                        places.release();
                    }

                });
    }

    public static class ResourceUtil {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private Bitmap getBitmap(VectorDrawable vectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
            return bitmap;
        }

        private Bitmap getBitmap(VectorDrawableCompat vectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
            return bitmap;
        }

        public Bitmap getBitmap(Context context, @DrawableRes int drawableResId) {
            Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof VectorDrawableCompat) {
                return getBitmap((VectorDrawableCompat) drawable);
            } else if (drawable instanceof VectorDrawable) {
                return getBitmap((VectorDrawable) drawable);
            } else {
                throw new IllegalArgumentException("Unsupported drawable type");
            }
        }
    }

    

}
