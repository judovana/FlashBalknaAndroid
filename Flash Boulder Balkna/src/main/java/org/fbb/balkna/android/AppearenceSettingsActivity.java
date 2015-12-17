package org.fbb.balkna.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.fbb.balkna.Packages;
import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.SoundProvider;
import org.fbb.balkna.model.settings.Settings;
import org.fbb.balkna.swing.locales.SwingTranslator;

import java.net.URL;

public class AppearenceSettingsActivity extends AppCompatActivity {


    private CheckBox ratioCheckbox;
    private CheckBox invert;
    private CheckBox allowLayout;
    private TextView autoIterateLabel;
    private SeekBar auitoiterateSpinner;
    private TextView languageLabel;
    private TextView creditsLabel;
    private Button changeLanguageButton;
    private Button closeButton;
    private Button exportButton;

    private Spinner spinner2;

    private double O5DEL = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appearencesettings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ratioCheckbox = (CheckBox) findViewById(R.id.ratioCheckbox);
        allowLayout = (CheckBox) findViewById(R.id.allowLayout);
        invert = (CheckBox) findViewById(R.id.invert);
        autoIterateLabel = (TextView) findViewById(R.id.autoIterateLabel);
        auitoiterateSpinner = (SeekBar) findViewById(R.id.auitoiterateSpinner);
        languageLabel = (TextView) findViewById(R.id.languageLabel);
        changeLanguageButton = (Button) findViewById(R.id.changeLanguageButton);
        closeButton = (Button) findViewById(R.id.closeButton);
        exportButton = (Button) findViewById(R.id.exportButton);
        creditsLabel = (TextView) findViewById(R.id.creditsLabel);
        spinner2 = (Spinner) findViewById(R.id.spinner2);


        exportButton.setEnabled(false);

        auitoiterateSpinner.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                autoIterateLabel.setText("  - " + SwingTranslator.R("Autoiterate") + " - " + progress);
                Model.getModel().setImagesOnTimerSpeed(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ratioCheckbox.setChecked(Model.getModel().isRatioForced());
        ratioCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.getModel().setRatioForced(ratioCheckbox.isChecked());
                if (Model.getModel().isRatioForced()){
                    TrainingSelector.hack.img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    if (RunTraining.hack!=null) {
                        RunTraining.hack.img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }
                } else {
                    TrainingSelector.hack.img.setScaleType(ImageView.ScaleType.FIT_XY);
                    if (RunTraining.hack!=null) {
                        RunTraining.hack.img.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                }
            }
        });


        allowLayout.setChecked(Settings.getSettings().isAllowScreenChange());
        allowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.getSettings().setAllowScreenChange(allowLayout.isChecked());
            }
        });

        invert.setChecked(Settings.getSettings().isInvertScreenCompress());
        invert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.getSettings().setInvertScreenCompress(invert.isChecked());
            }
        });


        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Packages.LANGUAGES);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter2);
        final int[] keeper2 = new int[1];
        for (int i = 0; i < Packages.LANGUAGES.length; i++) {
            if (Packages.LANGUAGES[i].equals(Model.getModel().getLanguage())) {
                keeper2[0] = i;
                spinner2.clearFocus();
                spinner2.post(new Runnable() {
                    @Override
                    public void run() {
                        spinner2.requestFocusFromTouch();
                        spinner2.setSelection(keeper2[0]);
                        spinner2.requestFocus();
                    }
                });
            }
        }

        changeLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Model.getModel().setLanguage((String) spinner2.getSelectedItem());
                    TrainingSelector.hack.reloadTrainings();
                    SwingTranslator.load((String) spinner2.getSelectedItem());
                    TrainingSelector.hack.setLocales();
                    if (RunTraining.hack != null) {
                        RunTraining.hack.setLocales();
                    }
                    setLocales();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model.getModel().save();
                finish();
            }
        });


        final Context appContext = this.getApplicationContext();
        final AppearenceSettingsActivity self = this;

        TextView info = (TextView) findViewById(R.id.creditsLabel);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://flashbb.cz/aktualne"));
                startActivity(browserIntent);
            }
        });

        auitoiterateSpinner.setProgress(Model.getModel().getImagesOnTimerSpeed());

        setLocales();




        autoIterateLabel.setText("  - " + SwingTranslator.R("Autoiterate") + " - " + Model.getModel().getImagesOnTimerSpeed());

    }

    private void setLocales() {
        ratioCheckbox.setText(SwingTranslator.R("ForceRaatio"));
        autoIterateLabel.setText(SwingTranslator.R("Autoiterate"));
        languageLabel.setText(SwingTranslator.R("Language"));
        changeLanguageButton.setText(SwingTranslator.R("LanguageChange"));
        creditsLabel.setText(SwingTranslator.R("Credits"));
        closeButton.setText(SwingTranslator.R("Close"));
        exportButton.setText(SwingTranslator.R("ExportAndroid"));
        allowLayout.setText(SwingTranslator.R("alowScreenChange"));
        invert.setText(SwingTranslator.R("invertScreenLayout"));
        this.setTitle(SwingTranslator.R("appearenceTab"));

    }
}

