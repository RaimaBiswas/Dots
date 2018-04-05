package com.dots.games.pratick.dots.Enums;

/**
 * Created by Pratick on 05-06-2015.
 *
 */
public enum PLAYER_TYPE
{
    EASY,MEDIUM,HARD,TITAN,HUMAN;

    public static String getPlayerTypeString(PLAYER_TYPE currentPlayerType)
    {
        switch (currentPlayerType)
        {
            case EASY:
            {
                return "Easy";
            }
            case MEDIUM:
            {
                return "Medium";
            }
            case HARD:
            {
                return "Hard";
            }
            case TITAN:
            {
                return "Titan";
            }
        }
        return null;
    }

    public static PLAYER_TYPE getNextPlayerType(PLAYER_TYPE currentPlayerType)
    {
        switch (currentPlayerType)
        {
            case EASY:
            {
                return MEDIUM;
            }
            case MEDIUM:
            {
                return HARD;
            }
            case HARD:
            {
                return TITAN;
            }
            case TITAN:
            {
                return HUMAN;
            }
            case HUMAN:
            {
                return EASY;
            }
        }
        return null;
    }
}
