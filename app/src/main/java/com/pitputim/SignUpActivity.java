package com.pitputim;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The sign-up activity.
 * Users are registered with username, email and password, they can also choose profile picture,
 * using the Firebase authentication.
 */

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "SignUp";
    private static final int REQUEST_CAMERA = 0;
    private static final int SELECT_FILE = 1;

    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private Button signupBtn;
    private String userChoosenTask, profilePicture;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth]
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Views
        usernameField = (EditText) findViewById(R.id.field_username);
        emailField = (EditText) findViewById(R.id.field_email);
        passwordField = (EditText) findViewById(R.id.field_password);

        // Buttons
        signupBtn = (Button) findViewById(R.id.sign_up_button);
        signupBtn.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null && user.getDisplayName() == null){
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName((usernameField.getText()).toString()).build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Display name: ", (usernameField.getText()).toString());
                            }
                        }
                    });
                }
            }
        };

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
    }



    private boolean validateForm() {
        boolean valid = true;

        String username = usernameField.getText().toString();
        if (TextUtils.isEmpty(username)) {
            usernameField.setError("שדה חובה");
            valid = false;
        } else {
            usernameField.setError(null);
        }

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("שדה חובה");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("שדה חובה");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    private void createAccount() {
        if (!validateForm()) {
            return;
        }
        selectProfilePicture();
    }

    private void completeCreateAccount(final String username, final String email, final String password) {
        Log.d(TAG, "createAccount:" + email);
        // [START create_user_with_email]
        progressDialog.setMessage("יצירת חשבון...");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        if (!task.isSuccessful()) {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "ההרשמה נכשלה אנא נסו שנית",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Sign in success, update UI with the signed-in user's information
                            Log.w(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignUpActivity.this, "ההרשמה הצליחה ברוך הבא לפטפוטים!",
                                    Toast.LENGTH_SHORT).show();
                            addNewUser(username, profilePicture);
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_up_button) {
            createAccount();
        }
    }

    private void addNewUser(String username, String profilePic) {
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase.child("users").child(username).setValue(user.getUid());
        mDatabase.child("uids").child(user.getUid()).child("username").setValue(username);
        mDatabase.child("uids").child(user.getUid()).child("profile_picture").setValue(profilePic);
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

    private void selectProfilePicture() {
        final CharSequence[] items = { "צלם תמונת פרופיל", "בחר תמונת פרופיל מגלריית התמונות",
                "איני מעוניין בתמונת פרופיל" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this, R.style.MyDialogTheme);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("צלם תמונת פרופיל")) {
                    userChoosenTask = "camera";
                    cameraIntent();
                } else if (items[item].equals("בחר תמונת פרופיל מגלריית התמונות")) {
                    userChoosenTask = "gallery";
                    galleryIntent();
                } else if (items[item].equals("איני מעוניין בתמונת פרופיל")) {
                    userChoosenTask = "default";
                    profilePicture = "default";
                    dialog.dismiss();
                    completeCreateAccount(usernameField.getText().toString(), emailField.getText().toString(), passwordField.getText().toString());
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bm = Utilities.scaleDownBitmap(bm, SignUpActivity.this);
        profilePicture = Utilities.encode(bm);
        completeCreateAccount(usernameField.getText().toString(), emailField.getText().toString(), passwordField.getText().toString());
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bm = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bm = Utilities.scaleDownBitmap(bm, SignUpActivity.this);
        profilePicture = Utilities.encode(bm);
        completeCreateAccount(usernameField.getText().toString(), emailField.getText().toString(), passwordField.getText().toString());
    }
}

