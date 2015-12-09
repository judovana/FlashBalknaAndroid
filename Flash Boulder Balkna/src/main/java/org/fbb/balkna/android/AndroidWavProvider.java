package org.fbb.balkna.android;

import org.fbb.balkna.model.WavPlayer;
import org.fbb.balkna.model.WavPlayerProvider;

import java.net.URL;

/**
 * Created by jvanek on 11/29/15.
 */
public class AndroidWavProvider implements WavPlayerProvider {
    @Override
    public WavPlayer createPlayer(URL url) {
        return new AndroidWavPlayer(url);
    }
}
