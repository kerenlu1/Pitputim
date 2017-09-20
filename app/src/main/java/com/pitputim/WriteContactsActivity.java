package com.pitputim;

import android.os.Bundle;
import java.util.Arrays;

/**
 * The write contacts activity, extends the contacts activity.
 * Displays the user's contacts and the option to add new contact.
 */

public class WriteContactsActivity extends MyContactsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getNotifications() {
        notificationsList = new String[contactsUidList.size()];
        Arrays.fill(notificationsList, "off");
        CustomList adapter = new CustomList(WriteContactsActivity.this, contactsUidList, contactsUsernameList, contactsImgList, notificationsList, 1);
        contactsList.setAdapter(adapter);

    }
}
