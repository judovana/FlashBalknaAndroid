package org.fbb.balkna.android;


import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;

import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.SoundProvider;
import org.fbb.balkna.model.WavPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by jvanek on 11/29/15.
 */
public class AndroidWavPlayer implements WavPlayer {

    private final URL u;
    private SoundPool soundPool;

    public AndroidWavPlayer(URL u) {
        this.u = u;

    }

    int soundId;
    SoundPool sp;
    boolean loaded;

    @Override
    public void playAsync() {
        try {
            //mother fucking cheat to play sound on mother fucking anndroid not allowing froms-stream audio
            File f = File.createTempFile("fbb", "veryTest");
            File dir = f.getParentFile();
            f.delete();
            File ff = new File(u.getFile());
            f = new File(dir, SoundProvider.getInstance().getUsedSoundPack()+"-"+ff.getName());
            if (!f.exists()) {
                Model.saveUrl(u, f);
            }

            sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

            sp.setOnLoadCompleteListener(
                    new SoundPool.OnLoadCompleteListener() {
                        @Override
                        public void onLoadComplete(SoundPool soundPool, final int sampleId, int status) {
                            loaded = true;
                            sp.play(sampleId, 1, 1, 100, 0, 1);
                            new Thread(){
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2000); //all sounds eless then 1.5s
                                    }catch(Exception ex){
                                        ex.printStackTrace();
                                    }
                                    //reelase after palying finished
                                    sp.unload(sampleId);
                                    sp.release();
                                }
                            }.start();;

                        }
                    });
            //hmm not working :(
//        if(!loaded) {
//            soundId = sp.load(f.getAbsolutePath(), 100);
//        }else{
//            sp.play(soundId, 1, 1, 100, 0, 1);
//        }
           soundId = sp.load(f.getAbsolutePath(), 100);


        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void playSound() {

    }
}
