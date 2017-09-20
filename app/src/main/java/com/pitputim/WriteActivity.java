package com.pitputim;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * The write activity.
 * Displays different writing options: text message and picture message.
 */
public class WriteActivity extends AppCompatActivity implements View.OnClickListener{

    private FloatingActionButton fab;
    private TextInputLayout textInput;
    private LinearLayout pictureMenu;
    private RelativeLayout header, content, footer;
    private ImageButton textBtn, picBtn, sendBtn, deleteBtn;
    //picture menu buttons
    private ImageButton emotionsBtn, foodBtn, clothingBtn, generalBtn, verbsBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference database;
    private String type, result;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        // retrieve the sections of the activity.
        header = (RelativeLayout) findViewById(R.id.write_header);
        content = (RelativeLayout) findViewById(R.id.write_content);
        footer = (RelativeLayout) findViewById(R.id.write_footer);

        //retrieve picture menu buttons
        emotionsBtn = (ImageButton) findViewById(R.id.menu_emotions);
        foodBtn = (ImageButton) findViewById(R.id.menu_food);
        clothingBtn = (ImageButton) findViewById(R.id.menu_clothing);
        generalBtn = (ImageButton) findViewById(R.id.menu_general);
        verbsBtn = (ImageButton) findViewById(R.id.menu_verbs);

        emotionsBtn.setOnClickListener(this);
        foodBtn.setOnClickListener(this);
        clothingBtn.setOnClickListener(this);
        generalBtn.setOnClickListener(this);
        verbsBtn.setOnClickListener(this);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth/5, screenWidth/5);
        emotionsBtn.setLayoutParams(params);
        foodBtn.setLayoutParams(params);
        clothingBtn.setLayoutParams(params);
        generalBtn.setLayoutParams(params);
        verbsBtn.setLayoutParams(params);

        textBtn = (ImageButton) findViewById(R.id.text_btn);
        picBtn = (ImageButton) findViewById(R.id.pic_btn);
        sendBtn = (ImageButton) findViewById(R.id.send_btn);
        deleteBtn = (ImageButton) findViewById(R.id.delete_btn);

        sendBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setVisibility(View.INVISIBLE);

        Intent myIntent = getIntent(); // gets the previously created intent
        final String username = myIntent.getStringExtra("username");
        final String profilePic= myIntent.getStringExtra("profile_picture");
        final String uid = myIntent.getStringExtra("uid");

        // set the header content
        TextView txtTitle = (TextView) findViewById(R.id.write_txt);
        ImageView imageView = (ImageView) findViewById(R.id.write_img);

        txtTitle.setText(username);
        if (profilePic.equals("default")) {
            imageView.setImageResource(R.drawable.profile);
        } else {
            Bitmap resource = Utilities.decode(profilePic);
            imageView.setImageBitmap(resource);
        }

        // text message - hide edit text
        fab = (FloatingActionButton) findViewById(R.id.fab);
        textInput = (TextInputLayout) findViewById(R.id.txt_input);
        pictureMenu = (LinearLayout) findViewById(R.id.pic_menu);

        fab.setVisibility(View.GONE);
        textInput.setVisibility(View.GONE);
        pictureMenu.setVisibility(View.GONE);


        textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "text";
                footer.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                textInput.setVisibility(View.VISIBLE);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                EditText messageField = (EditText) findViewById(R.id.input);
                String message = messageField.getText().toString();
                if (message.equals("")) {
                    Toast.makeText(WriteActivity.this, "לא ניתן לשלוח הודעה ריקה",
                            Toast.LENGTH_SHORT).show();
                } else {
                    TextView messageView = (TextView) findViewById(R.id.text_message);
                    messageView.setText(message);
                    messageField.setText("");

                    View v = WriteActivity.this.getCurrentFocus();
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(WriteActivity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    textBtn.setVisibility(View.INVISIBLE);
                    picBtn.setVisibility(View.INVISIBLE);
                    sendBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);

                    fab.setVisibility(View.GONE);
                    textInput.setVisibility(View.GONE);
                    footer.setVisibility(View.VISIBLE);
                }
            }
        });

        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "picture";
                footer.setVisibility(View.GONE);
                pictureMenu.setVisibility(View.VISIBLE);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textMessageView = (TextView) findViewById(R.id.text_message);
                textMessageView.setText("");
                ImageView picMessageView = (ImageView)  findViewById(R.id.pic_message);
                picMessageView.setImageResource(0);

                sendBtn.setVisibility(View.INVISIBLE);
                deleteBtn.setVisibility(View.INVISIBLE);
                textBtn.setVisibility(View.VISIBLE);
                picBtn.setVisibility(View.VISIBLE);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create the Message object
                Message newMessage;
                if (type.equals("text")) {
                    TextView messageView = (TextView) findViewById(R.id.text_message);
                    newMessage = new Message(user.getDisplayName(), type, messageView.getText().toString());
                } else {
                    newMessage = new Message(user.getDisplayName(), type, result);
                }

                String userUid = user.getUid();
                String key;
                if (uid.compareTo(userUid) < 0) {
                    key = uid + userUid;
                } else {
                    key = userUid + uid;
                }
                database.child("chats").child(key).push().setValue(newMessage);
                database.child("notifications").child(uid).child(userUid).setValue("on");
                Toast.makeText(WriteActivity.this, "ההודעה נשלחה בהצלחה",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WriteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                result = data.getStringExtra("result");
                ImageView messageView = (ImageView) findViewById(R.id.pic_message);
                int id = getResources().getIdentifier(result, "mipmap", getPackageName());
                messageView.setImageResource(id);
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int screenWidth = metrics.widthPixels;
                messageView.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth, (int)(1.2*screenWidth)));

                textBtn.setVisibility(View.INVISIBLE);
                picBtn.setVisibility(View.INVISIBLE);
                sendBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);

                pictureMenu.setVisibility(View.GONE);
                footer.setVisibility(View.VISIBLE);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //do nothing.
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(WriteActivity.this, GalleryActivity.class);
        int i = view.getId();
        if (i == R.id.menu_emotions){
            intent.putExtra("category", "emotions");
        } else if (i == R.id.menu_food){
            intent.putExtra("category", "food");
        } else if (i == R.id.menu_clothing){
            intent.putExtra("category", "clothing");
        } else if (i == R.id.menu_general){
            intent.putExtra("category", "general");
        } else if (i == R.id.menu_verbs){
            intent.putExtra("category", "verbs");
        }
        startActivityForResult(intent, 1);
    }
}
