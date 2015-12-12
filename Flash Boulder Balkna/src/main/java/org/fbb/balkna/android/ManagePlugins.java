package org.fbb.balkna.android;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.fbb.balkna.model.Model;
import org.fbb.balkna.model.utils.JavaPluginProvider;
import org.fbb.balkna.swing.locales.SwingTranslator;

import java.io.File;

public class ManagePlugins extends AppCompatActivity {


    private int lastValidPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_plugins);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ListView l = (ListView) findViewById(R.id.listView2);
        final TypedArray array = getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });
        final ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, Model.getModel().getPluginsDir().listFiles()){

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
        l.setAdapter(adapter);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                lastValidPosition = position;
                ((ArrayAdapter) (l.getAdapter())).notifyDataSetChanged();
            }
        });

        final Button b = (Button) findViewById(R.id.button);
                  b.setText(SwingTranslator.R("DeletePlugin"));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastValidPosition>=0){
                    File f = (File) l.getItemAtPosition(lastValidPosition);
                    JavaPluginProvider.getPluginPaths().removePath(f);
                    finish();
                    TrainingSelector.hack.reloadTrainings();
                }
            }
        });

              }

          }
