package com.west.samplewidget;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//https://github.com/commonsguy/cw-advandroid/blob/master/AppWidget/LoremWidget/src/com/commonsware/android/appwidget/lorem/LoremViewsFactory.java
//https://laaptu.wordpress.com/2013/07/19/android-app-widget-with-listview/


public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    /*private static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events.CALENDAR_ID,         // 0
            CalendarContract.Events.TITLE,               // 1
            CalendarContract.Events.DESCRIPTION,         // 2
            CalendarContract.Events.DTSTART,             // 3
            CalendarContract.Events.DTEND                // 4
    };*/


    private static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Instances.CALENDAR_ID,         // 0
            CalendarContract.Instances.TITLE,               // 1
            CalendarContract.Instances.DESCRIPTION,         // 2
            CalendarContract.Instances.BEGIN,               // 3
            CalendarContract.Instances.END                  // 4
    };


    /* The indices for the projection array above. */

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_TITLE_INDEX = 1;
    private static final int PROJECTION_DESCRIPTION_INDEX = 2;
    private static final int PROJECTION_START_TIME_INDEX = 3;
    private static final int PROJECTION_END_TIME_INDEX = 4;

    private static final String[] items = {"One",
                                           "Two",
                                           "Three",
                                           "Four",
                                           "Five"};

    private ArrayList<ListItem> listItemList = new ArrayList<>();
    private Context context = null;
    private int appWidgetId;


    ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem();
    }

    private void populateListItem() {
        /*for (int i = 0; i < 5; i++) {
            ListItem listItem = new ListItem();
            listItem.setHeading("Header " + (i + 1));
            listItem.setContent("This is the content: " + items[i]);
            listItemList.add(listItem);
        }*/

        if (context.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

            // Run query
            Cursor cur = null;
            ContentResolver cr = context.getContentResolver();
            //Uri uri = CalendarContract.Instances.CONTENT_URI;

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DATE);

            calendar.add(Calendar.DATE, 10);

            int year2 = calendar.get(Calendar.YEAR);
            int month2 = calendar.get(Calendar.MONTH);
            int date2 = calendar.get(Calendar.DATE);

            Calendar c_start= Calendar.getInstance();
            c_start.set(year,month,date,0,0); //Note that months start from 0 (January)
            Calendar c_end= Calendar.getInstance();
            c_end.set(year2,month2,date2,0,0); //Note that months start from 0 (January)

            Uri.Builder uriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(uriBuilder, c_start.getTimeInMillis());
            ContentUris.appendId(uriBuilder, c_end.getTimeInMillis());
            Uri uri = uriBuilder.build();

            String selection = CalendarContract.Instances.BEGIN + " >= " + c_start.getTimeInMillis() + " and " + CalendarContract.Instances.BEGIN + " <= " + c_end.getTimeInMillis() + " and " + CalendarContract.Instances.VISIBLE + " = 1";

            // Submit the query and get a Cursor object back.
            cur = cr.query(uri, EVENT_PROJECTION, selection, null, null);

            while (cur.moveToNext()) {

                //TODO: Determine if the event has a conference code and then display it
                //TODO: Display something if there are no events in next ten days
                //TODO: Get the layout to match the spec

                long calID = 0;
                String displayTitle = null;
                String displayDesc = null;
                String displayStartTime = null;
                String displayEndTime = null;

                // Get the field values
                calID = cur.getLong(PROJECTION_ID_INDEX);
                displayTitle = cur.getString(PROJECTION_TITLE_INDEX);
                displayDesc = cur.getString(PROJECTION_DESCRIPTION_INDEX);
                displayStartTime = cur.getString(PROJECTION_START_TIME_INDEX);
                displayEndTime = cur.getString(PROJECTION_END_TIME_INDEX);

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

                final Date startTime = new Date(Long.parseLong(displayStartTime));
                final String formattedStart = sdf.format(startTime);

                final Date endTime = new Date(Long.parseLong(displayEndTime));
                final String formattedEnd = sdf.format(endTime);

                // Do something with the values...
                //TODO: These need to be ordered by date and time
                ListItem listItem = new ListItem();
                listItem.setHeading(displayTitle);
                listItem.setContent(formattedStart + " - " + formattedEnd);
                listItemList.add(listItem);
            }

            cur.close();
        }

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {

        final RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.widget_row);

        ListItem listItem = listItemList.get(position);
        row.setTextViewText(R.id.heading, listItem.getHeading());
        row.setTextViewText(R.id.time, listItem.getContent());

        Intent i = new Intent();
        Bundle extras = new Bundle();

        extras.putString(SampleWidgetProvider.EXTRA_WORD, listItem.getContent());
        extras.putBoolean(SampleWidgetProvider.FROM_WIDGET, true);
        i.putExtras(extras);

        row.setOnClickFillInIntent(R.id.imageView, i);

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}
