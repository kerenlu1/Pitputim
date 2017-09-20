package com.pitputim;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * The contacts activity. Displays the user's contacts.
 */

public abstract class MyContactsActivity extends AppCompatActivity {

    protected ListView contactsList;
    protected FloatingActionButton fab;
    protected FirebaseAuth mAuth;
    protected FirebaseUser user;
    protected DatabaseReference database, userRef;

    protected String newContactUid, userUid;
    protected boolean validContact;
    protected ArrayList<String> contactsUidList;
    protected String[] contactsUsernameList, contactsImgList, notificationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactsList = (ListView) findViewById(R.id.contacts_list);
        fab = (FloatingActionButton) findViewById(R.id.add_new_contact);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userUid = user.getUid();
        database = FirebaseDatabase.getInstance().getReference();
        userRef = database.child("uids").child(userUid);

        showContacts();

        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                LinearLayout layout = new LinearLayout(view.getContext());
                layout.setGravity(Gravity.RIGHT);
                layout.setOrientation(LinearLayout.VERTICAL);
                final TextView title = new TextView(view.getContext());
                title.setText("הוסף איש קשר חדש");
                title.setPadding(10, 10, 10, 10);
                title.setTextSize(24);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(getResources().getColor(R.color.colorAccent));
                layout.addView(title);
                final EditText contactEditText = new EditText(view.getContext());
                contactEditText.setHint("שם איש הקשר");
                contactEditText.setTextDirection(View.TEXT_DIRECTION_RTL);
                layout.addView(contactEditText);
                final AlertDialog dialog = new AlertDialog.Builder(view.getContext(), R.style.MyDialogTheme)
                        .setView(layout)
                        .setPositiveButton("הוסף", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String contactName = String.valueOf(contactEditText.getText());
                                contactEditText.setText("");
                                validateContact(contactName);
                            }
                        })
                        .setNegativeButton("ביטול", null)
                        .create();
                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                              @Override
                                              public void onShow(DialogInterface arg0) {
                                                  dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                                                  dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                                              }
                                          });
                dialog.show();
            }
        });
    }

    protected void validateContact(String name) {
        final DatabaseReference contactRef = database.child("users").child(name);

        if (name.equals(user.getDisplayName())) {
            validContact = false;
            Toast.makeText(MyContactsActivity.this, "לא ניתן להוסיף את עצמך כאיש קשר",
                    Toast.LENGTH_SHORT).show();
        } else {
            contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        validContact = true;
                        newContactUid = (String) dataSnapshot.getValue();
                    } else {
                        validContact = false;
                        Toast.makeText(MyContactsActivity.this, "לא קיים איש קשר בשם זה",
                                Toast.LENGTH_SHORT).show();
                    }
                    addNewContact();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    protected void addNewContact() {
        if (validContact) {
            userRef.child("contacts").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contactsUidList = new ArrayList<>();
                    if (dataSnapshot.exists()) {
                        contactsUidList = (ArrayList<String>) dataSnapshot.getValue();
                    }
                    if (contactsUidList.contains(newContactUid)) {
                        validContact = false;
                        Toast.makeText(MyContactsActivity.this, "לא ניתן להוסיף איש קשר זה",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        validContact = true;
                        contactsUidList.add(newContactUid);
                        userRef.child("contacts").setValue(contactsUidList);
                        // set notification off
                        database.child("notifications").child(newContactUid).child(userUid).setValue("off");
                        Toast.makeText(MyContactsActivity.this, "איש הקשר נוסף בהצלחה",
                                Toast.LENGTH_SHORT).show();
                        showContacts();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
        }
    }

    protected void showContacts() {
        userRef.child("contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contactsUidList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    contactsUidList = (ArrayList<String>) dataSnapshot.getValue();
                }
                getLists();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    protected void getLists() {
        DatabaseReference ref = database.child("uids");
        contactsUsernameList = new String[contactsUidList.size()];
        contactsImgList = new String[contactsUidList.size()];
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (contactsUidList.contains(child.getKey())) {
                        int i = contactsUidList.indexOf(child.getKey());
                        for (DataSnapshot subChild : child.getChildren()) {
                            String key = subChild.getKey();
                            if (key.equals("username")) {
                                contactsUsernameList[i] = subChild.getValue().toString();
                            } else if (key.equals("profile_picture")) {
                                contactsImgList[i] = subChild.getValue().toString();
                            }
                        }
                    }
                }
                getNotifications();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    protected abstract void getNotifications();
}
