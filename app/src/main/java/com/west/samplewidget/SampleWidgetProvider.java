package com.west.samplewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;


public class SampleWidgetProvider extends AppWidgetProvider {


    private static final String EXTRA_WORD = "com.west.samplewidget.WORD";
    private static final String FROM_WIDGET = "com.west.samplewidget.FROMWIDGET";


    public SampleWidgetProvider() {
        super();
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        int appWidgetId = appWidgetIds[0];

        Intent svcIntent = new Intent(context, WidgetListService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        widget.setRemoteAdapter(R.id.list, svcIntent);

        Intent clickIntent = new Intent(context, MainActivity.class);
        PendingIntent clickPI = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        widget.setPendingIntentTemplate(R.id.list, clickPI);

        appWidgetManager.updateAppWidget(appWidgetId, widget);
    }
}
