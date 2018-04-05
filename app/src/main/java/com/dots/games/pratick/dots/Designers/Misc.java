package com.dots.games.pratick.dots.Designers;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.dots.games.pratick.dots.Structures.gameDetails;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import javax.xml.datatype.Duration;

/**
 * Created by Pratick on 30-06-2015.
 */
public class Misc
{
    public static int getLongestPlayerNameCount()
    {
        int max=gameDetails.getPlayerName(0).length();
        for (int i=1;i< gameDetails.getNumberOfPlayers();i++)
        {
            if(max<gameDetails.getPlayerName(i).length())
            {
                max=gameDetails.getPlayerName(i).length();
            }
        }
        return max;
    }

    public static String getCorrectedPlayerName(String playerName)
    {
        int length=playerName.length();
        int newLength=getLongestPlayerNameCount();
        for (int i=length;i<newLength;i++)
        {
            playerName+=" ";
        }
        return playerName;


    }

    public static String getAllPlayersNameAndType()
    {
        String text="";
        for(int i=0;i<gameDetails.getNumberOfPlayers();i++)
        {
            text+= getCorrectedPlayerName(gameDetails.getPlayerName(i))+" : "+gameDetails.getPlayerType(i)+"\n";
        }
        return text;
    }

    public static String getAllPlayersNameAndScore()
    {
        String text="";
        for(int i=0;i<gameDetails.getNumberOfPlayers();i++)
        {
            text+= getCorrectedPlayerName(gameDetails.getPlayerName(i))+" : "+gameDetails.getPlayerScore(i)+"\n";
        }
        return text;
    }

    public static void setTextSizeForWidth(final TextView textView, int width)
    {
        String s = textView.getText().toString();
        float currentWidth = 0;
        float textSize=0;

        while(currentWidth <= width)
        {
            Log.d("width", currentWidth + "");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ++textSize);
            currentWidth = textView.getPaint().measureText(s);
        }
    }


}
