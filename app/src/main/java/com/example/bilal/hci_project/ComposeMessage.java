package com.example.bilal.hci_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class ComposeMessage extends AppCompatActivity {

    private Spinner categorySpinner;
    private EditText messageEditText;
    private Button selectExpiryDateButton,datePickerDoneButton;
    private ImageButton sendButton;
    private DatePicker datePicker;
    private TextView showDateTextView;
    private LinearLayout composeMessageContentLinearLayout;
    private LinearLayout datePickerContentLinearLayout;

    private String placeID;
    private String expiryDate;
    private String category;
    private String message;
    private Context context;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);

        categorySpinner = findViewById(R.id.select_category_spinner);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        datePicker = findViewById(R.id.datePicker);
        selectExpiryDateButton = findViewById(R.id.select_expirydate_button);
        showDateTextView = findViewById(R.id.show_date_textview);
        datePickerDoneButton = findViewById(R.id.datePicker_done_button);
        composeMessageContentLinearLayout = findViewById(R.id.compose_message_content_linear_layout);
        datePickerContentLinearLayout = findViewById(R.id.datePicker_linear_layout);
        showDateTextView = findViewById(R.id.show_date_textview);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
        datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis()+30L*86400000L);


        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setHintTextColor(Color.WHITE);
        ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextColor(Color.WHITE);


        category = (String) categorySpinner.getItemAtPosition(0);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setError(null);
                placeID = place.getId();
                Log.v( "Place: " , (String) place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.v( "An error occurred: " , String.valueOf(status));
            }
        });



        selectExpiryDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                composeMessageContentLinearLayout.setVisibility(View.INVISIBLE);
                datePickerContentLinearLayout.setVisibility(View.VISIBLE);
            }
        });


        datePickerDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerContentLinearLayout.setVisibility(View.INVISIBLE);
                composeMessageContentLinearLayout.setVisibility(View.VISIBLE);


                expiryDate = Integer.toString(datePicker.getDayOfMonth())+"-"+Integer.toString(datePicker.getMonth()+1)+"-"+Integer.toString(datePicker.getYear());

                showDateTextView.setText(expiryDate);
                Log.v( "Expiry Date: " , expiryDate);
            }
        });


        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                category = (String) categorySpinner.getItemAtPosition(i);

                Log.v("Category Selected",category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                messageEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(messageEditText.getText().toString().length()==0)
                {
                    messageEditText.setError("Message Field is Empty");
                }
            }
        });

        context = this;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).getText().toString().length()!=0)
                {
                    if(placeID!=null)
                    {
                        if(expiryDate!=null)
                        {
                            message = messageEditText.getText().toString();
                            if(message.length()!=0)
                            {
                                Toast.makeText(context,"Sending message",Toast.LENGTH_LONG).show();
                                DatabaseReference databaseReference = firebaseDatabase.getReference().child("Messages").child(placeID).push();

                                databaseReference.child("from").setValue(mAuth.getUid());

                                databaseReference.child("message").setValue(message);

                                databaseReference.child("category").setValue(category);

                                databaseReference.child("expiry date").setValue(expiryDate);

                                Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();

                                startActivity(new Intent(ComposeMessage.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                            }
                            else{messageEditText.setError("Message Field is Empty");}
                        }
                    }
                }
                else {((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setError("Place Not Selected");}


            }
        });





    }

    @Override
    public void onBackPressed() {

        if(composeMessageContentLinearLayout.getVisibility()==View.INVISIBLE)
        {
            datePickerContentLinearLayout.setVisibility(View.INVISIBLE);
            composeMessageContentLinearLayout.setVisibility(View.VISIBLE);
        }else
        {super.onBackPressed();}
    }
}
