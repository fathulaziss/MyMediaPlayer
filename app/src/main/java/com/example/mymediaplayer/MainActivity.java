package com.example.mymediaplayer;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mMediaPlayer;
    private boolean isReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPlay = findViewById(R.id.btn_play);
        Button btnStop = findViewById(R.id.btn_stop);

        btnPlay.setOnClickListener(v -> {
            if (!isReady) {
                // Prepare the MediaPlayer asynchronously
                mMediaPlayer.prepareAsync();
            } else {
                if (mMediaPlayer.isPlaying()) {
                    // Pause if it's playing
                    mMediaPlayer.pause();
                } else {
                    // Start if it's paused
                    mMediaPlayer.start();
                }
            }
        });

        btnStop.setOnClickListener(v -> {
            if (mMediaPlayer.isPlaying() || isReady) {
                // Stop the MediaPlayer and reset the isReady flag
                mMediaPlayer.stop();
                isReady = false;
            }
        });

        init();
    }

    private void init() {
        mMediaPlayer = new MediaPlayer();

        // Set up AudioAttributes for proper usage
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        mMediaPlayer.setAudioAttributes(attributes);

        // Load the raw resource
        try {
            // Open the raw resource as a file descriptor
            android.content.res.AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.guitar_background);
            if (afd != null) {
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close(); // Always close the AssetFileDescriptor after use
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading audio", Toast.LENGTH_SHORT).show();
        }

        // Set the listener for when the MediaPlayer is prepared
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isReady = true;
                mMediaPlayer.start(); // Start playing when ready
            }
        });

        // Set error listener (return false if there's an error)
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("MediaPlayer", "Error occurred: " + what + ", " + extra);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release(); // Release resources when the activity is destroyed
            mMediaPlayer = null;
        }
    }
}