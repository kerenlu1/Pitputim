package com.pitputim;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.List;

/**
 * The gallery activity.
 * Display the communication borad gallery and the select and audio options.
 */

public class GalleryActivity extends AppCompatActivity {

    private List<String> items;
    private String category;

    private ImageButton selectBtn, audioBtn;
    private GridView grid;
    static long selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        selectedItem = -1;

        Intent myIntent = getIntent(); // gets the previously created intent
        category = myIntent.getStringExtra("category");
        items = PictureMap.map.get(category);

        final ImageAdapter adapter = new ImageAdapter(GalleryActivity.this, items);
        grid = (GridView) findViewById(R.id.gallery_grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedItem == id) {
                    selectedItem = -1;
                } else {
                    selectedItem = id;
                }
                adapter.notifyDataSetChanged();
            }
        });

        audioBtn = (ImageButton) findViewById(R.id.audio_btn);
        selectBtn = (ImageButton) findViewById(R.id.select_btn);

        audioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItem != -1) {
                    int id = getResources().getIdentifier(items.get((int)selectedItem), "raw", getPackageName());
                    MediaPlayer mPlayer = MediaPlayer.create(GalleryActivity.this, id);
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.start();
                }
            }
        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItem != -1) {
                    String itemName = items.get((int)selectedItem);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",itemName);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }
        });
    }
}
