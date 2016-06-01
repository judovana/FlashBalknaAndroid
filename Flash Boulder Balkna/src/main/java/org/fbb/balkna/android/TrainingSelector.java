package org.fbb.balkna.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.Trainable;
import org.fbb.balkna.model.merged.uncompressed.MainTimer;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BasicTime;
import org.fbb.balkna.model.primitives.Cycle;
import org.fbb.balkna.model.primitives.Exercise;
import org.fbb.balkna.model.primitives.Training;
import org.fbb.balkna.model.primitives.history.StatisticHelper;
import org.fbb.balkna.model.settings.Settings;
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
    public static Trainable mainsrc;
    public static Training runParent;
    public static String lastHtmlFile;

    private OnTouchListenerImpl touch;
    private Button startTraining;
    private Button trainingBack;
    private Button trainingFwd;
    static TrainingSelector hack;
    ListView listview;
    ListView listviewCycles;
    ListView listviewExercises;
    private LinearLayout cyclesInfo;
    private LinearLayout togglButtons;
    private TextView cyclesTrainingInfo;
    ImageView img;
    private Menu menu;
    private EditText memo;
    private ToggleButton exercises;
    private ToggleButton trainings;
    private ToggleButton cycles;


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void selectCycle() {
        final Trainable item = getSelectedTrainable();
        if (item instanceof  Cycle){
            Cycle c = (Cycle) item;
            cyclesTrainingInfo.setText(SwingTranslator.R("trainingCurrent", c.getTrainingPointer() + " - " + c.getTraining().getName()));
            trainingFwd.setEnabled(true);
            trainingBack.setEnabled(true);

            if (c.getTrainingPointer() == 1) {
                startTraining.setText(SwingTranslator.R("StartTraining1"));
            } else if (c.getTrainingPointer() == c.getTrainingOverrides().size()) {
                startTraining.setText(SwingTranslator.R("StartTraining3"));
            } else {
                startTraining.setText(SwingTranslator.R("StartTraining2"));
            }

        }else{

        }
    }

    private Trainable getSelectedTrainable(){
        return getSelectedTrainable(lastValidPosition);
    }
    private Trainable getSelectedTrainable(int position){
        if (trainings.isChecked()){
            ((ArrayAdapter) (listview.getAdapter())).notifyDataSetChanged();
            return  (Training) listview.getItemAtPosition(position);
        } else if (exercises.isChecked()){
            ((ArrayAdapter) (listviewExercises.getAdapter())).notifyDataSetChanged();
            if (position >= 0) {
                return new Training((Exercise) listviewExercises.getItemAtPosition(position));
            } else { return null;}
        }else if (cycles.isChecked()){
            ((ArrayAdapter) (listviewCycles.getAdapter())).notifyDataSetChanged();
            return  (Trainable) listviewCycles.getItemAtPosition(position);
        }else{
            return null;
        }
    }

    private void selectItem(int position) {
        lastValidPosition = position;
        final Trainable item = getSelectedTrainable();
        startTraining.setText(SwingTranslator.R("StartTraining"));
        if (item instanceof  Cycle){
            Cycle c = (Cycle) item;
            selectCycle();
        }
        if (item == null) {
            memo.setText(Model.getModel().getDefaultStory()+getAndroidGuiStory());
            showedImages = new ArrayList<>();
            showedImages.add(ImgUtils.getDefaultImage());
            memo.setText(Model.getModel().getDefaultStory()+getAndroidGuiStory());
            showedImagesPoint = 0;
            startTraining.setEnabled(false);
        } else {
            memo.setText(item.getStory());
            if (!cycles.isChecked()) {
                showedImages = ImgUtils.getTrainingImages(item, img.getWidth(), img.getHeight());
                if (showedImages.size() == 1) {
                    showedImagesPoint = 0;
                } else {
                    showedImagesPoint = 1;
                }
            }
            img.setImageBitmap(showedImages.get(showedImagesPoint));
        }
        startTraining.setEnabled(true);

    }

    private void startTraining() {
        final Trainable t = getSelectedTrainable();
        if (t != null) {
            Cycle c = null;
            if (t instanceof  Cycle){
                c = (Cycle) t;
                selectCycle();
            }
            Intent i = new Intent(getApplicationContext(), RunTraining.class);
            Training training = t.getTraining();
            if (t instanceof  Cycle){
                c = (Cycle) t;
                c.startCyclesTraining();
                selectCycle();
            }
            List<BasicTime> l = training.getMergedExercises(Model.getModel().getTimeShift()).decompress();
            l.add(0, Model.getModel().getWarmUp());
            if (run != null) {
                run.stop();
            }
            run = new MainTimer(l);
            mainsrc = t;
            runParent =  training;
            training.getStatsHelper().started(StatisticHelper.generateMessage(c, (Training) training, (Exercise) null));
            startActivity(i);
        }
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://flashbb.cz/aktualne"));
                startActivity(browserIntent);
            }
        });

        listview = (ListView) findViewById(R.id.listView);
        listviewCycles = (ListView) findViewById(R.id.listViewCycles);
        listviewExercises = (ListView) findViewById(R.id.listViewExercises);
        startTraining = (Button) findViewById(R.id.startTrainingButton);
        trainingFwd = (Button) findViewById(R.id.cyclesPlusPlus);
        trainingBack = (Button) findViewById(R.id.cyclesMinusMinus);
        memo = (EditText) findViewById(R.id.editText);
        cyclesInfo = (LinearLayout) findViewById(R.id.cyclesInfo);
        togglButtons= (LinearLayout) findViewById(R.id.toggleButtons);
        cyclesTrainingInfo = (TextView) findViewById(R.id.cyclesTrainingInfo);
        img = (ImageView) findViewById(R.id.imageView);
        exercises= (ToggleButton) findViewById(R.id.exercises);
        trainings= (ToggleButton) findViewById(R.id.trainings);
        cycles= (ToggleButton) findViewById(R.id.cycles);

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
                selectItem(position);
            }

        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
                startTraining();
                return true;
            }
        });

        listviewExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                selectItem(position);
            }

        });
        listviewExercises.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
                startTraining();
                return true;
            }
        });

        listviewCycles.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                selectItem(position);
            }

        });
        listviewCycles.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
                startTraining();
                return true;
            }
        });
        startTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTraining();
            }
        });

        trainingFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainingForwardActionPerformed();
            }
        });

        trainingBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainingBackActionPerformed();
            }
        });


        memo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    final Trainable item = getSelectedTrainable();
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
        if (Model.getModel().isRatioForced()) {
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            img.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        trainings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainings.setChecked(true);
                exercises.setChecked(false);
                cycles.setChecked(false);
                listviewCycles.setVisibility(View.GONE);
                listviewExercises.setVisibility(View.GONE);
                listview.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                cyclesInfo.setVisibility(View.GONE);
                selectItem(-1);

            }
        });
        exercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainings.setChecked(false);
                exercises.setChecked(true);
                cycles.setChecked(false);
                listviewCycles.setVisibility(View.GONE);
                listview.setVisibility(View.GONE);
                listviewExercises.setVisibility(View.VISIBLE);
                img.setVisibility(View.VISIBLE);
                cyclesInfo.setVisibility(View.GONE);
                selectItem(-1);
            }
        });
        final TrainingSelector self = this;
        cycles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainings.setChecked(false);
                exercises.setChecked(false);
                cycles.setChecked(true);
                listview.setVisibility(View.GONE);
                listviewExercises.setVisibility(View.GONE);
                listviewCycles.setVisibility(View.VISIBLE);
                img.setVisibility(View.GONE);
                cyclesInfo.setVisibility(View.VISIBLE);
                selectItem(-1);
                if (listviewCycles.getCount() <= 0) {
                    try {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(self);
                        //alertDialogBuilder.setTitle("Error");
                        alertDialogBuilder.setCancelable(true);
                        alertDialogBuilder
                                .setMessage(SwingTranslator.R("NoCyclesFound"));
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } catch (Exception eex) {
                        eex.printStackTrace();
                    }
                }
            }
    });
        showHideAdvancedBUttons();
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
        this.menu = menu;
        MenuItem settings = menu.findItem(R.id.action_settings);
        MenuItem appearence = menu.findItem(R.id.action_view);
        MenuItem reset = menu.findItem(R.id.reset_settings);
        MenuItem check= menu.findItem(R.id.checkTest);
        MenuItem statustics= menu.findItem(R.id.statisticsMenuItem);
        statustics.setTitle(SwingTranslator.R("statsTab"));
        check.setTitle(SwingTranslator.R("AndroidAdvanced"));
        reset.setTitle(SwingTranslator.R("resetButton"));
        settings.setTitle(SwingTranslator.R("settingsTab"));
        appearence.setTitle(SwingTranslator.R("appearenceTab"));
        check.setChecked(Settings.getSettings().isAndroidAdvanced());
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
            Intent i = new Intent(getApplicationContext(), TrainingSettingsActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_view) {
            Intent i = new Intent(getApplicationContext(), AppearenceSettingsActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.statisticsMenuItem) {
            Intent i = new Intent(getApplicationContext(), StatisticsTextViewActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.reset_settings) {
            Settings.getSettings().resetDefaults();
            MenuItem check= menu.findItem(R.id.checkTest);
            check.setChecked(Settings.getSettings().isAndroidAdvanced());
            showHideAdvancedBUttons();
            return true;
        }
        if (id == R.id.checkTest) {
            MenuItem check = menu.findItem(R.id.checkTest);
            check.setChecked(!check.isChecked());
            Settings.getSettings().setAndroidAdvanced(check.isChecked());
            Model.getModel().save();
            showHideAdvancedBUttons();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showHideAdvancedBUttons(){
        if (!Settings.getSettings().isAndroidAdvanced()){
         togglButtons.setVisibility(View.GONE);
        } else{
            togglButtons.setVisibility(View.VISIBLE);
        }
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
        if (menu != null) {
            MenuItem settings = menu.findItem(R.id.action_settings);
            MenuItem appearence = menu.findItem(R.id.action_view);
            MenuItem reset = menu.findItem(R.id.reset_settings);
            MenuItem check= menu.findItem(R.id.checkTest);
            MenuItem stats= menu.findItem(R.id.statisticsMenuItem);
            stats.setTitle(SwingTranslator.R("statsTab"));
            check.setTitle(SwingTranslator.R("AndroidAdvanced"));
            reset.setTitle(SwingTranslator.R("resetButton"));
            settings.setTitle(SwingTranslator.R("settingsTab"));
            appearence.setTitle(SwingTranslator.R("appearenceTab"));
            }
        startTraining.setText(SwingTranslator.R("StartTraining"));

        exercises.setText(SwingTranslator.R("mainTabExercise"));
        exercises.setTextOn(SwingTranslator.R("mainTabExercise"));
        exercises.setTextOff(SwingTranslator.R("mainTabExercise"));
        trainings.setText(SwingTranslator.R("mainTabTrainings"));
        trainings.setTextOn(SwingTranslator.R("mainTabTrainings"));
        trainings.setTextOff(SwingTranslator.R("mainTabTrainings"));
        cycles.setText(SwingTranslator.R("mainTabCycles"));
        cycles.setTextOn(SwingTranslator.R("mainTabCycles"));
        cycles.setTextOff(SwingTranslator.R("mainTabCycles"));
        cyclesTrainingInfo.setText(SwingTranslator.R("trainingCurrent", "?"));
        trainingBack.setText(SwingTranslator.R("trainingBack"));
        trainingFwd.setText(SwingTranslator.R("trainingFwd"));
    }

    public void reloadTrainings() {

        final TypedArray array = getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });

        //trainings
        final ArrayAdapter<String> adapter1 = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, Model.getModel().getTraingNames()) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View a = super.getView(position, convertView, parent);
                if (position == lastValidPosition) {
                    if (Settings.getSettings().getSelectedItemColor()!=null) {
                        a.setBackgroundColor(ImgUtils.javaColorToAndroidColor(Settings.getSettings().getSelectedItemColor()));
                    }
                } else {
                    a.setBackgroundColor(array.getColor(0, 0xFF00FF));
                }
                return a;
            }

        };
        if (Settings.getSettings().getTrainingDelimiterSize() != null && Settings.getSettings().getTrainingDelimiterSize() >0) {
            if (Settings.getSettings().getTrainingDelimiterColor() != null) {
                ColorDrawable sage = new ColorDrawable(ImgUtils.javaColorToAndroidColor(Settings.getSettings().getTrainingDelimiterColor()));
                listview.setDivider(sage);
            }
            listview.setDividerHeight(Settings.getSettings().getTrainingDelimiterSize());
        }
        listview.setAdapter(adapter1);

        //exercises
        final ArrayAdapter<String> adapter2 = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, Model.getModel().getExercises()) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View a = super.getView(position, convertView, parent);
                if (position == lastValidPosition) {
                    if (Settings.getSettings().getSelectedItemColor()!=null) {
                        a.setBackgroundColor(ImgUtils.javaColorToAndroidColor(Settings.getSettings().getSelectedItemColor()));
                    }
                } else {
                    a.setBackgroundColor(array.getColor(0, 0xFF00FF));
                }
                return a;
            }

        };
        if (Settings.getSettings().getTrainingDelimiterSize() != null && Settings.getSettings().getTrainingDelimiterSize() >0) {
            if (Settings.getSettings().getTrainingDelimiterColor() != null) {
                ColorDrawable sage = new ColorDrawable(ImgUtils.javaColorToAndroidColor(Settings.getSettings().getTrainingDelimiterColor()));
                listviewExercises.setDivider(sage);
            }
            listviewExercises.setDividerHeight(Settings.getSettings().getTrainingDelimiterSize());
        }
        listviewExercises.setAdapter(adapter2);

        //cycles
        final ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, Model.getModel().getCycles()) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View a = super.getView(position, convertView, parent);
                if (position == lastValidPosition) {
                    if (Settings.getSettings().getSelectedItemColor()!=null) {
                        a.setBackgroundColor(ImgUtils.javaColorToAndroidColor(Settings.getSettings().getSelectedItemColor()));
                    }
                } else {
                    a.setBackgroundColor(array.getColor(0, 0xFF00FF));
                }
                return a;
            }

        };
        if (Settings.getSettings().getTrainingDelimiterSize() != null && Settings.getSettings().getTrainingDelimiterSize() >0) {
            if (Settings.getSettings().getTrainingDelimiterColor() != null) {
                ColorDrawable sage = new ColorDrawable(ImgUtils.javaColorToAndroidColor(Settings.getSettings().getTrainingDelimiterColor()));
                listviewCycles.setDivider(sage);
            }
            listviewCycles.setDividerHeight(Settings.getSettings().getTrainingDelimiterSize());
        }
        listviewCycles.setAdapter(adapter);



    }

    private void mdfc(Cycle c, int i) {
        if (i != c.getTrainingPointer()) {
            c.modified(" pointer changed  from " + i + " to " + c.getTrainingPointer()+". So training "+c.getTraining(i).getName()+" to "+c.getTraining().getName());
        }
    }

    private void trainingBackActionPerformed() {
        // TODO add your handling code here:
        Trainable tc = getSelectedTrainable();
        if (tc instanceof  Cycle) {
            Cycle c = (Cycle) tc;
            int i = c.getTrainingPointer();
            c.decTrainingPointer();
            mdfc(c, i);
            selectCycle();
        }
    }

    private void trainingForwardActionPerformed() {
        Trainable tc = getSelectedTrainable();
        if (tc instanceof  Cycle) {
            Cycle c = (Cycle) tc;
            int i = c.getTrainingPointer();
            c.incTrainingPointer();
            mdfc(c, i);
            selectCycle();
        }
    }
}
