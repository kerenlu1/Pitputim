package com.pitputim;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * An adapter for displaying contacts in the contacts list.
 */

public class CustomList extends ArrayAdapter<String> {

    public static final int MENU = 0;
    public static final int WRITE = 1;
    public static final int READ = 2;

    private final Activity context;
    private final ArrayList<String> uid;
    private final String[] text;
    private final String[] image;
    private final String[] notification;
    private final int mode;

    public CustomList(Activity context, ArrayList<String> uid, String[] text, String[] image, String[] notification, int mode) {
        super(context, R.layout.single_list_item, text);
        this.context = context;
        this.uid = uid;
        this.text = text;
        this.image = image;
        this.notification = notification;
        this.mode = mode;

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.single_list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        txtTitle.setText(text[position]);
        if (image[position].equals("default")) {
            imageView.setImageResource(R.drawable.profile);
        } else {
            Bitmap resource = Utilities.decode(image[position]);
            imageView.setImageBitmap(resource);
        }

        switch (mode) {
            case WRITE:
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, WriteActivity.class);
                        intent.putExtra("uid", uid.get(position));
                        intent.putExtra("username", text[position]);
                        intent.putExtra("profile_picture", image[position]);
                        context.startActivity(intent);
                    }
                });
                break;
            case READ:
                ImageView notificationView = (ImageView) rowView.findViewById(R.id.notification);
                if(notification[position].equals("on")) {
                    notificationView.setImageResource(R.drawable.env);
                }
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ReadActivity.class);
                        intent.putExtra("uid", uid.get(position));
                        intent.putExtra("username", text[position]);
                        intent.putExtra("profile_picture", image[position]);
                        context.startActivity(intent);
                    }
                });

                break;
            default:
        }
        return rowView;
    }
}