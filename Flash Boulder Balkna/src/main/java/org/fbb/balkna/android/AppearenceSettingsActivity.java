package org.fbb.balkna.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
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

    private TextView systemViewLabel;
    private TextView delmiterWidthLabel;
    private SeekBar delimiterWidth;
    private TextView delimiterColorLabel;
    private TextView delimiterColor;
    private TextView selectedColorLabel;
    private TextView selectedColor;
    private TextView mainTimerSizeLabel;
    private SeekBar mainTimerSize;
    private TextView mainTimerColorLabel;
    private TextView mainTimerColor;
    private TextView mainTimerVerticalLabel;
    private Spinner mainTimerVertical;
    private TextView mainTimerHorizontalLabel;
    private Spinner mainTimerHorizontal;

    private Spinner spinner2;

    private double O5DEL = 20;
    private int CALLER=0;

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
        systemViewLabel = (TextView) findViewById(R.id.systemViewLabel);
        delmiterWidthLabel = (TextView) findViewById(R.id.delimiterWidthLabel);
        delimiterWidth = (SeekBar) findViewById(R.id.delimiterWidth);
        delimiterColorLabel = (TextView) findViewById(R.id.delimiterColorLabel);
        delimiterColor = (TextView) findViewById(R.id.delimiterColor);
        selectedColorLabel = (TextView) findViewById(R.id.selectedColorLabel);
        selectedColor = (TextView) findViewById(R.id.selectedColor);
        mainTimerSizeLabel = (TextView) findViewById(R.id.mainTimerSizeLabel);
        mainTimerSize = (SeekBar) findViewById(R.id.mainTimerSize);
        mainTimerColorLabel = (TextView) findViewById(R.id.mainTimerColorLabel);
        mainTimerColor = (TextView) findViewById(R.id.mainTimerColor);
        mainTimerVerticalLabel = (TextView) findViewById(R.id.mainTimerVerticalLabel);
        mainTimerVertical = (Spinner) findViewById(R.id.mainTimerVertical);
        mainTimerHorizontalLabel = (TextView) findViewById(R.id.mainTimerHorizontalLabel);
        mainTimerHorizontal = (Spinner) findViewById(R.id.mainTimerHorizontal);

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
                if (Model.getModel().isRatioForced()) {
                    TrainingSelector.hack.img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    if (RunTraining.hack != null) {
                        RunTraining.hack.img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }
                } else {
                    TrainingSelector.hack.img.setScaleType(ImageView.ScaleType.FIT_XY);
                    if (RunTraining.hack != null) {
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
                RunTraining.rightVisibleSavedState=null;
                RunTraining.bottomSavedState=null;
                RunTraining.isImageInTimerSavedState=null;
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

        ArrayAdapter<String> dataAdapter4 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Settings.HPOSITIONS);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainTimerHorizontal.setAdapter(dataAdapter4);
        final int[] keeper4 = new int[1];
        for (int i = 0; i < Settings.VPOSITIONS.length; i++) {
            if (Settings.HPOSITIONS[i].equals(Settings.getSettings().getMainTimerPositionH())) {
                keeper4[0] = i;
                mainTimerHorizontal.clearFocus();
                mainTimerHorizontal.post(new Runnable() {
                    @Override
                    public void run() {
                        mainTimerHorizontal.requestFocusFromTouch();
                        mainTimerHorizontal.setSelection(keeper4[0]);
                        mainTimerHorizontal.requestFocus();
                    }
                });
            }
        }
        mainTimerHorizontal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Settings.getSettings().setMainTimerPositionH(Settings.HPOSITIONS[position]);
                if (RunTraining.hack != null) {
                    RunTraining.hack.adjustTimrLabel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Settings.VPOSITIONS);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainTimerVertical.setAdapter(dataAdapter3);
        final int[] keeper3 = new int[1];
        for (int i = 0; i < Settings.VPOSITIONS.length; i++) {
            if (Settings.VPOSITIONS[i].equals(Settings.getSettings().getMainTimerPositionV())) {
                keeper3[0] = i;
                mainTimerVertical.clearFocus();
                mainTimerVertical.post(new Runnable() {
                    @Override
                    public void run() {
                        mainTimerVertical.requestFocusFromTouch();
                        mainTimerVertical.setSelection(keeper3[0]);
                        mainTimerVertical.requestFocus();
                    }
                });
            }
        }
        mainTimerVertical.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Settings.getSettings().setMainTimerPositionV(Settings.VPOSITIONS[position]);
                if (RunTraining.hack != null) {
                    RunTraining.hack.adjustTimrLabel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        delimiterWidth.setProgress(Settings.getSettings().getTrainingDelimiterSize());
        delimiterWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Settings.getSettings().setTrainingDelimiterSize(progress);
                delmiterWidthLabel.setText(SwingTranslator.R("trainingDelimiterSizeLabel") + ": " + Settings.getSettings().getTrainingDelimiterSize());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mainTimerSize.setProgress(Settings.getSettings().getMainTimerSize());
        mainTimerSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Settings.getSettings().setMainTimerSize(progress);
                mainTimerSizeLabel.setText(SwingTranslator.R("mainTimerSizeLabel") + ": " + Settings.getSettings().getMainTimerSize());
                if (RunTraining.hack != null) {
                    RunTraining.hack.adjustTimrLabel();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        delimiterColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Settings.getSettings().getTrainingDelimiterColor()!=null) {
                    ColorPicker.pixel = Settings.getSettings().getTrainingDelimiterColor();
                }
                CALLER=1;
                Intent i = new Intent(getApplicationContext(), ColorPicker.class);
                startActivityForResult(i, 0);
                            }
        });
        delimiterColor.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Settings.getSettings().setTrainingDelimiterColor(null);
                setColoredLabel(delimiterColor, Settings.getSettings().getTrainingDelimiterColor());
                return true;
            }
        });

        selectedColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Settings.getSettings().getSelectedItemColor()!=null) {
                    ColorPicker.pixel = Settings.getSettings().getSelectedItemColor();
                }
                CALLER=2;
                Intent i = new Intent(getApplicationContext(), ColorPicker.class);
                startActivityForResult(i,0);

            }
        });
        selectedColor.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Settings.getSettings().setSelectedItemColor(null);
                setColoredLabel(selectedColor, Settings.getSettings().getSelectedItemColor());
                return true;
            }
        });

        mainTimerColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Settings.getSettings().getMainTimerColor()!=null) {
                    ColorPicker.pixel = Settings.getSettings().getMainTimerColor();
                }
                CALLER=3;
                Intent i = new Intent(getApplicationContext(), ColorPicker.class);
                startActivityForResult(i,0);

            }
        });
        mainTimerColor.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Settings.getSettings().setMainTimerColor(null);
                setColoredLabel(mainTimerColor, Settings.getSettings().getMainTimerColor());
                return true;
            }
        });

        setLocales();

        setColoredLabel(mainTimerColor, Settings.getSettings().getMainTimerColor());
        setColoredLabel(selectedColor, Settings.getSettings().getSelectedItemColor());
        setColoredLabel(delimiterColor, Settings.getSettings().getTrainingDelimiterColor());


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

        systemViewLabel.setText(SwingTranslator.R("colorsInfo"));
        delmiterWidthLabel.setText(SwingTranslator.R("trainingDelimiterSizeLabel") + ": " + Settings.getSettings().getTrainingDelimiterSize());
        //delimiterWidth
        delimiterColorLabel.setText(SwingTranslator.R("trainingDelimiterColorLabel"));
        delimiterColor.setText("                                                                                 ");
        selectedColorLabel.setText(SwingTranslator.R("selectedItemColorLabel"));
        selectedColor.setText( "                                                                                ");
        mainTimerSizeLabel.setText(SwingTranslator.R("mainTimerSizeLabel") + ": " + Settings.getSettings().getMainTimerSize());
        //mainTimerSize
        mainTimerColorLabel.setText(SwingTranslator.R("mainTimerColorLabel"));
        mainTimerColor.setText("                                                                                 ");
        mainTimerVerticalLabel.setText(SwingTranslator.R("alowScreenChange"));
        //mainTimerVertical
        mainTimerHorizontalLabel.setText(SwingTranslator.R("alowScreenChange"));
        //mainTimerHorizontal

    }


    private void setColoredLabel(TextView label, Integer javaColor) {
        if (javaColor == null) {
            label.setText("_ _ _ _ System _ _ _ _ _");
            label.setBackground(null);
        } else {
            label.setBackgroundColor(ImgUtils.javaColorToAndroidColor(javaColor));
            label.setText("                                                                                 ");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ColorPicker.pixel==0)  {
            return;
    }
        if (ColorPicker.pixel==-1)  {
            return;
        }
        if (this.CALLER==1){
            Settings.getSettings().setTrainingDelimiterColor(ColorPicker.pixel);
            setColoredLabel(delimiterColor, Settings.getSettings().getTrainingDelimiterColor());
        }

                if (this.CALLER==2) {
        Settings.getSettings().setSelectedItemColor(ColorPicker.pixel);
        setColoredLabel(selectedColor, Settings.getSettings().getSelectedItemColor());
        }


        if (this.CALLER==3) {
        Settings.getSettings().setMainTimerColor(ColorPicker.pixel);
        setColoredLabel(mainTimerColor, Settings.getSettings().getMainTimerColor());
            if (RunTraining.hack!=null){
                RunTraining.hack.adjustTimrLabel();
            }
        }
    }
}

