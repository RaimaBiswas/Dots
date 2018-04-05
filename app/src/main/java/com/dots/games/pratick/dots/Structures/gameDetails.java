package com.dots.games.pratick.dots.Structures;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.dots.games.pratick.dots.DotsUI;
import com.dots.games.pratick.dots.Enums.GAME_TYPE;
import com.dots.games.pratick.dots.Enums.PLAYER_TYPE;
import com.dots.games.pratick.dots.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Pratick on 05-06-2015.
 */
public class gameDetails
{

    public static String getPlayerName(int currentPlayer)
    {
        return playerNameList.get(currentPlayer);
    }

    public static void updatePlayerName(String newPlayerName,int currentPlayer)
    {
        gameDetails.playerNameList.set(currentPlayer,newPlayerName);
    }

    public static void addPlayerName(String newPlayerName)
    {
        gameDetails.playerNameList.add(newPlayerName);
    }


    public static int getPlayerBarColor(int currentPlayer)
    {
        return playerBarColorList.get(currentPlayer);
    }

    public static void updatePlayerBarColor(int newPlayerBarColor,int currentPlayer)
    {
        gameDetails.playerBarColorList.set(currentPlayer,newPlayerBarColor);
    }

    public static void addPlayerBarColor(int newPlayerBarColor)
    {
        gameDetails.playerBarColorList.add(newPlayerBarColor);
    }


    public static int getPlayerBoxColor(int currentPlayer)
    {
        return playerBoxColorList.get(currentPlayer);
    }

    public static void updatePlayerBoxColor(int newPlayerBoxColor,int currentPlayer)
    {
        gameDetails.playerBoxColorList.set(currentPlayer,newPlayerBoxColor);
    }

    public static void addPlayerBoxColor(int newPlayerBoxColor)
    {
        gameDetails.playerBoxColorList.add(newPlayerBoxColor);
    }


    public static int getPlayerScore(int currentPlayer)
    {
        return playerScoreList.get(currentPlayer);
    }

    public static void updatePlayerScore(int newPlayerScore,int currentPlayer)
    {
        gameDetails.playerScoreList.set(currentPlayer,newPlayerScore);
    }

    public static void addPlayerScore(int newPlayerScore)
    {
        gameDetails.playerScoreList.add(newPlayerScore);
    }


    public static PLAYER_TYPE getPlayerType(int currentPlayer)
    {
        return playerTypeList.get(currentPlayer);
    }

    public static void updatePlayerType(PLAYER_TYPE newPlayerType,int currentPlayer)
    {
        gameDetails.playerTypeList.set(currentPlayer,newPlayerType);
    }

    public static void addPlayerType(PLAYER_TYPE newPlayerType)
    {
        gameDetails.playerTypeList.add(newPlayerType);
    }

    public static int getPreviewBarColor()
    {
        return previewBarColor;
    }

    public static void setPreviewBarColor(int previewBarColorList)
    {
        gameDetails.previewBarColor = previewBarColorList;
    }

    public static int getUnfilledBarColor()
    {
        return unfilledBarColor;
    }

    public static int getUnfilledBoxColor()
    {
        return unfilledBoxColor;
    }

    public static int getDotRowCount()
    {
        return dotRowCount;
    }

    public static void setDotRowCount(int dotRowCount)
    {
        gameDetails.dotRowCount = dotRowCount;
    }

    public static int getDotColCount()
    {
        return dotColCount;
    }

    public static void setDotColCount(int dotColCount)
    {
        gameDetails.dotColCount = dotColCount;
    }

    public static int getTotalScore()
    {
        return totalScore;
    }

    public static void setTotalScore(int totalScore)
    {
        gameDetails.totalScore = totalScore;
    }

    public static int getNumberOfPlayers()
    {
        return numberOfPlayers;
    }

    private static int dotRowCount;
    private static int dotColCount;
    private static GAME_TYPE gameType;
    private static int previewBarColor;
    private static int unfilledBarColor;
    private static int unfilledBoxColor;
    private static int numberOfPlayers;
    private static int totalScore;
    private static DotsUI activity;
    private static Typeface appTypeface;
    private static Typeface monoTypeface;

    private static ArrayList<String>      playerNameList      = new ArrayList();
    private static ArrayList<Integer>     playerBarColorList  = new ArrayList();
    private static ArrayList<Integer>     playerBoxColorList  = new ArrayList();
    private static ArrayList<PLAYER_TYPE> playerTypeList      = new ArrayList();
    private static ArrayList<Integer>     playerScoreList     = new ArrayList();


    public static void initialize(DotsUI activity)
    {
        gameDetails.activity=activity;
        appTypeface=Typeface.createFromAsset(activity.getAssets(), "font/RobotoCondensed-BoldItalic.ttf");
        monoTypeface=Typeface.createFromAsset(activity.getAssets(), "font/UbuntuMono-RI.ttf");
        dotRowCount=6;
        dotColCount=6;
        gameType=GAME_TYPE.SINGLE_PLAYER;
        previewBarColor  = 0xFFFF9800;
        unfilledBarColor = 0xFFFFFFFF;
        unfilledBoxColor = 0xFFFFFFFF;
        numberOfPlayers=6;
        totalScore=0;
    }

    public static void destroy()
    {
        gameDetails.activity=null;
        appTypeface=null;
        monoTypeface=null;
        dotRowCount=0;
        dotColCount=0;
        gameType=null;
        previewBarColor  = 0;
        unfilledBarColor = 0;
        unfilledBoxColor = 0;
        numberOfPlayers=0;
        totalScore=0;

    }

    public static void reset()
    {
        for (int i=0;i<playerScoreList.size();i++)
        {
            playerScoreList.set(i,0);
        }
        totalScore=0;
    }

    public static boolean isGameOver(int safeUnsafeCount)
    {
        if(safeUnsafeCount==-1)
        {
            return (totalScore==(dotRowCount -1)*(dotColCount -1));
        }
        else
        {
            return (totalScore+safeUnsafeCount==(dotRowCount -1)*(dotColCount -1));
        }
    }

    public static void updatePlayerScore(int currentPlayer)
    {
        playerScoreList.set(currentPlayer,playerScoreList.get(currentPlayer)+1);
        totalScore++;
    }

    public static void setGameType(GAME_TYPE currentGameType)
    {
        gameDetails.gameType=currentGameType;
        initializeGameDetails();
    }
    public static GAME_TYPE getGameType()
    {
        return gameType;
    }

    private static void clearLists()
    {
        playerNameList.clear();
        playerBarColorList.clear();
        playerBoxColorList.clear();
        playerScoreList.clear();
        playerTypeList.clear();
    }

    final static float BRIGHTNESS_RATIO=0.75f;
    
    private static void addListItemGroup(String playerName,PLAYER_TYPE playerType)
    {
        playerNameList.add(playerName);
        int r=(new Random()).nextInt(255);
        int g=(new Random()).nextInt(255);
        int b=(new Random()).nextInt(255);
        playerBarColorList.add(Color.rgb(r, g, b));
        playerBoxColorList.add(Color.rgb(Math.max((int) (r * BRIGHTNESS_RATIO), 0), Math.max((int) (g * BRIGHTNESS_RATIO), 0), Math.max((int) (b * BRIGHTNESS_RATIO), 0)));
        playerScoreList.add(0);
        playerTypeList.add(playerType);
    }

    private static void removeListItemGroup(int itemLocation)
    {

        playerNameList.remove(itemLocation);
        playerBarColorList.remove(itemLocation);
        playerBoxColorList.remove(itemLocation);
        playerScoreList.remove(itemLocation);
        playerTypeList.remove(itemLocation);
    }

    public static void initializeGameDetails()
    {
        Log.d("Game TYPE",gameType.name());

        if (gameType == GAME_TYPE.SINGLE_PLAYER)
        {
            Log.d("Game TYPE","S");

            clearLists();
            gameDetails.numberOfPlayers = 2;

            if((new Random()).nextInt(2)==0)
            {
                addListItemGroup("Human", PLAYER_TYPE.HUMAN);
                addListItemGroup("AI", PLAYER_TYPE.TITAN);
            }
            else
            {
                addListItemGroup("AI", PLAYER_TYPE.TITAN);
                addListItemGroup("Human", PLAYER_TYPE.HUMAN);
            }
        }
        else if (gameType == GAME_TYPE.MULTI_PLAYER)
        {
            Log.d("Game TYPE","MUL");

            clearLists();
            numberOfPlayers=(new Random()).nextInt(8)+2;
            for(int i=0;i<numberOfPlayers;i++)
            {
                addListItemGroup("Human"+(i+1),PLAYER_TYPE.HUMAN);
            }
        }
        else if(gameType == GAME_TYPE.MIXED)
        {

            Log.d("Game TYPE","MIX");

            clearLists();
            gameDetails.numberOfPlayers=(new Random()).nextInt(8)+2;

            int humanCnt=1;
            int aiCnt=1;

            Random random=new Random();

            for (int i=0;i<numberOfPlayers;i++)
            {
                PLAYER_TYPE playerType=PLAYER_TYPE.values()[random.nextInt(5)];

                if(playerType==PLAYER_TYPE.HUMAN)
                {
                    addListItemGroup("HUMAN"+humanCnt++,PLAYER_TYPE.HUMAN);
                }
                else
                {
                    addListItemGroup("AI"+aiCnt++,playerType);
                }
                Log.d("Game Details",playerType.name());
            }
        }

    }

    public static void updateGameDetails(PLAYER_TYPE[] playerTypes)
    {
        switch (gameType)
        {
            case MIXED:
            {
                clearLists();
                gameDetails.numberOfPlayers=playerTypes.length;

                int humanCnt=1;
                int aiCnt=1;

                for (int i=0;i<numberOfPlayers;i++)
                {
                    if(playerTypes[i]==PLAYER_TYPE.HUMAN)
                    {
                        addListItemGroup("HUMAN"+humanCnt++,PLAYER_TYPE.HUMAN);
                    }
                    else
                    {
                        addListItemGroup("AI"+aiCnt++,playerTypes[i]);
                    }
                    Log.d("Game Details",playerTypes[i].name());
                }
                break;
            }
            case MULTI_PLAYER:
            {
                clearLists();
                gameDetails.numberOfPlayers=playerTypes.length;

                for (int i=0;i<numberOfPlayers;i++)
                {
                    addListItemGroup("HUMAN"+(i+1),PLAYER_TYPE.HUMAN);
                    Log.d("Game Details", playerTypes[i].name());
                }
                break;
            }
        }
    }

    public static Typeface getAppTypeface() {
        return appTypeface;
    }

    public static Typeface getMonoTypeface() {
        return monoTypeface;
    }
}
