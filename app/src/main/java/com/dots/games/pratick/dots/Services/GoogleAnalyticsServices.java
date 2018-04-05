package com.dots.games.pratick.dots.Services;

import android.app.Activity;

import com.dots.games.pratick.dots.DotsUI;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Pratick on 11-07-2015.
 */
public class GoogleAnalyticsServices
{
    Activity activity;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public GoogleAnalyticsServices()
    {
        activity=null;
        initializeAnalytics(activity);
        initializeTrackers();
    }
    public GoogleAnalyticsServices(Activity activity)
    {
        this.activity=activity;
        initializeAnalytics(activity);
        initializeTrackers();
    }

    private void initializeAnalytics(Activity activity)
    {
        analytics = GoogleAnalytics.getInstance(activity);
        analytics.setLocalDispatchPeriod(1800);
    }

    private void initializeTrackers()
    {
        tracker = analytics.newTracker("UA-64439108-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }

    private void sendEventTracker()
    {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(activity.getApplicationContext());
        Tracker tracker = analytics.newTracker("UA-64439108-1"); // Send hits to tracker id UA-XXXX-Y

        // All subsequent hits will be send with screen name = "main screen"

            tracker.setScreenName("main screen");

            tracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("submit")
                .build());

        // Builder parameters can overwrite the screen name set on the tracker.

            tracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("click")
                .setLabel("help popup")
                .build());
    }


}
