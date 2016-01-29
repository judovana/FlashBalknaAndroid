package org.fbb.balkna.android;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.fbb.balkna.Packages;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.SoundProvider;
import org.fbb.balkna.model.primitives.ExerciseOverrides;
import org.fbb.balkna.model.settings.Settings;
import org.fbb.balkna.swing.locales.SwingTranslator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class TrainingSettingsActivity extends AppCompatActivity {


    private long enqueue;
    private DownloadManager dm;

    private CheckBox mute;
    private CheckBox pauseOnExercise;
    private CheckBox pauseOnChange;
    private CheckBox allowSkipping;
    private CheckBox saveForOfline;
    private TextView soundPackLabel;
    private TextView tutorialLabel;
    private TextView cheaterLabel;
    private TextView creditsLabel;
    private TextView exercisesModLabel;
    private TextView iterationsModLabel;
    private Button setSoundPackButton;
    private Button testSoundsButton;
    private Button closeButton;
    private Button exportButton;
    private Button downloadButton;
    private Button managePluginsButton;
    private Button localPlugin;
    private TextView trainingsModLabel;
    private TextView pausesModLabel;
    private TextView restsModLabel;
    private TextView editText1;
    private TextView singleExerciseOverrideLabel;
    private TextView singleExerciseOverrideValidation;
    private EditText singleExerciseOverride;


    private Spinner spinner;

    private double O5DEL = 20;

    private static final int PICKFILE_REQUEST_CODE = 9;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String Fpath = data.getDataString();
            editText1.setText(Fpath);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainingsettings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pauseOnChange = (CheckBox) findViewById(R.id.pauseOnChange);
        pauseOnExercise = (CheckBox) findViewById(R.id.pauseOnExercise);
        allowSkipping = (CheckBox) findViewById(R.id.allowSkipping);
        saveForOfline = (CheckBox) findViewById(R.id.saveForOfline);
        mute = (CheckBox) findViewById(R.id.mute);
        setSoundPackButton = (Button) findViewById(R.id.setSoundPackButton);
        testSoundsButton = (Button) findViewById(R.id.testSoundsButton);
        closeButton = (Button) findViewById(R.id.closeButton);
        exportButton = (Button) findViewById(R.id.exportButton);
        downloadButton = (Button) findViewById(R.id.downloadButton);
        managePluginsButton = (Button) findViewById(R.id.managePluginsButton);
        soundPackLabel = (TextView) findViewById(R.id.soundPackLabel);
        tutorialLabel = (TextView) findViewById(R.id.tutorialLabel);
        cheaterLabel = (TextView) findViewById(R.id.cheaterLabel);
        creditsLabel = (TextView) findViewById(R.id.creditsLabel);
        exercisesModLabel = (TextView) findViewById(R.id.exercisesModLabel);
        iterationsModLabel = (TextView) findViewById(R.id.iterationsModLabel);
        trainingsModLabel = (TextView) findViewById(R.id.trainingsModLabel);
        pausesModLabel = (TextView) findViewById(R.id.pausesModLabel);
        restsModLabel = (TextView) findViewById(R.id.restsModLabel);
        singleExerciseOverrideLabel = (TextView) findViewById(R.id.singleExerciseOverrideLabel);
        singleExerciseOverrideValidation = (TextView) findViewById(R.id.singleExerciseOverrideValidation);
        singleExerciseOverride = (EditText) findViewById(R.id.singleExerciseOverride);
        localPlugin = (Button) findViewById(R.id.selectLocalFile);

        spinner = (Spinner) findViewById(R.id.spinner);


        SeekBar iterationsModLabelSeek = (SeekBar) findViewById(R.id.iterationsModLabelSeek);
        SeekBar trainingsModLabelSeek = (SeekBar) findViewById(R.id.trainingsModLabelSeek);
        SeekBar pausesModLabelSeek = (SeekBar) findViewById(R.id.pausesModLabelSeek);
        SeekBar restsModLabelSeek = (SeekBar) findViewById(R.id.restsModLabelSeek);

        exportButton.setEnabled(false);

        editText1 = (TextView) findViewById(R.id.editText1);
        editText1.setText(Model.getModel().getExamplePluginUrl());
        final TrainingSettingsActivity self = this;

        localPlugin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");
                    intent.putExtra("CONTENT_TYPE", "*/*");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivityForResult(intent, PICKFILE_REQUEST_CODE);
                }catch(Exception ex){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(self);
                    // set title
                    alertDialogBuilder.setTitle("Error");
                    alertDialogBuilder.setCancelable(true);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(ex.getMessage());
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        iterationsModLabelSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                iterationsModLabel.setText("  - " + SwingTranslator.R("iterationsModifiers") + " " + ((double) progress / O5DEL));
                Model.getModel().getTimeShift().setIterations((double) progress / O5DEL);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        trainingsModLabelSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                trainingsModLabel.setText("  - " + SwingTranslator.R("TrainingTimesModifier") + " " + ((double) progress / O5DEL));
                Model.getModel().getTimeShift().setTraining((double) progress / O5DEL);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pausesModLabelSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pausesModLabel.setText("  - " + SwingTranslator.R("PauseTimesModifier") + " " + ((double) progress / O5DEL));
                Model.getModel().getTimeShift().setPause((double) progress / O5DEL);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        restsModLabelSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                restsModLabel.setText("  - " + SwingTranslator.R("RestTimesModifier") + " " + ((double) progress / O5DEL));
                Model.getModel().getTimeShift().setRest((double) progress / O5DEL);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mute.setChecked(!Model.getModel().isLaud());
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.getModel().setLaud(!mute.isChecked());
            }
        });


        allowSkipping.setChecked(Model.getModel().isAllowSkipping());
        allowSkipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.getModel().setAllowSkipping(allowSkipping.isChecked());
            }
        });

        pauseOnChange.setChecked(Model.getModel().isPauseOnExercise());
        pauseOnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.getModel().setPauseOnExercise(pauseOnChange.isChecked());
            }
        });

        pauseOnExercise.setChecked(Model.getModel().isPauseOnExercise());
        pauseOnExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.getModel().setPauseOnExercise(pauseOnExercise.isChecked());
            }
        });

        testSoundsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundProvider.getInstance().test(spinner.getSelectedItem().toString());
            }
        });

        setSoundPackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.getModel().setSoundPack(spinner.getSelectedItem().toString());
            }
        });

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Packages.SOUND_PACKS());
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter1);
        String[] ps = Packages.SOUND_PACKS();
        final int[] keeper1 = new int[1];
        for (int i = 0; i < ps.length; i++) {
            if (ps[i].equals(SoundProvider.getInstance().getUsedSoundPack())) {
                keeper1[0] = i;
                spinner.clearFocus();
                spinner.post(new Runnable() {
                    @Override
                    public void run() {
                        spinner.requestFocusFromTouch();
                        spinner.setSelection(keeper1[0]);
                        spinner.requestFocus();
                    }
                });
            }
        }


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.getModel().save();
                finish();
            }
        });


        final Context appContext = this.getApplicationContext();

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Exception wasError = null;
                try {
//                    Model.getModel().reload(saveForOfline.isChecked(), new URL(editText1.getText().toString()));
//                    TrainingSelector.hack.reloadTrainings();

                    Uri uri = Uri.parse(editText1.getText().toString());
                    dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(
                            uri);
                    request.setTitle(SwingTranslator.R("AndroidDownloadTitle", new File(uri.getPath()).getName()));
                    enqueue = dm.enqueue(request);

                } catch (Exception ex) {
                    wasError = ex;
                    ex.printStackTrace();
                    try {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(self);
                        // set title
                        alertDialogBuilder.setTitle("Error");
                        alertDialogBuilder.setCancelable(true);
                        // set dialog message
                        alertDialogBuilder
                                .setMessage(ex.getMessage());
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } catch (Exception eex) {
                        eex.printStackTrace();
                    }
                }
                if (wasError == null) try {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(self);
                    // set title
                    alertDialogBuilder.setTitle(SwingTranslator.R("AndroidDownloadTitle"));
                    alertDialogBuilder.setCancelable(true);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(SwingTranslator.R("AndroidDownloadMessage", editText1.getText()));
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } catch (Exception eex) {
                    eex.printStackTrace();
                }
            }
        });

        TextView info = (TextView) findViewById(R.id.creditsLabel);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://flashbb.cz/aktualne"));
                startActivity(browserIntent);
            }
        });


        managePluginsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Model.getModel().getPluginsDir().exists() && Model.getModel().getPluginsDir().list().length > 0) {
                    Intent i = new Intent(getApplicationContext(), ManagePlugins.class);
                    startActivity(i);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(self);
                    // set title
                    alertDialogBuilder.setTitle("Error");
                    alertDialogBuilder.setCancelable(true);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("No Plugins!!");
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        singleExerciseOverride.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Settings.getSettings().setSingleExerciseOverride(singleExerciseOverride.getText().toString());
                singleExerciseOverrideValidation.setText(ExerciseOverrides.fakeFromString(Settings.getSettings().getSingleExerciseOverride()).format());
            }
        });

        singleExerciseOverride.setText(Settings.getSettings().getSingleExerciseOverride());

        setLocales();

        trainingsModLabelSeek.setProgress((int) Math.round(Model.getModel().getTimeShift().getTraining() * O5DEL));
        pausesModLabelSeek.setProgress((int) Math.round(Model.getModel().getTimeShift().getPause() * O5DEL));
        restsModLabelSeek.setProgress((int) Math.round((Model.getModel().getTimeShift().getRest() * O5DEL)));
        iterationsModLabelSeek.setProgress((int) Math.round(Model.getModel().getTimeShift().getIterations() * O5DEL));

        setLocales();


        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String nwName = null;
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {
                            Exception wasError = null;
                            try {
                                nwName = c
                                        .getString(c
                                                .getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                            } catch (Exception ex) {
                                wasError = ex;
                                ex.printStackTrace();
                                try {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(self);
                                    // set title
                                    alertDialogBuilder.setTitle("Error");
                                    alertDialogBuilder.setCancelable(true);
                                    // set dialog message
                                    alertDialogBuilder
                                            .setMessage(ex.getMessage());
                                    // create alert dialog
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                } catch (Exception eex) {
                                    eex.printStackTrace();
                                }
                            }
                            if (wasError == null) try {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(self);
                                // set title
                                alertDialogBuilder.setTitle(SwingTranslator.R("AndroidDownloadFinishedTitle"));
                                alertDialogBuilder.setCancelable(true);
                                // set dialog message
                                alertDialogBuilder
                                        .setMessage(SwingTranslator.R("AndroidDownloadFinishedMessage", editText1.getText().toString()));
                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            } catch (Exception eex) {
                                eex.printStackTrace();
                            }

                            try {
                                Model.getModel().reload(saveForOfline.isChecked(), new URL("File://"+nwName));
                                TrainingSelector.hack.reloadTrainings();

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                try {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(self);
                                    // set title
                                    alertDialogBuilder.setTitle("Error");
                                    alertDialogBuilder.setCancelable(true);
                                    // set dialog message
                                    alertDialogBuilder
                                            .setMessage(ex.getMessage());
                                    // create alert dialog
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                } catch (Exception eex) {
                                    eex.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        };

        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private void setLocales() {
        mute.setText(SwingTranslator.R("mute"));
        setSoundPackButton.setText(SwingTranslator.R("SetSoundpack"));
        soundPackLabel.setText(SwingTranslator.R("Soundpack"));
        testSoundsButton.setText(SwingTranslator.R("Test"));
        tutorialLabel.setText(SwingTranslator.R("TutorialModeLabel"));
        pauseOnExercise.setText(SwingTranslator.R("PauseOnExercise"));
        pauseOnChange.setText(SwingTranslator.R("PauseOnSerie"));
        cheaterLabel.setText(SwingTranslator.R("CheaterSettings"));
        allowSkipping.setText(SwingTranslator.R("Skipping"));
        creditsLabel.setText(SwingTranslator.R("Credits"));
        closeButton.setText(SwingTranslator.R("Close"));
        exportButton.setText(SwingTranslator.R("ExportAndroid"));
        downloadButton.setText(SwingTranslator.R("Upload"));
        managePluginsButton.setText(SwingTranslator.R("ManagePlugins"));
        exercisesModLabel.setText(SwingTranslator.R("ExerciseModifiers"));
        iterationsModLabel.setText("  - " + SwingTranslator.R("iterationsModifiers") + " " + Model.getModel().getTimeShift().getIterations());
        trainingsModLabel.setText("  - " + SwingTranslator.R("TrainingTimesModifier") + " " + Model.getModel().getTimeShift().getTraining());
        pausesModLabel.setText("  - " + SwingTranslator.R("PauseTimesModifier") + " " + Model.getModel().getTimeShift().getPause());
        restsModLabel.setText("  - " + SwingTranslator.R("RestTimesModifier") + " " + Model.getModel().getTimeShift().getRest());
        saveForOfline.setText(SwingTranslator.R("SaveForOfline"));
        localPlugin.setText(SwingTranslator.R("localPlugin"));
        singleExerciseOverrideLabel.setText(SwingTranslator.R("singleTrainingOverride"));
        this.setTitle(SwingTranslator.R("settingsTab"));

    }
}

