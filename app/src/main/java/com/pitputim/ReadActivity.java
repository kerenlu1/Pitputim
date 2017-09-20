package com.pitputim;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * The read activity.
 * Displays the chat between the two users.
 */
public class ReadActivity extends AppCompatActivity {

    private RelativeLayout header, content, footer;

    private ArrayList<Message> messages;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        // retrieve the sections of the activity.
        header = (RelativeLayout) findViewById(R.id.read_header);
        content = (RelativeLayout) findViewById(R.id.read_content);
        footer = (RelativeLayout) findViewById(R.id.read_footer);

        Intent myIntent = getIntent(); // gets the previously created intent
        final String username = myIntent.getStringExtra("username");
        final String profilePic= myIntent.getStringExtra("profile_picture");
        final String uid = myIntent.getStringExtra("uid");

        // set the header content
        TextView txtTitle = (TextView) findViewById(R.id.read_txt);
        ImageView imageView = (ImageView) findViewById(R.id.read_img);

        txtTitle.setText(username);
        if (profilePic.equals("default")) {
            imageView.setImageResource(R.drawable.profile);
        } else {
            Bitmap resource = Utilities.decode(profilePic);
            imageView.setImageBitmap(resource);
        }

        String userUid = user.getUid();
        String key;
        if (uid.compareTo(userUid) < 0) {
            key = uid + userUid;
        } else {
            key = userUid + uid;
        }

        final ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        database.child("notifications").child(userUid).child(uid).setValue("off");
        DatabaseReference ref = database.child("chats").child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Message item = child.getValue(Message.class);
                    messages.add(item);
                }
                String[] data = new String[messages.size()];
                MessageAdapter adapter = new MessageAdapter(ReadActivity.this, messages, data);
                listOfMessages.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
