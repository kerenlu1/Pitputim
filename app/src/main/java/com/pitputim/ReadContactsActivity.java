package com.pitputim;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

/**
 * The read contacts activity, extends the contacts activity.
 * Displays the user's contacts and the unread messages notifications.
 */

public class ReadContactsActivity extends MyContactsActivity {

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fab = (FloatingActionButton) findViewById(R.id.add_new_contact);
        fab.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void getNotifications() {
        DatabaseReference ref = database.child("notifications").child(userUid);
        notificationsList = new String[contactsUidList.size()];
        Arrays.fill(notificationsList, "off");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (contactsUidList.contains(child.getKey())) {
                        int i = contactsUidList.indexOf(child.getKey());
                        notificationsList[i] = child.getValue().toString();
                    }
                }
                CustomList adapter = new CustomList(ReadContactsActivity.this, contactsUidList, contactsUsernameList, contactsImgList, notificationsList, 2);
                contactsList.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
