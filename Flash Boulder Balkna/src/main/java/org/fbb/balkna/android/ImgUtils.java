package org.fbb.balkna.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import org.fbb.balkna.Packages;
import org.fbb.balkna.model.ImagesSaver;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.Trainable;
import org.fbb.balkna.model.primitives.Exercise;
import org.fbb.balkna.model.primitives.Training;
import org.fbb.balkna.model.utils.IoUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jvanek on 12/5/15.
 */
public class ImgUtils implements ImagesSaver {
    public  void writeExercisesImagesToDir(File imgDir, List<String> names) throws IOException {
        List<Bitmap> allti = getImages(Packages.IMAGES_EXE, names);
        writeImagesToDir(imgDir, names, allti);
    }

    public  void writeTrainingsImagesToDir(File imgDir, List<String> names) throws IOException {
        List<Bitmap> allti = getImages(Packages.IMAGES_TRA, names);
        writeImagesToDir(imgDir, names, allti);
    }

    private static void writeImagesToDir(File imgDir, List<String> names, List<Bitmap> allti) throws IOException {
        for (int i = 0; i < allti.size(); i++) {
            Bitmap it = allti.get(i);
            SaveImage(it, new File(imgDir, names.get(i)));
        }
        Bitmap orig = getDefaultImage();
        SaveImage(orig, new File(imgDir, Model.getDefaultImageName()));
    }

    private static void SaveImage(Bitmap bmp, File filename) {

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    public static Bitmap getImage(String subPackage, String fileName) {
        try {
            return processCache(IoUtils.getFile(subPackage, fileName));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static List<Bitmap> getImages(String subPackage, List<String> l) {
        String[] ll = new String[l.size()];
        for (int i = 0; i < l.size(); i++) {
            String get = l.get(i);
            ll[i] = get;

        }
        return getImages(subPackage, ll);
    }

    public static List<Bitmap> getImages(String subPackage, String... names) {
        List<Bitmap> r = new ArrayList<Bitmap>(names.length + 1); //we can expect also the "title one" to eb added later
        for (String l1 : names) {
            try {
                Bitmap img = processCache(IoUtils.getFile(subPackage, l1));
                if (img != null) {
                    r.add(img);
                }
            } catch (Exception ex) {
                System.out.println("Eror loading: " + l1);
                ex.printStackTrace();
            }
        }
        return r;
    }

    public static Bitmap getDefaultImage() {
        return ImgUtils.getImage(Packages.IMAGES_APP, Model.getDefaultImage());
    }

    public static List<Bitmap> getTrainingImages(Trainable t, int targetW, int targetH) {
        List<Bitmap> r = getImages(Packages.IMAGES_TRA, t.getImages());
        List<Bitmap> r2 = getImages(Packages.IMAGES_EXE, t.getExerciseImages());
        if (r == null) {
            r = new ArrayList<Bitmap>();
        }
        r.add(0, getDefaultImage());
        if (r2.size() > 1) {
            // r.add(createAllImage(r2, targetW, targetH));
        }
        r.addAll(r2);

        return r;
    }

    public static List<Bitmap> getExerciseImages(Exercise e, int targetW, int targetH) {
        List<Bitmap> r = getImages(Packages.IMAGES_EXE, e.getImages());
        if (r == null) {
            r = new ArrayList<Bitmap>();
        }
        if (r.isEmpty()) {
            r.add(getDefaultImage());
        } else {
            if (r.size() > 1) {
                //  r.add(0, createAllImage(r, targetW, targetH));
            }
        }

        return r;
    }

    private static final Map<URL, Bitmap> cache = new HashMap<URL, Bitmap>();

    private static Bitmap processCache(URL file) throws IOException {
        if (cache.get(file) != null) {
            return cache.get(file);
        } else {
            URLConnection conn = file.openConnection();
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            Bitmap img = BitmapFactory.decodeStream(bufferedInputStream);
            cache.put(file, img);
            return img;
        }
    }

    public static Bitmap createAllImage(List<Bitmap> imgs, int targetW, int targetH) {
        if (imgs == null || imgs.isEmpty()) {
            return null;
        }
        if (imgs.size() == 1) {
            return imgs.get(0);
        }
        int W = 100;
        int H = 100;
        int width = (int) Math.round(Math.sqrt(imgs.size()));
        int height = width;
        if (imgs.size() == 2) {
            if (targetW > targetH) {
                width = 2;
                height = 1;
            } else {
                width = 1;
                height = 2;
            }
        }
        if (imgs.size() == 3) {
            width = 2;
            height = 2;
        }
        if (imgs.size() == 5 || imgs.size() == 6) {
            if (targetW > targetH) {
                width = 3;
                height = 2;
            } else {
                width = 2;
                height = 3;
            }
        }
        if (imgs.size() > 6 && imgs.size() < 10) {
            width = 3;
            height = 3;
        }
        if (imgs.size() >= 10 && imgs.size() < 16) {
            if (targetW > targetH) {
                width = 4;
                height = 3;
            } else {
                width = 3;
                height = 4;
            }
        }
        int iw = (width) * W;
        int ih = (height) * H;
        int x = 0;
        int y = 0;
        Bitmap r = Bitmap.createBitmap(iw, ih, imgs.get(0).getConfig());
        for (int i = 0; i < imgs.size(); i++) {
            Bitmap img = imgs.get(i);

            Canvas c = new Canvas(img);
            Rect src = new Rect(0, 0, img.getWidth(), img.getHeight());
            Rect dest = new Rect(x, y, W, H);
            Paint p = new Paint();
            c.drawBitmap(squeeze(img, W, H), src, dest, p);

            //r.getcreateGraphics().drawImage(squeeze(img, W, H), x, y, W, H, null);
            if ((i + 1) % (width) == 0) {
                x = 0;
                y = y + W;

            } else {
                x = x + H;
            }
        }
        return r;

    }


    public static Bitmap squeeze(Bitmap img, int W, int H) {
        if (Model.getModel().isRatioForced()) {
            Bitmap buf = Bitmap.createBitmap(W, H, img.getConfig());
            int w = W;
            int h = H;
            double wRatio = (double) W / (double) img.getWidth();
            double hRatio = (double) H / (double) img.getHeight();
            if (wRatio < hRatio) {
                w = (int) (wRatio * (double) img.getWidth());
                h = (int) (wRatio * (double) img.getHeight());
            } else {
                w = (int) (hRatio * (double) img.getWidth());
                h = (int) (hRatio * (double) img.getHeight());
            }
            Canvas c = new Canvas(buf);
            Rect src = new Rect(0, 0, img.getWidth(), img.getHeight());
            Rect dest = new Rect(0, 0, w, h);
            Paint p = new Paint();
            c.drawBitmap(buf, src, dest, p);
            //buf.createGraphics().drawImage(img, Math.abs((W - w) / 2), Math.abs((H - h) / 2), w, h, null);
            return buf;
        } else {
            return img;
        }
    }

    public static int javaColorToAndroidColor(int i){
        return Color.parseColor(javaColorToAndroidHexColor(i));
    }
    public static String javaColorToAndroidHexColor(int i){
        return String.format("#%06X", (0xFFFFFF & i));
    }

}
