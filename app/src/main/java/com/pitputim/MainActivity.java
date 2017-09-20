package com.pitputim;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The main activity of the application.
 * Display the sign-up or sign-in options for a unsigned user,
 * or read/write message options and sign-out option for signed user.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    private ImageButton writeBtn, readBtn, signinBtn, signupBtn, signoutBtn;
    private TextView welcomeText;
    //Authentication parameters.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = (TextView) findViewById(R.id.welcome);
        welcomeText.setVisibility(View.INVISIBLE);
        writeBtn = (ImageButton) findViewById(R.id.write_btn);
        writeBtn.setVisibility(View.INVISIBLE);
        readBtn = (ImageButton) findViewById(R.id.read_btn);
        readBtn.setVisibility(View.INVISIBLE);
        signinBtn = (ImageButton) findViewById(R.id.signin_btn);
        signinBtn.setVisibility(View.INVISIBLE);
        signupBtn = (ImageButton) findViewById(R.id.signup_btn);
        signupBtn.setVisibility(View.INVISIBLE);
        signoutBtn = (ImageButton) findViewById(R.id.signout_btn);
        signoutBtn.setVisibility(View.INVISIBLE);

        //Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    welcomeText.setVisibility(View.VISIBLE);
                    String username = user.getDisplayName();
                    if (username != null) {
                        welcomeText.setText(user.getDisplayName() + "\n" + "ברוך הבא!");
                    } else {
                        welcomeText.setText("ברוך הבא!");
                    }
                    writeBtn.setVisibility(View.VISIBLE);
                    readBtn.setVisibility(View.VISIBLE);
                    signoutBtn.setVisibility(View.VISIBLE);

                    signinBtn.setVisibility(View.INVISIBLE);
                    signupBtn.setVisibility(View.INVISIBLE);

                    writeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, WriteContactsActivity.class);
                            startActivity(intent);
                        }
                    });
                    readBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, ReadContactsActivity.class);
                            startActivity(intent);
                        }
                    });
                    signoutBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAuth.signOut();
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    // User is signed out - start sign in
                    signinBtn.setVisibility(View.VISIBLE);
                    signupBtn.setVisibility(View.VISIBLE);

                    welcomeText.setVisibility(View.INVISIBLE);
                    writeBtn.setVisibility(View.INVISIBLE);
                    readBtn.setVisibility(View.INVISIBLE);
                    signoutBtn.setVisibility(View.INVISIBLE);


                    signinBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                            startActivity(intent);
                        }
                    });
                    signupBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                            startActivity(intent);
                        }
                    });

                }

            }
        };


    }

    //Attach the listener to the FirebaseAuth instance.
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume(){
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
