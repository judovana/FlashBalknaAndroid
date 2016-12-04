package org.fbb.balkna.android;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.fbb.balkna.model.settings.PluginlistProvider;
import org.fbb.balkna.swing.locales.SwingTranslator;

import java.util.List;

public class ShowKnownPlugins extends AppCompatActivity {

    public static String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_known_plugins);
        setLocales();
        createTable();
    }

    private void createTable() {
        PluginlistProvider.LoadedPlugins data = PluginlistProvider.obtain();

        TableLayout table = new TableLayout(this);

        //table.setStretchAllColumns(true);
        //table.setShrinkAllColumns(true);

        //table tile
        TableRow rowTitle1 = new TableRow(this);
        rowTitle1.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView title1 = new TextView(this);
        title1.setText(data.getSource().getResolution());
        title1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        title1.setGravity(Gravity.CENTER);
        title1.setTypeface(Typeface.SERIF, Typeface.BOLD);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams();
        params1.span = 4;
        rowTitle1.addView(title1, params1);
        table.addView(rowTitle1);

        //table header
        TableRow labelsRow = new TableRow(this);

        TextView stateLabel = new TextView(this);
        stateLabel.setText(SwingTranslator.R("PPstate"));
        stateLabel.setTypeface(Typeface.DEFAULT_BOLD);

        TextView descLabel = new TextView(this);
        descLabel.setText(SwingTranslator.R("PPdesc"));
        descLabel.setTypeface(Typeface.SERIF, Typeface.BOLD);

        TextView hpLabel = new TextView(this);
        hpLabel.setText(SwingTranslator.R("PPhomePage"));
        hpLabel.setTypeface(Typeface.SERIF, Typeface.BOLD);

        TextView linkLabel = new TextView(this);
        linkLabel.setText(SwingTranslator.R("PPlink"));
        linkLabel.setTypeface(Typeface.SERIF, Typeface.BOLD);

        labelsRow.addView(stateLabel);
        labelsRow.addView(descLabel);
        labelsRow.addView(hpLabel);
        labelsRow.addView(linkLabel);
        table.addView(labelsRow);

        //data
        List<PluginlistProvider.ParsedLine> r = data.getResult();
        for (final PluginlistProvider.ParsedLine line : r) {
            TableRow row = new TableRow(this);

            TextView stateColumn = new TextView(this);
            if (line.getState() == PluginlistProvider.PluginState.STABLE) {
                stateColumn.setBackgroundColor(Color.GREEN);
            } else if (line.getState() == PluginlistProvider.PluginState.TESTING) {
                stateColumn.setBackgroundColor(Color.YELLOW);
            } else {
                stateColumn.setBackgroundColor(Color.RED);
            }
            stateColumn.setText(line.getState().getResolution()+" ");
            stateColumn.setTypeface(Typeface.DEFAULT_BOLD);
            row.addView(stateColumn);

            TextView descColumn = new TextView(this);
            descColumn.setText(line.getDescription()+" ");
            row.addView(descColumn);

            TextView homePageColumn = new TextView(this);
            SpannableString content = new SpannableString(line.getHomePage().toString()+" ");
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            homePageColumn.setText(content);
            homePageColumn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(line.getHomePage().toURI().toString()));
                    startActivity(browserIntent);
                    } catch (Exception ex) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShowKnownPlugins.this);
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
            row.addView(homePageColumn);

            TextView useColumn = new Button(this);
            useColumn.setText(SwingTranslator.R("PPuse") + ": " + line.getUrl().toString());
            useColumn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (line.getUrl()!=null) {
                        ShowKnownPlugins.result = line.getUrl().toString();
                    } else {
                        ShowKnownPlugins.result = null;
                    }
                    ShowKnownPlugins.this.finish();
                }
            });
            row.addView(useColumn);

            table.addView(row);
        }

        TableRow rowTitle2 = new TableRow(this);
        rowTitle2.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView title2 = new TextView(this);
        title2.setText(data.getSource().getResolution());
        title2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        title2.setTypeface(Typeface.SERIF, Typeface.BOLD);
        TableRow.LayoutParams params2 = new TableRow.LayoutParams();
        params2.span = 4;
        rowTitle2.addView(title2, params2);
        table.addView(rowTitle2);

        ScrollView sv = new ScrollView(this);
        HorizontalScrollView hsv = new HorizontalScrollView(this);
        hsv.addView(table);
        sv.addView(hsv);
        setContentView(sv);
    }

    private void setLocales() {
        setTitle(SwingTranslator.R("PPkp"));
    }

    public String getDataString(){
        return result;
    }
}
