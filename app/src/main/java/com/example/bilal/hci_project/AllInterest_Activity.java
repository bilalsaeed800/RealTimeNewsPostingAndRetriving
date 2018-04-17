package com.example.bilal.hci_project;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllInterest_Activity extends AppCompatActivity {

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private DatabaseReference ReferenceToUserInterest;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private ArrayList<String> interestArray;
    private String[] queriedLocation;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_interest_);
        mContext=this;

        mGoogleApiClient = new GoogleApiClient
                .Builder(mContext)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        mAuth = FirebaseAuth.getInstance();
        ReferenceToUserInterest = ref.child("users").child(mAuth.getUid()).child("Interests");

        FetchDataAysncTask fetchDataAysncTask = new FetchDataAysncTask();
        fetchDataAysncTask.doInBackground(null);
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

    private void NextTast()
    {
        ListAdapter1 listAdapter = new ListAdapter1(this, queriedLocation);
        ListView listView = (ListView) findViewById(R.id.AllInterestListView);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new DataClickListener(queriedLocation));
    }
    private class DataClickListener implements ListView.OnItemClickListener {
        String[] dataList;

        DataClickListener(String[] mDoc) {
            dataList = mDoc;
        }
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d("billal -> ",dataList[i].toString());
            String interest = interestArray.get(i);
            DatabaseReference demo =  ReferenceToUserInterest.child(Integer.toString(i)).getRef();
            demo.setValue(null);//removeValue();
            startActivity(new Intent(AllInterest_Activity.this,AllInterest_Activity.class));
            finish();
        }
    }
    private class ListAdapter1 extends ArrayAdapter<String> {

        ListAdapter1(Context context, String[] values) {
            super(context, R.layout.activity_all_interest_, values);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;

            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.all_interest_listview_content, parent, false);
            }
            String data = getItem(position);

            TextView textViewTitle = (TextView) listItemView.findViewById(R.id.txtallinterest);
            textViewTitle.setText(data);
            return listItemView;
        }
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
            Toast.makeText(mContext,"Interest Is Retreiving...",Toast.LENGTH_LONG).show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }
    private void furtherworking() {
        queriedLocation = new String[interestArray.size()];
        for (int i = 0; i < interestArray.size(); i++) {
            String interest = interestArray.get(i);
            findLocation(interest, i);
        }
    }
    private void findLocation(String placeId, final int i) {
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place myPlace = places.get(0);
                            queriedLocation[i] = (String) myPlace.getName();
//                            Log.v("billal from ALLInterest", "" + queriedLocation[i]);
//                            return true;
                        }
                        if(i+1==interestArray.size())
                        {
                            if(queriedLocation[0]!=null)
                            {
                                NextTast();
                            }
                        }
                        places.release();
                    }

                });
    }
}
