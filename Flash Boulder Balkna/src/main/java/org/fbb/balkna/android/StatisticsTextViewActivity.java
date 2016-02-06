package org.fbb.balkna.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.primitives.Cycles;
import org.fbb.balkna.model.primitives.Exercises;
import org.fbb.balkna.model.primitives.Trainings;
import org.fbb.balkna.model.primitives.history.Record;
import org.fbb.balkna.model.primitives.history.RecordWithOrigin;
import org.fbb.balkna.model.settings.Settings;
import org.fbb.balkna.swing.locales.SwingTranslator;

import java.io.File;
import java.util.List;

public class StatisticsTextViewActivity extends AppCompatActivity {


    ListView statsView;
    CheckBox exCheck;
    CheckBox trCheck;
    CheckBox cycCheck;
    CheckBox messages;

    Button exDel;
    Button trDel;
    Button cycDel;
    Button nextGraphs;

    int lastValid = -1;

    StatisticsTextViewActivity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.activity_statistics_text_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        statsView = (ListView) findViewById(R.id.statsView);
        exCheck = (CheckBox) findViewById(R.id.exCheck);
        trCheck = (CheckBox) findViewById(R.id.trCheck);
        cycCheck = (CheckBox) findViewById(R.id.cycCheck);
        messages = (CheckBox) findViewById(R.id.messagesShow);
        exDel = (Button) findViewById(R.id.exDelete);
        trDel = (Button) findViewById(R.id.trDelete);
        cycDel = (Button) findViewById(R.id.cycDelete);
        nextGraphs= (Button) findViewById(R.id.nextGraphs);

        exCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadStats();
            }
        });
        trCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadStats();
            }
        });
        cycCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadStats();
            }
        });

        exDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File[] ff = Exercises.getStatsDir().listFiles();
                for (File f : ff) {
                    f.delete();
                }
                reloadStats();
            }
        });

        trDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File[] ff = Trainings.getStatsDir().listFiles();
                for (File f : ff) {
                    f.delete();
                }
                reloadStats();
            }
        });

        cycDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(self);
                        // set title
                        alertDialogBuilder.setTitle("!!!No!Nie!Nein!Ne!!!");
                        alertDialogBuilder.setCancelable(true);
                        // set dialog message
                        alertDialogBuilder
                                .setMessage(SwingTranslator.R("CylesClearWarning"));

                        alertDialogBuilder
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        File[] ff = Cycles.getStatsDir().listFiles();
                                        {
                                            for (File f : ff) {
                                                f.delete();
                                            }
                                            reloadStats();
                                        }
                                    }
                                });
                        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        });


        nextGraphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(self);
                        // set title
                        alertDialogBuilder.setTitle("!!!Yes!Jo!Ja!ANo!!!");
                        alertDialogBuilder.setCancelable(true);
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Comming soon! Will be in next release!");
                       AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } catch (Exception eex) {
                    eex.printStackTrace();
                }


            }
        });

        statsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastValid = position;
                ((ArrayAdapter) (statsView.getAdapter())).notifyDataSetChanged();
            }
        });

        messages.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Record.SHOW_MESSAGE = messages.isChecked();
                int i = lastValid;
                reloadStats();
                if (i >= 0) {
                    lastValid = i;
                    statsView.smoothScrollToPosition(lastValid);
                }
            }
        });

        reloadStats();
        setLocales();
    }

    private void reloadStats() {
        lastValid = -1;
        final TypedArray array = getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });

        List<RecordWithOrigin> data = Model.getModel().gatherStatistics(exCheck.isChecked(), trCheck.isChecked(), cycCheck.isChecked());
        final ArrayAdapter<String> adapter1 = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, data) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View a = super.getView(position, convertView, parent);
                if (position == lastValid) {
                    if (Settings.getSettings().getSelectedItemColor()!=null) {
                        a.setBackgroundColor(ImgUtils.javaColorToAndroidColor(Settings.getSettings().getSelectedItemColor()));
                    }
                } else {
                    a.setBackgroundColor(array.getColor(0, 0xFF00FF));
                }
                return a;
            }

        };
        statsView.setAdapter(adapter1);
    }

    private void setLocales() {
        this.setTitle(SwingTranslator.R("statsTab"));

        exCheck.setText(SwingTranslator.R("mainTabExercise"));
        trCheck.setText(SwingTranslator.R("mainTabTrainings"));
        cycCheck.setText(SwingTranslator.R("mainTabCycles"));
        nextGraphs.setText(SwingTranslator.R("Next"));
        exDel.setText(SwingTranslator.R("delete"));
        trDel.setText(SwingTranslator.R("delete"));
        cycDel.setText(SwingTranslator.R("delete"));
        messages.setText(SwingTranslator.R("messages"));

    }

}
