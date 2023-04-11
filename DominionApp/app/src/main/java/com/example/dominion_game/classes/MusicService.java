/**
 * MusicService is a class extends Service which plays and stops music.
 */
package com.example.dominion_game.classes;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.dominion_game.R;


public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    public MusicService() {

    }

    /**
     * A function that starts the service.
     * @param intent
     * @param flags
     * @param startId
     * @return START_STICKY
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //getting systems default ringtone
        mediaPlayer = MediaPlayer.create(this, R.raw.super_smash_bros_music);
        //setting loop play to true
        //this will make the ringtone continuously playing
        mediaPlayer.setLooping(true);

        //staring the player
        mediaPlayer.start();

        //we have some options for service
        //start sticky means service will be explicity started and stopped
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * A function that stops the player.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopping the player when service is destroyed
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}

