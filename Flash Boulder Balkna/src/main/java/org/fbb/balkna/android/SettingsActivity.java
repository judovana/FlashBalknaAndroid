package org.fbb.balkna.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import org.fbb.balkna.swing.locales.SwingTranslator;

public class SettingsActivity extends AppCompatActivity {


    private CheckBox ratioCheckbox;
    private CheckBox mute;
    private TextView autoIterateLabel;
    private SeekBar auitoiterateSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ratioCheckbox = (CheckBox) findViewById(R.id.ratioCheckbox);
        mute = (CheckBox) findViewById(R.id.mute);
        autoIterateLabel = (TextView) findViewById(R.id.autoIterateLabel);
        auitoiterateSpinner = (SeekBar) findViewById(R.id.auitoiterateSpinner);
        setLocales();

    }

    private void setLocales() {
        mute.setText(SwingTranslator.R("mute"));
        // not localised because of logic
        //startButton.setText(SwingTranslator.R("Start"));
        ratioCheckbox.setText(SwingTranslator.R("ForceRaatio"));
        autoIterateLabel.setText(SwingTranslator.R("Autoiterate"));
        /*languageLabel.setText(SwingTranslator.R("Language"));
        setSoundPackButton.setText(SwingTranslator.R("SetSoundpack"));
        soundPackLabel.setText(SwingTranslator.R("Soundpack"));
        testSoundsButton.setText(SwingTranslator.R("Test"));
        changeLanguageButton.setText(SwingTranslator.R("LanguageChange"));
        tutorialLabel.setText(SwingTranslator.R("TutorialModeLabel"));
        pauseOnExercise.setText(SwingTranslator.R("PauseOnExercise"));
        pauseOnChange.setText(SwingTranslator.R("PauseOnSerie"));
        cheaterLabel.setText(SwingTranslator.R("CheaterSettings"));
        allowSkipping.setText(SwingTranslator.R("Skipping"));
        creditsLabel.setText(SwingTranslator.R("Credits"));
        closeButton.setText(SwingTranslator.R("Close"));
        exportButton.setText(SwingTranslator.R("Export"));
        downloadButton.setText(SwingTranslator.R("Upload"));
        managePluginsButton.setText(SwingTranslator.R("ManagePlugins"));
        exercisesModLabel.setText(SwingTranslator.R("ExerciseModifiers"));
        iterationsModLabel.setText("  - " + SwingTranslator.R("iterationsModifiers"));
        trainingsModLabel.setText("  - " + SwingTranslator.R("TrainingTimesModifier"));
        pausesModLabel.setText("  - " + SwingTranslator.R("PauseTimesModifier"));
        restsModLabel.setText("  - " + SwingTranslator.R("RestTimesModifier"));
        saveForOfline.setText(SwingTranslator.R("SaveForOfline"));*/

    }
}
