package com.pitputim;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * An adapter for displaying images in the communication board gallery.
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<String> items;

    // Constructor
    public ImageAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            imageView.setLayoutParams(new GridView.LayoutParams(screenWidth/2, (int)(1.2*(screenWidth/2))));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(18,18,18,18);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        String itemName = items.get(position);
        int id = context.getResources().getIdentifier(itemName, "mipmap", context.getPackageName());
        imageView.setImageResource(id);
        if (GalleryActivity.selectedItem == getItemId(position)) {
            imageView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            imageView.setBackgroundColor(Color.TRANSPARENT);
        }
        return imageView;
    }
}
