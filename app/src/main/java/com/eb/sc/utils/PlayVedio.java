package com.eb.sc.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.eb.sc.R;

/**
 * Created by Administrator on 2017/9/22.
 */

public class PlayVedio {
    private static PlayVedio instance;
    private MediaPlayer player;
    private Context mcontext;

    private PlayVedio() {
    }

    public static PlayVedio getInstance() {
        if (instance == null) {
            instance = new PlayVedio();
        }
        return instance;
    }


    public void play(Context mcontext, int index) {
        if(index==1){
            player = MediaPlayer.create(mcontext, R.raw.wuxiaopiao);
        }else if(index==2){
            player = MediaPlayer.create(mcontext, R.raw.ertongpiao);
        }
        else if(index==3){
            player = MediaPlayer.create(mcontext, R.raw.laonianpiao);
        }
        else if(index==4){
            player = MediaPlayer.create(mcontext, R.raw.tongguo);
        }
        else if(index==5){
            player = MediaPlayer.create(mcontext, R.raw.chengrenpiao);
        }
        else if(index==6){
            player = MediaPlayer.create(mcontext, R.raw.yishiyong);
        }else if(index==7){
            player = MediaPlayer.create(mcontext, R.raw.youhuipiao);
        }else  if(index==8){
            player = MediaPlayer.create(mcontext, R.raw.tuanduipiao);
        }

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                }
            });

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    destory();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void destory() {
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
            player = null;
        }
    }


}
