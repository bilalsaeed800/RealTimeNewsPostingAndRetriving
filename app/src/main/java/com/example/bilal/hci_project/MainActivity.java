package com.example.bilal.hci_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private FirebaseAuth mAuth;
    private DatabaseReference ReferenceToUserInterest, referenceToMessages;
    private ArrayList<String> interestUiDArray;
    private GoogleApiClient mGoogleApiClient;
    private int totalplace;
    TabLayout tabLayout;
    private FloatingActionButton floatingActionButton;

    public ArrayList<InterestArea> interestAreas;
    private Context context;
    final Message[] message = new Message[5];

    boolean pausescreen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        mAuth = FirebaseAuth.getInstance();
        ReferenceToUserInterest = ref.child("users").child(mAuth.getUid()).child("Interests");
        interestAreas = new ArrayList<InterestArea>();
        AsyncTask1 asyncTask1 = new AsyncTask1();
        asyncTask1.doInBackground(null);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ComposeMessage.class);
                startActivity(intent);
            }
        });
    }

    class AsyncTask1 extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            ReferenceToUserInterest.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    interestUiDArray = (ArrayList<String>) dataSnapshot.getValue();

                    if (interestUiDArray.get(0) != null) {

                        furtherworking();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }
    }

    private void furtherworking() {
        totalplace = interestUiDArray.size();
        for (int i = 0; i < interestUiDArray.size(); i++) {
            String interest = interestUiDArray.get(i);
            interestAreas.add(new InterestArea(interest));
            findLocation(interest, i);
        }
    }

    private boolean findLocation(String placeId, final int i) {
        if (!placeId.equals("null")) {
            Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {

                            if (places.getStatus().isSuccess()) {
                                final Place myPlace = places.get(0);
                                interestAreas.get(i).setName((String) myPlace.getName());
//                             tabLayout.addTab(tabLayout.newTab().setText(queriedLocation));
                                Log.v("Longitude is", "" + interestAreas.get(i).getName());
                                Log.v("LatLongitude is", "" + myPlace.getLatLng());
//                            return true;
                            }
                            if (i + 1 == interestUiDArray.size()) {
                                fetchMessagesFromDatabase();
                            }
                            places.release();
                        }
                    });
        }
        return false;
    }

    private void fetchMessagesFromDatabase() {
        Toast.makeText(context, "fetching messages", Toast.LENGTH_LONG).show();
        referenceToMessages = ref.child("Messages");

        for (int i = 0; i < interestUiDArray.size(); i++) {
            final int finalI = i;
            referenceToMessages.child(interestUiDArray.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot interest : dataSnapshot.getChildren()) {
                        message[0] = new Message(interest.child("category").getValue().toString(), interest.child("expiry date").getValue().toString(),
                                interest.child("message").getValue().toString(), interest.child("from").getValue().toString());
                        if (message[0] != null && interestAreas.get(finalI) != null) {
                            interestAreas.get(finalI).addMessage(message[0]);
                        }

                    }
                    if (finalI + 1 == interestUiDArray.size()) {
                        showScreenContent();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    private void showScreenContent() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    protected void onPause() {
        pausescreen = true;
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (pausescreen == true) {
            showScreenContent();
        }
        pausescreen = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";


        public PlaceholderFragment() {
        }


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, InterestArea interestArea) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putSerializable("interest area", interestArea);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            // TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            InterestArea interestArea = (InterestArea) getArguments().getSerializable("interest area");
            ListView listView = rootView.findViewById(R.id.list_view);

            MessagesAdapter messagesAdapter;
            if (interestArea != null) {
                ArrayList<Message> arrayList = interestArea.getMessagesArrayList();
                if (arrayList == null) {
                    arrayList = new ArrayList<Message>();
                    arrayList.add(new Message("", "", "No Messages", ""));
                }
                messagesAdapter = new MessagesAdapter(rootView.getContext(), arrayList);
                listView.setAdapter(messagesAdapter);
                //if(interestArea.getMessage(0)!=null)
                //textView.setText(interestArea.getMessage(0).getMessageText());
                //else textView.setText("No Messages");
            }
//
// textView.setText("abc");
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //  if(interestAreas.get(position)!=null)
            // Toast.makeText(context,interestAreas.get(0).getMessage(0).getMessageText(),Toast.LENGTH_LONG).show();

            return PlaceholderFragment.newInstance(position + 1, interestAreas.get(position));
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return interestAreas.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return interestAreas.get(position).getName().toString();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.AddInterestDrawer) {
            startActivity(new Intent(MainActivity.this, AddInterest2.class));
        } else if (id == R.id.GeneralNewsDrawer) {
            mGoogleApiClient.disconnect();
            startActivity(new Intent(MainActivity.this, GeneralNews_Activity.class));

        } else if (id == R.id.SettingDrawer) {

        } else if (id == R.id.LogoutDrawer) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, AddInterest_Activity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else if (id == R.id.DeleteInterest) {
            startActivity(new Intent(MainActivity.this, AllInterest_Activity.class));
        } else if (id == R.id.PostNews) {
            startActivity(new Intent(MainActivity.this, ComposeMessage.class));
        } else if (id == R.id.DeleteNews) {
            startActivity(new Intent(MainActivity.this,DeleteNews.class));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
