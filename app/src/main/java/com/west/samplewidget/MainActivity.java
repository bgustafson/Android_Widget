package com.west.samplewidget;

import android.Manifest;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static final String EXTRA_WORD = "com.west.samplewidget.WORD";
    public static final String FROM_WIDGET = "com.west.samplewidget.FROMWIDGET";
    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 9999;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.widget_update_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forceWidgetUpdate();
            }
        });

        forceWidgetUpdate();

        handleIntent();
    }


    private void handleIntent() {

        Intent i = getIntent();

        boolean fromWidget = i.getBooleanExtra(FROM_WIDGET, false);

        if (fromWidget) {
            String word = i.getStringExtra(EXTRA_WORD);
            Toast.makeText(this, word, Toast.LENGTH_LONG).show();
        }
    }


    private void forceWidgetUpdate() {

        if (checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            this.requestPermissions(new String[] { Manifest.permission.READ_CALENDAR },
                    MY_PERMISSIONS_REQUEST_READ_CALENDAR);
        } else {

            Intent intent = new Intent(this, SampleWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
            // since it seems the onUpdate() is only fired on that:
            int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), SampleWidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            sendBroadcast(intent);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
