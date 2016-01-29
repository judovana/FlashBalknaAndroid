package org.fbb.balkna.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.primitives.Cycle;
import org.fbb.balkna.model.settings.Settings;
import org.fbb.balkna.model.merged.uncompressed.MainTimer;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BasicTime;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.BigRestTime;
import org.fbb.balkna.model.merged.uncompressed.timeUnits.PausaTime;
import org.fbb.balkna.model.primitives.Exercise;
import org.fbb.balkna.model.utils.TimeUtils;
import org.fbb.balkna.swing.locales.SwingTranslator;

import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;

public class RunTraining extends AppCompatActivity {

    private  RunTraining self;

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
    private boolean isImageInTimerImpl;
    static Boolean isImageInTimerSavedState;
    static Integer rightVisibleSavedState;
    static Integer bottomSavedState;
    private int timerImageCounter = 0;


    private Button start;
    private Button skip;
    private Button back;
    private TextView timerLabel;
    private Runnable exercseShiftedLIstener;
    private Runnable secondListener;
    ImageView img;

    static RunTraining hack;

    private Menu menu;

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    if (Model.getModel().getImagesOnTimerSpeed() > 0 && isImageInTimer() && showedImages.size() > 1) {
                        Thread.sleep(Model.getModel().getImagesOnTimerSpeed() * 1000);
                        timerImageCounter++;
                        if (timerImageCounter >= showedImages.size()) {
                            timerImageCounter = 0;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timerLabel.setBackground(new BitmapDrawable(getResources(), showedImages.get(timerImageCounter)));
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    });

    public void setIsImageInTimer(boolean isImageInTimer) {
        this.isImageInTimerImpl = isImageInTimer;
        isImageInTimerSavedState = isImageInTimer;
    }

    public boolean isImageInTimer() {
        return isImageInTimerImpl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hack = this;
        self = this;
        t.setDaemon(true);
        if (!t.isAlive()) {
            t.start();
        }
        setContentView(R.layout.activity_run_training);

        final TypedArray array = getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final ListView trainingItems = (ListView) findViewById(R.id.trainingItems);
        final EditText memo = (EditText) findViewById(R.id.editText);
        img = (ImageView) findViewById(R.id.imageView);
        timerLabel = (TextView) findViewById(R.id.timer);
        final TextView cross = (TextView) findViewById(R.id.cross);
        final TextView allTime = (TextView) findViewById(R.id.allTime);
        final TextView title = (TextView) findViewById(R.id.title);
        final TextView nextTitle = (TextView) findViewById(R.id.nextTitle);
        final LinearLayout imgAndDesc = (LinearLayout) findViewById(R.id.imgAndDesc);
        final EditText description = (EditText) findViewById(R.id.editText);
        start = (Button) findViewById(R.id.startButton);
        skip = (Button) findViewById(R.id.skipButton);
        back = (Button) findViewById(R.id.backButton);
        setSupportActionBar(toolbar);
        setTitle();
        memo.setKeyListener(null);


        if (!TrainingSelector.run.isStopped()) {
            start.setText(Model.getModel().getPauseTitle());
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, TrainingSelector.run.getSrc()) {


            public View getView(int position, View convertView, ViewGroup parent) {
                View a = createViewFromResource(position, convertView, parent);
                if (a == null) {
                    a = super.getView(position, convertView, parent);
                }
                if (position == TrainingSelector.run.getIndex()) {
                    if (Settings.getSettings().getSelectedItemColor()!=null) {
                        a.setBackgroundColor(ImgUtils.javaColorToAndroidColor(Settings.getSettings().getSelectedItemColor()));
                    }
                } else {
                    a.setBackgroundColor(array.getColor(0, 0xFF00FF));
                }
                return a;
            }

            private View createViewFromResource(int position, View convertView,
                                                ViewGroup parent) {
                View view;
                TextView text;
                view = convertView;
                try {
                    text = (TextView) view;
                } catch (ClassCastException e) {
                    throw new IllegalStateException(
                            "ArrayAdapter requires the resource ID to be a TextView", e);
                }
                Object item = getItem(position);
                if (text != null) {
                    if (item instanceof BasicTime) {
                        text.setText(((BasicTime) item).getHtmlPreview1(false));
                    } else {
                        text.setText(item.toString());
                    }
                }

                return view;
            }

        };
        trainingItems.setAdapter(adapter);


        trainingItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                if (!Settings.getSettings().isAllowScreenChange()){
                    return;
                }
                rightVisibleSavedState=View.GONE;
                trainingItems.setVisibility(View.GONE);
            }

        });

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Settings.getSettings().isAllowScreenChange()){
                    return;
                }
                bottomSavedState=View.GONE;
                imgAndDesc.setVisibility(View.GONE);

            }
        });

        exercseShiftedLIstener = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainTimer model = TrainingSelector.run;

                        trainingItems.setSelection(model.getIndex());
                        ((ArrayAdapter) trainingItems.getAdapter()).notifyDataSetChanged();
                        //trainingItems.smoothScrollToPositionFromTop(model.getIndex(), 0);
                        trainingItems.smoothScrollToPosition(model.getIndex());

                        BasicTime time = model.getCurrent();
                        if (model.isEnded()) {
                            title.setText(time.getEndMssage());
                            showedImages = new ArrayList<>();
                            showedImages.add(ImgUtils.getDefaultImage());
                            showedImagesPoint = 0;
                            img.setImageBitmap(showedImages.get(showedImagesPoint));
                            nextTitle.setText("");
                            setMainBg();

                        } else {
                            time.play();
                            title.setText(time.getInformaiveTitle());
                            if (time instanceof PausaTime) {
                                cross.setVisibility(View.VISIBLE);
                                nextTitle.setText(model.next());
                                BasicTime ntime = model.getNext();
                                Exercise t = ntime.getOriginator().getOriginal();
                                showedImages = ImgUtils.getExerciseImages(t, img.getWidth(), img.getHeight());
                                showedImagesPoint = 0;
                                img.setImageBitmap(showedImages.get(showedImagesPoint));
                                memo.setText(t.getDescription());
                                nextTitle.setText(model.next() + " " + t.getName());
                                setMainBg();
                                if (time instanceof BigRestTime) {
                                    if (Model.getModel().isPauseOnChange() || Model.getModel().isPauseOnExercise()) {
                                        startStop();
                                    }
                                } else {
                                    if (Model.getModel().isPauseOnExercise()) {
                                        startStop();
                                    }
                                }

                            } else {
                                cross.setVisibility(View.INVISIBLE);
                                nextTitle.setText(model.now());
                                Exercise t = time.getOriginator().getOriginal();
                                showedImages = ImgUtils.getExerciseImages(t, img.getWidth(), img.getHeight());
                                showedImagesPoint = 0;
                                img.setImageBitmap(showedImages.get(showedImagesPoint));
                                memo.setText(t.getDescription());
                                nextTitle.setText(model.now() + " " + t.getName());
                                setMainBg();
                            }
                        }
                        adjustTimrLabel(TimeUtils.secondsToMinutes(time.getCurrentValue()));


                    }
                });
            }
        };
        TrainingSelector.run.setExerciseShifted(exercseShiftedLIstener);

        TrainingSelector.run.setOneTenthOfSecondListener(new Runnable() {
            @Override
            public void run() {
                MainTimer model = TrainingSelector.run;
                final String s = TimeUtils.secondsToMinutes(model.getCurrent().getCurrentValue()) + ":" + model.getTenthOfSecond();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adjustTimrLabel(s);
                    }
                });
            }

            ;
        });

        secondListener = new Runnable() {
            @Override
            public void run() {
                MainTimer model = TrainingSelector.run;
                BasicTime c = model.getCurrent();
                c.soundLogicRuntime(model);
                final String s = TimeUtils.secondsToHours(c.getCurrentValue() + model.getFutureTime()) + "/" + TimeUtils.secondsToHours(model.getTotalTime());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allTime.setText(s);
                    }
                });
            }

            ;
        };
        TrainingSelector.run.setSecondListener(secondListener);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });


        img.setOnTouchListener(new OnTouchListenerImpl(img));
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!Settings.getSettings().isAllowScreenChange()){
                    return false;
                }
                timerLabel.setBackground(new BitmapDrawable(getResources(), showedImages.get(showedImagesPoint)));
                setIsImageInTimer(true);
                return false;
            }
        });
        timerLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Settings.getSettings().isAllowScreenChange()){
                    return;
                }
                rightVisibleSavedState=View.VISIBLE;
                bottomSavedState=View.VISIBLE;
                trainingItems.setVisibility(View.VISIBLE);
                imgAndDesc.setVisibility(View.VISIBLE);
            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Settings.getSettings().isAllowScreenChange()){
                    return;
                }
                rightVisibleSavedState=View.VISIBLE;
                bottomSavedState=View.VISIBLE;
                trainingItems.setVisibility(View.VISIBLE);
                imgAndDesc.setVisibility(View.VISIBLE);
            }
        });

        timerLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!Settings.getSettings().isAllowScreenChange()){
                    return false;
                }
                timerLabel.setBackgroundResource(0);
                setIsImageInTimer(false);
                return false;
            }
        });
        cross.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!Settings.getSettings().isAllowScreenChange()){
                    return false;
                }
                timerLabel.setBackgroundResource(0);
                setIsImageInTimer(false);
                return false;
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Model.getModel().isAllowSkipping()) {
                    return;
                }
                boolean was = Model.getModel().isLaud();
                Model.getModel().setLaud(false);
                TrainingSelector.run.skipForward(true);
                runAllListeners();
                Model.getModel().setLaud(was);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Model.getModel().isAllowSkipping()) {
                    return;
                }
                boolean was = Model.getModel().isLaud();
                Model.getModel().setLaud(false);
                TrainingSelector.run.jumpBack(true);
                runAllListeners();
                Model.getModel().setLaud(was);
            }
        });


        if (Model.getModel().isRatioForced()){
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            img.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        setLocales();
        runAllListeners();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        long w = size.x;
        long h = size.y;

        if (
                (w * h < 800l * 600l && !Settings.getSettings().isInvertScreenCompress())
                        ||
                (w * h >= 800l * 600l && Settings.getSettings().isInvertScreenCompress())
                ) {
            trainingItems.setVisibility(View.GONE);
            imgAndDesc.setVisibility(View.GONE);
            if (isImageInTimerSavedState==null) {
                isImageInTimerImpl = true; //not saving state!
            }else{
                setIsImageInTimer(isImageInTimerSavedState);
            }
            setMainBg();
        }else{
            if (isImageInTimerSavedState==null) {
                isImageInTimerImpl = false; //not saving state!
            }else{
                setIsImageInTimer(isImageInTimerSavedState);
            }
            setMainBg();
        }

        if (bottomSavedState!=null){
            imgAndDesc.setVisibility(bottomSavedState);
        }
        if (rightVisibleSavedState!=null){
            trainingItems.setVisibility(rightVisibleSavedState);
        }


    }

    private void adjustTimrLabel(String s){
        adjustTimrLabel();
        timerLabel.setText(s);
    }

    public void adjustTimrLabel(){
        timerLabel.setTextAppearance(this, android.R.style.TextAppearance_Large);
        timerLabel.setTypeface(null, Typeface.BOLD);


        if (Settings.getSettings().getMainTimerColor() != null) {
            timerLabel.setTextColor(ImgUtils.javaColorToAndroidColor(Settings.getSettings().getMainTimerColor()));
        } else {
        }
        if (Settings.getSettings().getMainTimerSize() != null && Settings.getSettings().getMainTimerSize()!=0) {
            timerLabel.setTextSize(Settings.getSettings().getMainTimerSize());
        } else {
        }

        int grav = Gravity.CENTER;

        String v = Settings.getSettings().getMainTimerPositionV();
        if (Settings.VPOS_T.equals(v)){
            grav = Gravity.TOP;
        }
        if (Settings.VPOS_B.equals(v)){
            grav = Gravity.BOTTOM;
        }

        timerLabel.setGravity(grav);
        try {
            int ali = View.TEXT_ALIGNMENT_CENTER;

            String h = Settings.getSettings().getMainTimerPositionH();
            if (Settings.HPOS_L.equals(h)){
                ali = View.TEXT_ALIGNMENT_VIEW_START;
            }
            if (Settings.HPOS_R.equals(h)){
                ali = View.TEXT_ALIGNMENT_TEXT_END;
            }

            timerLabel.setTextAlignment(ali);
        }catch(Exception ex){
            //older api "noSuchMethod"
        }

    }

    private void startStop() {
        if (TrainingSelector.run.isStopped()) {
            start.setText(Model.getModel().getPauseTitle());
            TrainingSelector.run.go();
        } else {
            start.setText(Model.getModel().getContinueTitle());
            TrainingSelector.run.stop();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_training_selector, menu);
        this.menu = menu;
        MenuItem settings = menu.findItem(R.id.action_settings);
        MenuItem appearence = menu.findItem(R.id.action_view);
        MenuItem reset = menu.findItem(R.id.reset_settings);
        MenuItem statustics= menu.findItem(R.id.statisticsMenuItem);
        statustics.setTitle(SwingTranslator.R("statsTab"));
        reset.setTitle(SwingTranslator.R("resetButton"));
        settings.setTitle(SwingTranslator.R("settingsTab"));
        appearence.setTitle(SwingTranslator.R("appearenceTab"));
        return true;
    }


    private void runAllListeners() {
        boolean was = Model.getModel().isLaud();
        Model.getModel().setLaud(false);
        exercseShiftedLIstener.run();
        //oneTenthOfSecondListener.run();
        secondListener.run();
        Model.getModel().setLaud(was);
    }

    public void setMainBg() {
        if (isImageInTimer()) {
            timerImageCounter = showedImagesPoint;
            timerLabel.setBackground(new BitmapDrawable(getResources(), showedImages.get(timerImageCounter)));
        }
    }

    public void setTitle() {
        this.setTitle(TrainingSelector.mainsrc.getName());
        if (TrainingSelector.mainsrc instanceof Cycle){
            this.setTitle(TrainingSelector.runParent.getName()+" - "+TrainingSelector.mainsrc.getName());
        }
    }

    void setLocales() {
        skip.setText(SwingTranslator.R("skipForward"));
        back.setText(SwingTranslator.R("jumpBack"));
        setTitle();
        // not localised because of logic
        //startButton.setText(SwingTranslator.R("Start"));
        if (menu != null) {
            MenuItem settings = menu.findItem(R.id.action_settings);
            MenuItem appearence = menu.findItem(R.id.action_view);
            MenuItem reset = menu.findItem(R.id.reset_settings);
            MenuItem statustics= menu.findItem(R.id.statisticsMenuItem);
            statustics.setTitle(SwingTranslator.R("statsTab"));
            reset.setTitle(SwingTranslator.R("resetButton"));
            settings.setTitle(SwingTranslator.R("settingsTab"));
            appearence.setTitle(SwingTranslator.R("appearenceTab"));
        }
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
        if (id == R.id.reset_settings) {
            Settings.getSettings().resetDefaults();
            return true;
        }
        if (id == R.id.statisticsMenuItem) {
            Intent i = new Intent(getApplicationContext(), StatisticsTextViewActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        try {
            if (TrainingSelector.run.getIndex() == TrainingSelector.run.getSrc().size()-1){
                RunTraining.super.onBackPressed();
                TrainingSelector.run.stop();
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(self);
                // set title
                alertDialogBuilder.setTitle("!!!No!Nie!Nein!Ne!!!");
                alertDialogBuilder.setCancelable(true);
                // set dialog message
                alertDialogBuilder
                        .setMessage(SwingTranslator.R("AndroidBackTraining"));

                alertDialogBuilder
                        .setPositiveButton(SwingTranslator.R("AndroidBackYes"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RunTraining.super.onBackPressed();
                                TrainingSelector.run.stop();
                            }
                        });
                alertDialogBuilder.setNegativeButton(SwingTranslator.R("AndroidBackNo"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        } catch (Exception eex) {
            eex.printStackTrace();
        }


    }


}
