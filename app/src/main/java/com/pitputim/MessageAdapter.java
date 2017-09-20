package com.pitputim;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * An adapter for displaying messages in the chat.
 */

public class MessageAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<Message> messageList;

    public MessageAdapter(Activity context, ArrayList<Message> messageList, String[] data) {
        super(context, R.layout.single_message, data);
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.single_message, null, true);

        // Get references to the views of single_message.xml
        TextView messageText = (TextView) rowView.findViewById(R.id.message_text);
        ImageView messagePic = (ImageView) rowView.findViewById(R.id.message_pic);
        TextView messageUser = (TextView) rowView.findViewById(R.id.message_user);
        TextView messageTime = (TextView) rowView.findViewById(R.id.message_time);

        // Set their text
        final String type = messageList.get(position).getMessageType();
        final String content = messageList.get(position).getMessageContent();
        if (type.equals("text")) {
            messageText.setText(content);
        } else {
            int picId = context.getResources().getIdentifier(content, "mipmap", context.getPackageName());
            messagePic.setImageResource(picId);
            messagePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int audioId = context.getResources().getIdentifier(content, "raw", context.getPackageName());
                    MediaPlayer mPlayer = MediaPlayer.create(context, audioId);
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.start();
                }
            });
        }
        messageUser.setText(messageList.get(position).getMessageSender());

        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                messageList.get(position).getMessageTime()));

        return rowView;
    }
}

