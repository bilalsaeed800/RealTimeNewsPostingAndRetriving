package com.example.bilal.hci_project;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateNewUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String email,password;
    private Button mButtonSignUp;
    private EditText mEditTextEmail,mEditTextPassword;
    private ProgressBar mProgressBar;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);

        mContext = this;
        mAuth = FirebaseAuth.getInstance();

        mEditTextEmail = findViewById(R.id.create_email);
        mEditTextPassword = findViewById(R.id.create_password);
        mButtonSignUp = findViewById(R.id.button_signup);
        mProgressBar = findViewById(R.id.progressBar_createNewUserActivity);







        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


            mEditTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    final String email1 = mEditTextEmail.getText().toString().trim();
                    if (email1.matches(emailPattern))
                    {
                        mEditTextEmail.setError(null);
                        //        mEditTextEmail.setError("Invalid");

                    }
                    else
                    {
                        if(!email1.isEmpty())
                        mEditTextEmail.setError("Invalid");
                    }

                }
            });







        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = mEditTextEmail.getText().toString();
                password = mEditTextPassword.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(mContext,"Enter Email address",Toast.LENGTH_SHORT).show();
                    mEditTextEmail.setError("Provide Email Address Here");
                    return;
                }

                if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(mContext,"Enter Password",Toast.LENGTH_SHORT).show();
                    mEditTextPassword.setError("Provide Password Here");
                    return;
                }

                mProgressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(CreateNewUser.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(mContext, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(mContext, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext,"Successfu" +
                                            "lly Registered", Toast.LENGTH_LONG).show();
                                   // startActivity(new Intent(mContext, MainActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });

    }





}
