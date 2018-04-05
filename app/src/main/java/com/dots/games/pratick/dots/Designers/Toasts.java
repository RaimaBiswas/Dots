package com.dots.games.pratick.dots.Designers;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.Gravity;

import com.dots.games.pratick.dots.Structures.gameDetails;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

/**
 * Created by Pratick on 30-06-2015.
 */
public class Toasts
{
    public static final class ToastDurations
    {
        public static final int verySmallDuration=100;
        public static final int smallDuration=500;
        public static final int showScoreDuration=2000;
        public static final int showNetworkConnectionErrorDuration=3000;
    }

    public static final class ToastMessages
    {
        public static final String waitForUpdate ="Wait For it !! \n Feature available in Next Update";
        public static final String waitForSometime ="Wait !!";
        public static final String invalidMove1  ="Invalid Move";
        public static final String invalidMove2  ="Not Happening Bro !!";
        public static final String alreadySelected  ="Already Selected !!";
        public static final String networkConnectionError  ="Sorry, Not Connected to Google Play Games\n Please sign in first.";
    }

    public static final class ToastStyles
    {
        public static final Style whiteFlying=Style.getStyle(Style.WHITE, SuperToast.Animations.FLYIN);
        public static final Style greenFlying=Style.getStyle(Style.GREEN, SuperToast.Animations.FLYIN);
        public static final Style blueFlying=Style.getStyle(Style.BLUE, SuperToast.Animations.FLYIN);
        public static final Style orangeScale=Style.getStyle(Style.ORANGE, SuperToast.Animations.SCALE);
    }

    public static SuperActivityToast appMonoSpacedToast(Activity activity,String toastMessage)
    {
        SuperActivityToast superActivityToast = SuperActivityToast.create(activity, toastMessage, ToastDurations.smallDuration, ToastStyles.whiteFlying);
        superActivityToast.getTextView().setGravity(Gravity.LEFT);
        superActivityToast.getTextView().setTypeface(gameDetails.getMonoTypeface());
        return superActivityToast;
    }

    public static SuperActivityToast appStandardToast(Activity activity,String toastMessage)
    {
        SuperActivityToast superActivityToast = SuperActivityToast.create(activity, toastMessage, ToastDurations.smallDuration, Style.getStyle(Style.WHITE, SuperToast.Animations.FLYIN));
        superActivityToast.getTextView().setTypeface(gameDetails.getAppTypeface());
        return superActivityToast;
    }

    public static void appPresetToast(Activity activity,String toastMessage)
    {
        if(toastMessage.equals(ToastMessages.waitForUpdate))
        {
            SuperActivityToast superActivityToast = SuperActivityToast.create(activity, ToastMessages.waitForUpdate, ToastDurations.smallDuration, ToastStyles.whiteFlying);
            superActivityToast.getTextView().setTypeface(gameDetails.getMonoTypeface());
            superActivityToast.show();
        }
        else if(toastMessage.equals(ToastMessages.waitForSometime))
        {
            SuperActivityToast superActivityToast = SuperActivityToast.create(activity, ToastMessages.waitForSometime, ToastDurations.smallDuration, Toasts.ToastStyles.blueFlying);
            superActivityToast.getTextView().setTypeface(gameDetails.getMonoTypeface());
            superActivityToast.show();
        }
        else if(toastMessage.equals(ToastMessages.invalidMove1))
        {
            SuperActivityToast superActivityToast = SuperActivityToast.create(activity, ToastMessages.invalidMove1, ToastDurations.smallDuration, Toasts.ToastStyles.blueFlying);
            superActivityToast.getTextView().setTypeface(gameDetails.getMonoTypeface());
            superActivityToast.show();
        }
        else if(toastMessage.equals(ToastMessages.invalidMove2))
        {
            SuperActivityToast superActivityToast = SuperActivityToast.create(activity, ToastMessages.invalidMove2, ToastDurations.verySmallDuration, ToastStyles.greenFlying);
            superActivityToast.getTextView().setTypeface(gameDetails.getMonoTypeface());
            superActivityToast.show();
        }
        else if(toastMessage.equals(ToastMessages.alreadySelected))
        {
            SuperActivityToast superActivityToast = SuperActivityToast.create(activity, ToastMessages.alreadySelected, ToastDurations.smallDuration, Toasts.ToastStyles.blueFlying);
            superActivityToast.getTextView().setTypeface(gameDetails.getMonoTypeface());
            superActivityToast.show();
        }
        else if(toastMessage.equals(ToastMessages.networkConnectionError))
        {
            SuperActivityToast superActivityToast = SuperActivityToast.create(activity, ToastMessages.networkConnectionError, ToastDurations.showNetworkConnectionErrorDuration, ToastStyles.orangeScale);
            superActivityToast.getTextView().setTypeface(gameDetails.getAppTypeface());
            superActivityToast.show();
        }

    }

    public static SuperActivityToast appCustomToast(Activity activity,String toastMessage,int duration, Style style,Typeface typeface)
    {
        SuperActivityToast superActivityToast = SuperActivityToast.create(activity, toastMessage, duration, style);
        superActivityToast.getTextView().setTypeface(typeface);
        return superActivityToast;
    }

}
