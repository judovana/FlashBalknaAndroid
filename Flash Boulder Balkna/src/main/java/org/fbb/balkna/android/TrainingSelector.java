package org.fbb.balkna.android;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.merged.uncompressed.MainTimer;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BasicTime;
import org.fbb.balkna.model.primitives.Training;
import org.fbb.balkna.swing.locales.SwingTranslator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TrainingSelector extends AppCompatActivity {


    private class OnTouchListenerImpl implements View.OnTouchListener {
        private final ImageView img;

        public OnTouchListenerImpl(ImageView src) {
            this.img = src;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return onTouchImpl(v.getWidth(), event.getX(), event.getAction());
        }

        public boolean onTouchImpl(int w, float x, int act) {
            if (act == MotionEvent.ACTION_DOWN) {
                if (x > w / 3) {
                    showedImagesPoint++;
                } else {
                    showedImagesPoint--;
                }
                if (showedImagesPoint >= showedImages.size()) {
                    showedImagesPoint = showedImages.size() - 1;
                }
                if (showedImagesPoint < 0) {
                    showedImagesPoint = 0;
                }
                img.setImageBitmap(showedImages.get(showedImagesPoint));
            }
            return false;
        }
    }

    private List<Bitmap> showedImages;
    private int showedImagesPoint = 0;
    private int lastValidPosition = -1;

    public static MainTimer run;
    public static Training src;
    public static String lastHtmlFile;

    private OnTouchListenerImpl touch;
    private Button startTraining;
    static TrainingSelector hack;
    ListView listview;


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TrainingSelector.hack = this;
//http://developer.android.com/reference/android/os/StrictMode.html
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .penaltyLog()
                .build());

        setContentView(R.layout.activity_training_selector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listview = (ListView) findViewById(R.id.listView);
        startTraining = (Button) findViewById(R.id.startTrainingButton);
        final EditText memo = (EditText) findViewById(R.id.editText);
        final ImageView img = (ImageView) findViewById(R.id.imageView);
        memo.setKeyListener(null);
        startTraining.setEnabled(false);
        touch = new OnTouchListenerImpl(img);
        img.setOnTouchListener(touch);
        File dataDir = new File(this.getBaseContext().getApplicationInfo().dataDir);
        Model.createrModel(dataDir, new AndroidWavProvider());
        this.setTitle(Model.getModel().getTitle());
        memo.setText(Model.getModel().getDefaultStory() + getAndroidGuiStory());
        showedImages = new ArrayList<>();
        showedImages.add(ImgUtils.getDefaultImage());
        showedImagesPoint = 0;
        touch.onTouchImpl(100, 10, MotionEvent.ACTION_DOWN);

        reloadTrainings();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                lastValidPosition = position;
                ((ArrayAdapter) (listview.getAdapter())).notifyDataSetChanged();
                final Training item = (Training) parent.getItemAtPosition(position);
                //final Training item = (Training) parent.getSelectedItem();
                if (item == null) {
                    memo.setText(Model.getModel().getDefaultStory());
                    showedImages = new ArrayList<>();
                    showedImages.add(ImgUtils.getDefaultImage());
                    memo.setText(Model.getModel().getDefaultStory());
                    showedImagesPoint = 0;
                    startTraining.setEnabled(false);
                } else {
                    memo.setText(item.getStory());
                    showedImages = ImgUtils.getTrainingImages(item, img.getWidth(), img.getHeight());
                    if (showedImages.size() == 1) {
                        showedImagesPoint = 0;
                    } else {
                        showedImagesPoint = 1;
                    }
                    startTraining.setEnabled(true);
                }
                img.setImageBitmap(showedImages.get(showedImagesPoint));

            }

        });

        startTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Training t = (Training) listview.getItemAtPosition(lastValidPosition);
                //final Training item = (Training) parent.getSelectedItem();
                if (t != null) {
                    Intent i = new Intent(getApplicationContext(), RunTraining.class);

                    List<BasicTime> l = t.getMergedExercises(Model.getModel().getTimeShift()).decompress();
                    l.add(0, Model.getModel().getWarmUp());
                    if (run != null) {
                        run.stop();
                    }
                    run = new MainTimer(l);
                    src = t;

                    startActivity(i);
                }
            }
        });


        memo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    final Training item = (Training) listview.getItemAtPosition(lastValidPosition);
                    //final Training item = (Training) parent.getSelectedItem();
                    if (item != null) {
                        File target = File.createTempFile("android", "cache").getParentFile();
                        File result = item.export(target, new ImgUtils());
                        Intent i = new Intent(getApplicationContext(), BrowserActivityTry.class);
                        lastHtmlFile = result.toURI().toURL().toString();
                        startActivity(i);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;
            }
        });
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://flashbb.cz/aktualne"));
                startActivity(browserIntent);
                return false;
            }
        });
        setLocales();

    }

    private String getAndroidGuiStory() {
        return "\n" + SwingTranslator.R("AndroidInfoLine1")
                + "\n" + SwingTranslator.R("AndroidInfoLine2")
                + "\n" + SwingTranslator.R("AndroidInfoLine3")
                + "\n" + SwingTranslator.R("AndroidInfoLine4")
                + "\n" + SwingTranslator.R("AndroidInfoLine5")
                + "\n" + SwingTranslator.R("AndroidInfoLine6");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_training_selector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Fires after the OnStop() state
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            //needed by fucking sound provider
            //trimCache(this);
            //not cleaning, cache reduced to obe file per soundpack/file/usage
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                deleteDir(new File(dir, children[i]));
            }
        }
    }


    final void setLocales() {
        setTitle(Model.getModel().getTitle());
        startTraining.setText(SwingTranslator.R("StartTraining"));
    }

    public void reloadTrainings() {

        final TypedArray array = getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });
        final ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, Model.getModel().getTraingNames()) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View a = super.getView(position, convertView, parent);
                if (position == lastValidPosition) {
                    a.setBackgroundColor(Color.CYAN);
                } else {
                    a.setBackgroundColor(array.getColor(0, 0xFF00FF));
                }
                return a;
            }

        };
        listview.setAdapter(adapter);
    }
}
